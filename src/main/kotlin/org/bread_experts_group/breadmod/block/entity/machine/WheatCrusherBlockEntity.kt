package org.bread_experts_group.breadmod.block.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.items.ItemStackHandler
import org.bread_experts_group.breadmod.Breadmod.Companion.modTranslatable
import org.bread_experts_group.breadmod.menu.WheatCrusherMenu
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipe.WheatCrusherInput
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*
import kotlin.math.max

class WheatCrusherBlockEntity(
    pos: BlockPos, state: BlockState
) : AbstractMachineBlockEntity<WheatCrusherInput, WheatCrusherRecipe, WheatCrusherBlockEntity>(
    ModBlockEntityTypes.WHEAT_CRUSHER.get(),
    pos, state, 2,
    ModRecipeTypes.WHEAT_CRUSHING.get()
), MenuProvider, WorldlyContainer {
    var progress = 0
    var maxProgress = 0
    private var energyDivision: Int? = null

    val energyHandler: EnergyStorage by lazy {
        object : EnergyStorage(100000) {
            override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
                setChanged()
                return super.receiveEnergy(toReceive, simulate)
            }
        }
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: WheatCrusherBlockEntity) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            val div = if (energyDivision == null) ((activeRecipe.value.recipeEnergy) / max(
                activeRecipe.value.recipeTime,
                1
            )).also { div -> energyDivision = div } else energyDivision ?: return@ifPresentOrElse
            if ((div < 0) && (energyHandler.energyStored + div > energyHandler.maxEnergyStored)) return@ifPresentOrElse

            val energy = energyHandler.extractEnergy(div, false)

            if (energy >= div) {
                if (progress >= activeRecipe.value.recipeTime) {
                    recipeDone(level, activeRecipe.value)
                    currentRecipe = Optional.empty()
                    progress = 0; maxProgress = 0
                } else {
                    progress++
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true))
                }
            }
        }, {
            val recipeChecker = recipeDial.getRecipeFor(
                WheatCrusherInput(getItem(0), getItem(0).count), level
            )
            recipeChecker.ifPresentOrElse({ recipeCheck ->
                currentRecipe = recipeChecker
                maxProgress = recipeCheck.value.recipeTime
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true))
            }, {
                progress = 0; maxProgress = 0
                currentRecipe = Optional.empty()
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false))
            })
        })
    }

    override fun recipeDone(level: Level, recipe: WheatCrusherRecipe) {
        items[0].shrink(recipe.recipeInput.count)
        val assemble =
            recipe.assemble(WheatCrusherInput(recipe.recipeInput, recipe.recipeInput.count), level.registryAccess())
        println(assemble)
        if (items[1].isEmpty) items[1] = assemble.copy() else items[1].grow(assemble.count)
//        setItem(1, assemble)
    }

    override fun adjustSaveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put("energy", energyHandler.serializeNBT(registries))
        tag.put("recipeProgress", CompoundTag().also { progressTag ->
            progressTag.putInt("progress", progress)
            progressTag.putInt("maxProgress", maxProgress)
        })
    }

    override fun adjustLoadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        val progressTag = tag.getCompound("progress")

        energyHandler.deserializeNBT(registries, tag.get("energy") ?: return)
        progress = progressTag.getInt("progress")
        maxProgress = progressTag.getInt("maxProgress")
    }

    // todo WorldlyContainer throws
    //  java.lang.NullPointerException: Parameter specified as non-null is null: method org.bread_experts_group.breadmod.block.entity.machine.WheatCrusherBlockEntity.getSlotsForFace, parameter side
    //  for some reason it only happens when looking directly at the block

    override fun getSlotsForFace(side: Direction): IntArray =
        if (side == Direction.NORTH) intArrayOf(1) else intArrayOf(0)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        if (direction != null) getSlotsForFace(direction).contains(index) else true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
        getSlotsForFace(direction).contains(index)

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        WheatCrusherMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
}