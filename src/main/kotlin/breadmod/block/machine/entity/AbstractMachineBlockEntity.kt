package breadmod.block.machine.entity

import breadmod.network.CapabilityDataPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import breadmod.util.capability.CapabilityContainer
import breadmod.util.capability.CapabilityHolder
import breadmod.util.capability.ICapabilitySavable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
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
import java.util.*
import kotlin.math.max

abstract class AbstractMachineBlockEntity<T: AbstractMachineBlockEntity<T>>(
    pType: BlockEntityType<T>,
    pPos: BlockPos,
    pBlockState: BlockState,
    vararg additionalCapabilities: Pair<Capability<*>, CapabilityContainer>
): BlockEntity(pType, pPos, pBlockState) {
    val capabilityHolder = CapabilityHolder(mapOf(*additionalCapabilities))

    final override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> =
        getCapability(cap, null)
    final override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T>
        = capabilityHolder.capabilitySided(cap, /*blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)*/ Direction.NORTH, side) ?: super.getCapability(cap, side)
    final override fun invalidateCaps() {
        capabilityHolder.invalidate()
        super.invalidateCaps()
    }

    open fun adjustSaveAdditional(pTag: CompoundTag) {}
    final override fun saveAdditional(pTag: CompoundTag) {
        adjustSaveAdditional(pTag)
        super.saveAdditional(pTag)
    }

    open fun adjustLoad(pTag: CompoundTag) {}
    final override fun load(pTag: CompoundTag) {
        adjustLoad(pTag)
        super.load(pTag)
    }

    open fun adjustChanged() {}
    final override fun setChanged() = super.setChanged().also {
        adjustChanged()
        if(level is ServerLevel) NETWORK.send(
            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
            CapabilityDataPacket(blockPos, updateTag)
        )
    }

    init {
        capabilityHolder.capabilities.forEach {
            (capabilityHolder.capability(it.key) as ICapabilitySavable<*>).changed = ::setChanged
        }
    }

    abstract class Powered<T: AbstractMachineBlockEntity<T>>(
        pType: BlockEntityType<T>,
        pPos: BlockPos,
        pBlockState: BlockState,
        powerHandler: CapabilityContainer,
        vararg additionalCapabilities: Pair<Capability<*>, CapabilityContainer>
    ): AbstractMachineBlockEntity<T>(pType, pPos, pBlockState, ForgeCapabilities.ENERGY to powerHandler, *additionalCapabilities)

    abstract class Progressive<T: AbstractMachineBlockEntity<T>, R: FluidEnergyRecipe>(
        pType: BlockEntityType<T>,
        pPos: BlockPos,
        pBlockState: BlockState,
        recipeType: RecipeType<R>,
        vararg additionalCapabilities: Pair<Capability<*>, CapabilityContainer>
    ): AbstractMachineBlockEntity<T>(pType, pPos, pBlockState, *additionalCapabilities), CraftingContainer {
        protected val recipeDial: RecipeManager.CachedCheck<CraftingContainer, R> = RecipeManager.createCheck(recipeType)
        var currentRecipe: Optional<R> = Optional.empty()
            protected set
        var progress = 0
            protected set

        open fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T,R>) = currentRecipe.ifPresentOrElse({
            progress++
            recipeTick(pLevel, pPos, pState, pBlockEntity, it)
            if(progress > it.time) {
                recipeDone(pLevel, pPos, pState, pBlockEntity, it)
                currentRecipe = Optional.empty()
            }
        }, {
            val sLevel = level
            if(sLevel != null) currentRecipe = recipeDial.getRecipeFor(this, sLevel)
        })

        open fun adjustSaveAdditionalProgressive(pTag: CompoundTag) {}
        final override fun adjustSaveAdditional(pTag: CompoundTag) {
            adjustSaveAdditionalProgressive(pTag)
            currentRecipe.ifPresent { pTag.putInt(PROGRESS_KEY, progress) }
        }

        open fun adjustLoadProgressive(pTag: CompoundTag) {}
        final override fun adjustLoad(pTag: CompoundTag) {
            adjustLoadProgressive(pTag)
            currentRecipe.ifPresent { progress = pTag.getInt(PROGRESS_KEY) } // TODO RVW
        }

        open fun noRecipeTick (pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T,R>) {}
        open fun consumeRecipe(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T, R>, recipe: R): Boolean = true
        open fun recipeTick   (pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T,R>, recipe: R) {}
        open fun recipeDone   (pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T,R>, recipe: R): Boolean = true

        abstract class Powered<T: AbstractMachineBlockEntity<T>, R: FluidEnergyRecipe>(
            pType: BlockEntityType<T>,
            pPos: BlockPos,
            pBlockState: BlockState,
            recipeType: RecipeType<R>,
            powerHandler: CapabilityContainer,
            vararg additionalCapabilities: Pair<Capability<*>, CapabilityContainer>
        ): Progressive<T,R>(pType, pPos, pBlockState, recipeType, ForgeCapabilities.ENERGY to powerHandler, *additionalCapabilities), CraftingContainer {
            private var energyDivision: Int? = null
            final override fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Progressive<T,R>) = currentRecipe.ifPresentOrElse({
                val div = if(energyDivision == null) ((it.energy ?: 0) / max(it.time, 1)).also { div -> energyDivision = div } else energyDivision!!
                val energy = capabilityHolder.capability<IEnergyStorage>(ForgeCapabilities.ENERGY).extractEnergy(div, false)

                if(energy >= div) {
                    if(progress > it.time && recipeDone(pLevel, pPos, pState, pBlockEntity, it)) {
                        energyDivision = null
                        currentRecipe = Optional.empty()
                    } else {
                        progress++
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, true))
                        recipeTick(pLevel, pPos, pState, pBlockEntity, it)
                    }
                } else progress--
            }, {
                val sLevel = level
                if(sLevel != null) {
                    val recipe = recipeDial.getRecipeFor(this, sLevel)
                    recipe.ifPresent {
                        if(consumeRecipe(pLevel, pPos, pState, pBlockEntity, it)) currentRecipe = recipe
                    }
                } else {
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, false))
                    noRecipeTick(pLevel, pPos, pState, pBlockEntity)
                }
            })
        }

        private companion object {
            const val PROGRESS_KEY = "progress"
            //const val RECIPE_KEY = "currentRecipe"
        }
    }
}