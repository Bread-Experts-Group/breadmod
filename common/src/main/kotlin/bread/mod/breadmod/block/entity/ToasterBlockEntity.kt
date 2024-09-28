package bread.mod.breadmod.block.entity

import bread.mod.breadmod.recipe.toaster.ToasterInput
import bread.mod.breadmod.recipe.toaster.ToasterRecipe
import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.recipe.ModRecipeTypes
import bread.mod.breadmod.util.forge_handlers.CapabilityTypes
import bread.mod.breadmod.util.forge_handlers.ForgeCapableObject
import bread.mod.breadmod.util.forge_handlers.energy.ArchEnergyStorage
import bread.mod.breadmod.util.forge_handlers.fluid.ArchFluidStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.Optional

class ToasterBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : BlockEntity(ModBlockEntityTypes.TOASTER.get(), pos, state), CraftingContainer, ForgeCapableObject {
    var currentRecipe: Optional<RecipeHolder<ToasterRecipe>> = Optional.empty()
    var items: NonNullList<ItemStack> = NonNullList.withSize(1, ItemStack.EMPTY)
    private val triggered = BlockStateProperties.TRIGGERED

    var progress = 0
    var maxProgress = 0

    val recipeDial: RecipeManager.CachedCheck<ToasterInput, ToasterRecipe> by lazy {
        RecipeManager.createCheck(ModRecipeTypes.TOASTING.get())
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: ToasterBlockEntity) {
        val triggeredState = state.getValue(triggered)
        if (triggeredState) {
            if (items[0].`is`(Items.CHARCOAL)) {
                maxProgress = 60
                progress++
                if (progress == 35) level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS)
                if (progress >= 60) {
                    level.explode(
                        null,
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble(),
                        3f,
                        Level.ExplosionInteraction.BLOCK
                    )
                }
            }
            currentRecipe.ifPresentOrElse({ activeRecipe ->
                progress++
                if (progress >= activeRecipe.value.time) {
                    recipeDone(level, activeRecipe.value)
                    currentRecipe = Optional.empty()
                    progress = 0; maxProgress = 0
                    level.setBlockAndUpdate(pos, state.setValue(triggered, false))
                    level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 0.2f, 0.8f)
                }
            }, { // check for the recipe
                val recipe = recipeDial.getRecipeFor(
                    ToasterInput(getItem(0), getItem(0).count), level
                )
                recipe.ifPresentOrElse({ recipeCheck ->
                    currentRecipe = recipe
                    maxProgress = recipeCheck.value.time
                }, {
                    progress = 0; maxProgress = 0
                    currentRecipe = Optional.empty()
                    level.setBlockAndUpdate(pos, state.setValue(triggered, false))
                    level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 0.2f, 0.5f)
                })
            })
        }
    }

    private fun recipeDone(
        level: Level,
        recipe: ToasterRecipe
    ) {
        items[0].shrink(recipe.inputItem.count)
        val assemble = recipe.assemble(ToasterInput(getItem(0), getItem(0).count), level.registryAccess())
        setItem(0, assemble)
    }

    fun getRenderStack(): ItemStack =
        if (!items[0].isEmpty) {
            items[0]
        } else ItemStack.EMPTY

    val energyHandler: ArchEnergyStorage by lazy {
        object : ArchEnergyStorage(100000) {
            override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
                setChanged()
                return super.receiveEnergy(toReceive, simulate)
            }
        }
    }

    val fluidHandler: ArchFluidStorage by lazy {
        object : ArchFluidStorage(10000) {
            override fun onContentsChanged() {
                super.onContentsChanged()
                setChanged()
            }
        }
    }

    override fun addCapability(): Map<CapabilityTypes, Any?> = buildMap {
        this[CapabilityTypes.ENERGY] = energyHandler
        this[CapabilityTypes.FLUID] = fluidHandler
    }

    // todo find out why fluid saving and loading is just busted
    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("energy", CompoundTag().also { energyTag ->
            energyTag.putInt("energyStored", energyHandler.energy)
            energyTag.putInt("maxEnergyStored", energyHandler.capacity)
        })
        tag.put("recipeProgress", CompoundTag().also { progressTag ->
            progressTag.putInt("progress", progress)
            progressTag.putInt("maxProgress", maxProgress)
        })
//        tag.put("fluid", CompoundTag().also { fluidTag ->
//            if (fluid.fluid != Fluids.EMPTY) {
////                println("WRITE FLUID: ${FluidStackHooks.write(registries, fluid, fluidTag)}")
////                FluidStackHooks.write(registries, fluid, fluidTag)
//            }
//            fluidTag.putInt("fluidCapacity", fluidCapacity)
//            println(fluidTag)
//        })
//        if (fluid.fluid != Fluids.EMPTY) {
//            tag.put("fluid", serializeFluid(registries))
//        }
        ContainerHelper.saveAllItems(tag, items, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        val energyTag = tag.getCompound("energy")
        val progressTag = tag.getCompound("progress")
//        val fluidTag = tag.getCompound("fluid")

        energyHandler.energy = energyTag.getInt("energyStored")
        energyHandler.capacity = energyTag.getInt("maxEnergyStored")
        progress = progressTag.getInt("progress")
        maxProgress = progressTag.getInt("maxProgress")
//        deserializeFluid(registries, tag)
//        println("FLUID TAG: $fluidTag")
//        try {
//            println("READ RAW FLUID: ${FluidStackHooks.readOptional(registries, fluidTag)}")
//            println(
//                "FLUID TYPE READ: ${FluidStackHooks.readOptional(registries, fluidTag).fluid.`arch$registryName`()}"
//            )
//        } catch (e: Exception) {
//            println(e)
//        }
//        if (fluid.fluid != Fluids.EMPTY) {
//            println(
//                "FLUID HOOK READ: ${FluidStackHooks.readOptional(registries, fluidTag).fluid.`arch$registryName`()}"
//            )
//            fluid = FluidStackHooks.readOptional(registries, fluidTag)
//        }
//        fluidCapacity = fluidTag.getInt("fluidCapacity")
        items = NonNullList.withSize(containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, items, registries)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        super.getUpdateTag(registries).also { saveAdditional(it, registries) }

    override fun getContainerSize(): Int = 1
    override fun isEmpty(): Boolean = items.count() == 0
    override fun getItem(slot: Int): ItemStack = items[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack =
        items[slot].split(amount) ?: ItemStack.EMPTY

    override fun removeItemNoUpdate(slot: Int): ItemStack =
        items[slot].copyAndClear() ?: ItemStack.EMPTY

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        setChanged()
    }

    override fun getMaxStackSize(): Int = 2

    override fun setChanged() = super.setChanged()

    override fun stillValid(player: Player): Boolean =
        Container.stillValidBlockEntity(this, player)

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): List<ItemStack> = items
    override fun clearContent() = items.clear()

    override fun fillStackedContents(contents: StackedContents) {
        for (stack: ItemStack in items) {
            contents.accountSimpleStack(stack)
        }
    }
}
