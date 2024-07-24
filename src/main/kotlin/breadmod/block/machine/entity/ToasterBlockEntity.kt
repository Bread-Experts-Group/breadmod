package breadmod.block.machine.entity

import breadmod.recipe.fluidEnergy.ToasterRecipe
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities
import java.util.*

class ToasterBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity.Progressive<ToasterBlockEntity, ToasterRecipe>(
    ModBlockEntityTypes.TOASTER.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.TOASTING,
    IndexableItemHandler(listOf(
        2 to StorageDirection.STORE_ONLY
    )) to null,
    listOf(0),
    1 to 1
) {
    private val triggered = BlockStateProperties.TRIGGERED

    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    override fun tick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<ToasterBlockEntity, ToasterRecipe>
    ) {
        val triggeredState = pBlockEntity.blockState.getValue(triggered)
        if(triggeredState) {
            preTick(pLevel, pPos, pState, pBlockEntity)
            // the toaster does NOT like charcoal
            if(getItemHandler()?.getStackInSlot(0)?.`is`(Items.CHARCOAL) == true) {
                maxProgress = 60
                progress ++
                if(progress == 35) pLevel.playSound(null, pPos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS)
                if(progress >= 60) {
                    pLevel.explode(null, pPos.x.toDouble(), pPos.y.toDouble(), pPos.z.toDouble(), 3f, Level.ExplosionInteraction.BLOCK)
                }
            } else {
                currentRecipe.ifPresentOrElse({
                    progress ++
                    recipeTick(pLevel, pPos, pState, pBlockEntity, it)
                    if (progress >= it.time && recipeDone(pLevel, pPos, pState, pBlockEntity, it)) {
                        currentRecipe = Optional.empty()
                        progress = 0; maxProgress = 0
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(triggered, false))
                        pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_BELL.get(), SoundSource.BLOCKS, 0.2f, 0.8f)
                    }
                }, {
                    val recipe = recipeDial.getRecipeFor(craftingManager, pLevel)
                    recipe.ifPresentOrElse({
                        currentRecipe = recipe
                        maxProgress = it.time
                    }, {
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(triggered, false))
                        pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.BLOCKS, 0.2f, 0.5f)
                    })
                })
            }
            postTick(pLevel, pPos, pState, pBlockEntity)
        }
    }

    override fun recipeDone(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<ToasterBlockEntity, ToasterRecipe>,
        recipe: ToasterRecipe
    ): Boolean {
        val itemHandle = getItemHandler() ?: return false
        recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
        recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
        return if(recipe.canFitResults(itemHandle to listOf(0), null)) {
            val assembled = recipe.assembleOutputs(craftingManager, pLevel)
            assembled.first.forEach { stack -> itemHandle[0].let { slot -> if(slot.isEmpty) itemHandle[0] = stack.copy() else slot.grow(stack.count) } }
            true
        } else false
    }

    fun getRenderStack(): ItemStack {
        val item = getItemHandler()
        return if(item?.getStackInSlot(0)?.isEmpty == false) {
            item.getStackInSlot(0)
        } else ItemStack.EMPTY
    }
}