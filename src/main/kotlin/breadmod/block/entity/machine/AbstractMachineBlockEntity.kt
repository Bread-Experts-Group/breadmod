package breadmod.block.entity.machine

import breadmod.block.machine.CraftingManager
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.clientbound.CapabilitySideDataPacket
import breadmod.network.clientbound.CapabilityTagDataPacket
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import breadmod.util.capability.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.registries.RegistryObject
import java.util.*
import kotlin.math.max

abstract class AbstractMachineBlockEntity<T : AbstractMachineBlockEntity<T>>(
    pType: BlockEntityType<T>,
    pPos: BlockPos,
    pBlockState: BlockState,
    vararg additionalCapabilities: Pair<Capability<*>, CapabilityContainer>
) : BlockEntity(pType, pPos, pBlockState) {
    val capabilityHolder = CapabilityHolder(mapOf(*additionalCapabilities))

    init {
        this.capabilityHolder.capabilities.forEach { (_, u) ->
            u.first.ifPresent { it.changed = { setChanged() } }
        }
        this.capabilityHolder.changed = { cap, _, _ ->
            NETWORK.send(
                PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
                CapabilitySideDataPacket(blockPos, cap.name, capabilityHolder.capabilities[cap]?.second ?: emptyList())
            )
        }

        capabilityHolder.capabilities.forEach {
            (capabilityHolder.capability(it.key) as ICapabilitySavable<*>).changed = ::setChanged
        }
    }

    final override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> =
        getCapability(cap, null)

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
        capabilityHolder.capabilitySided(cap, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING), side)
            ?: super.getCapability(cap, side)

    final override fun invalidateCaps() {
        capabilityHolder.invalidate()
        super.invalidateCaps()
    }

    final override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    final override fun getUpdateTag(): CompoundTag =
        CompoundTag().also { saveAdditional(it) }

    open fun adjustSaveAdditional(pTag: CompoundTag) {}
    final override fun saveAdditional(pTag: CompoundTag) {
        val additionalCompound = CompoundTag()
        adjustSaveAdditional(additionalCompound)
        val breadmodCompound = CompoundTag()
        capabilityHolder.capabilities.forEach { (t, u) ->
            u.first.ifPresent { breadmodCompound.put(t.name, it.serializeNBT()) }
        }

        pTag.put(ADDITIONAL_KEY, additionalCompound)
        pTag.put(BREADMOD_CAPABILITY_KEY, breadmodCompound)
        super.saveAdditional(pTag)
    }

    open fun adjustLoad(pTag: CompoundTag) {}
    final override fun load(pTag: CompoundTag) {
        adjustLoad(pTag)
        adjustLoad(pTag.getCompound(ADDITIONAL_KEY))
        val capabilityData = pTag.getCompound(BREADMOD_CAPABILITY_KEY)
        capabilityHolder.capabilities.forEach { (t, u) ->
            u.first.ifPresent { it.deserializeNBT(capabilityData.get(t.name)) }
        }
        super.load(pTag)
    }

    open fun adjustChanged() {}
    final override fun setChanged() {
        adjustChanged()
        if (level is ServerLevel) NETWORK.send(
            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
            CapabilityTagDataPacket(blockPos, updateTag)
        )
        super.setChanged()
    }

    open fun preTick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: AbstractMachineBlockEntity<T>) {}
    open fun postTick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: AbstractMachineBlockEntity<T>) {}

    open fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: AbstractMachineBlockEntity<T>) {
        preTick(pLevel, pPos, pState, pBlockEntity)
        postTick(pLevel, pPos, pState, pBlockEntity)
    }

    abstract class Progressive<T : AbstractMachineBlockEntity<T>, R : FluidEnergyRecipe>(
        pType: BlockEntityType<T>,
        pPos: BlockPos,
        pBlockState: BlockState,
        private val recipeType: RegistryObject<RecipeType<R>>,
        itemHandler: Pair<IndexableItemHandler, List<Direction?>>,
        itemHandlerViewSlots: List<Int>,
        craftingWidthHeight: Pair<Int, Int>,
        vararg additionalCapabilities: Pair<Capability<*>, CapabilityContainer>
    ) : AbstractMachineBlockEntity<T>(
        pType, pPos, pBlockState,
        ForgeCapabilities.ITEM_HANDLER to itemHandler,
        *additionalCapabilities
    ) {
        @Suppress("LeakingThis")
                /**
                 * Crafting manager for this progressive machine block entity.
                 *
                 * @author Miko Elbrecht
                 * @since 1.0.0
                 */
        val craftingManager: CraftingManager<Progressive<T, R>> = CraftingManager(
            itemHandler.first,
            itemHandlerViewSlots,
            craftingWidthHeight.first, craftingWidthHeight.second,
            this
        )
        protected val recipeDial: RecipeManager.CachedCheck<CraftingContainer, R> by lazy {
            RecipeManager.createCheck(
                recipeType.get()
            )
        }

        var currentRecipe: Optional<R> = Optional.empty()
            protected set
        var progress = 0
            protected set
        var maxProgress = 0
            protected set

        open fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T, R>) {
            preTick(pLevel, pPos, pState, pBlockEntity)
            currentRecipe.ifPresentOrElse({
                progress++
                recipeTick(pLevel, pPos, pState, pBlockEntity, it)
                if (progress >= it.time) {
                    recipeDone(pLevel, pPos, pState, pBlockEntity, it)
                    currentRecipe = Optional.empty()
                    progress = 0; maxProgress = 0
                }
            }, {
                currentRecipe = recipeDial.getRecipeFor(craftingManager, pLevel)
            })
            postTick(pLevel, pPos, pState, pBlockEntity)
        }

        open fun adjustSaveAdditionalProgressive(pTag: CompoundTag) {}
        final override fun adjustSaveAdditional(pTag: CompoundTag) {
            adjustSaveAdditionalProgressive(pTag)
            pTag.putInt(PROGRESS_KEY, progress)
            pTag.putInt(MAX_PROGRESS_KEY, maxProgress)
        }

        open fun adjustLoadProgressive(pTag: CompoundTag) {}
        final override fun adjustLoad(pTag: CompoundTag) {
            adjustLoadProgressive(pTag)
            progress = pTag.getInt(PROGRESS_KEY)
            maxProgress = pTag.getInt(MAX_PROGRESS_KEY)
        }

        open fun noRecipeTick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T, R>) {}
        open fun consumeRecipe(
            pLevel: Level,
            pPos: BlockPos,
            pState: BlockState,
            pBlockEntity: Progressive<T, R>,
            recipe: R
        ): Boolean = true

        open fun recipeTick(
            pLevel: Level,
            pPos: BlockPos,
            pState: BlockState,
            pBlockEntity: Progressive<T, R>,
            recipe: R
        ) {
        }

        open fun recipeDone(
            pLevel: Level,
            pPos: BlockPos,
            pState: BlockState,
            pBlockEntity: Progressive<T, R>,
            recipe: R
        ): Boolean = true

        abstract class Powered<T : AbstractMachineBlockEntity<T>, R : FluidEnergyRecipe>(
            pType: BlockEntityType<T>,
            pPos: BlockPos,
            pBlockState: BlockState,
            recipeType: RegistryObject<RecipeType<R>>,
            itemHandler: Pair<IndexableItemHandler, List<Direction?>>,
            itemHandlerViewSlots: List<Int>,
            craftingWidthHeight: Pair<Int, Int>,
            powerHandler: Pair<EnergyBattery, List<Direction?>>,
            vararg additionalCapabilities: Pair<Capability<*>, CapabilityContainer>
        ) : Progressive<T, R>(
            pType,
            pPos,
            pBlockState,
            recipeType,
            itemHandler,
            itemHandlerViewSlots,
            craftingWidthHeight,
            ForgeCapabilities.ENERGY to powerHandler,
            *additionalCapabilities
        ) {
            open fun recipeTickPrePower(
                pLevel: Level,
                pPos: BlockPos,
                pState: BlockState,
                pBlockEntity: Progressive<T, R>,
                recipe: R
            ) {
            }

            private var energyDivision: Int? = null
            final override fun tick(
                pLevel: Level,
                pPos: BlockPos,
                pState: BlockState,
                pBlockEntity: Progressive<T, R>
            ) {
                preTick(pLevel, pPos, pState, pBlockEntity)
                currentRecipe.ifPresentOrElse({
                    preTick(pLevel, pPos, pState, pBlockEntity)
                    val div = if (energyDivision == null) ((it.energy ?: 0) / max(
                        it.time,
                        1
                    )).also { div -> energyDivision = div } else energyDivision ?: return@ifPresentOrElse

                    recipeTickPrePower(pLevel, pPos, pState, pBlockEntity, it)
                    val cap = capabilityHolder.capability<IEnergyStorage>(ForgeCapabilities.ENERGY)
                    // Suspend "charging" if this entity is full of energy
                    if ((div < 0) && (cap.energyStored + div > cap.maxEnergyStored)) return@ifPresentOrElse
                    val energy = cap.extractEnergy(div, false)

                    if (energy >= div) {
                        if (progress >= it.time && recipeDone(pLevel, pPos, pState, pBlockEntity, it)) {
                            energyDivision = null
                            currentRecipe = Optional.empty()
                            progress = 0; maxProgress = 0
                        } else {
                            progress++
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, true))
                            recipeTick(pLevel, pPos, pState, pBlockEntity, it)
                        }
                    } else progress--
                }, {
                    val recipe = recipeDial.getRecipeFor(craftingManager, pLevel)
                    recipe.ifPresentOrElse({
                        if (consumeRecipe(pLevel, pPos, pState, pBlockEntity, it)) {
                            currentRecipe = recipe
                            maxProgress = it.time
                        }
                    }, {
                        if (pState.getValue(BlockStateProperties.POWERED))
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, false))
                        noRecipeTick(pLevel, pPos, pState, pBlockEntity)
                    })
                })
                postTick(pLevel, pPos, pState, pBlockEntity)
            }
        }

        private companion object {
            const val PROGRESS_KEY = "progress"
            const val MAX_PROGRESS_KEY = "maxProgress"
            //const val RECIPE_KEY = "currentRecipe"
        }
    }

    private companion object {
        const val ADDITIONAL_KEY = "additional"
        const val BREADMOD_CAPABILITY_KEY = "capability"
    }
}