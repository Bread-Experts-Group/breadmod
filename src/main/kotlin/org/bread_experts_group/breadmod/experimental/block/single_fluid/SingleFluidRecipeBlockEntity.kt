package org.bread_experts_group.breadmod.experimental.block.single_fluid

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import org.bread_experts_group.breadmod.experimental.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class SingleFluidRecipeBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModBlockEntityTypes.SINGLE_FLUID_TEST.get(), pos, state), MenuProvider {
    var progress = 0
    var maxProgress = 0

    var currentRecipe: Optional<SingleFluidTestRecipe> = Optional.empty()
    val recipeDial: RecipeManager.CachedCheck<BMRecipeInputs.SingleFluid, SingleFluidTestRecipe> by lazy {
        RecipeManager.createCheck(ModRecipeTypes.SINGLE_FLUID.get())
    }

    // todo needs a custom FluidTank impl to allow setting specific tanks
    val tank: FluidTank by lazy {
        object : FluidTank(10000) {
            override fun onContentsChanged() {
                syncToClients()
            }

            override fun getTanks(): Int = 1
        }
    }

    fun resetRecipe() {
        currentRecipe = Optional.empty()
        maxProgress = 0; progress = 0
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            if (!activeRecipe.inputStillValid(tank.getFluidInTank(0))) resetRecipe()
            progress++
            if (progress >= maxProgress) {
                recipeDone(activeRecipe)
                resetRecipe()
            }
        }, {
            val check = recipeDial.getRecipeFor(
                BMRecipeInputs.SingleFluid(
                    tank.getFluidInTank(0),
                    tank.getFluidInTank(0).amount,
                    0
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                val recipeTime = recipe.rTime ?: 0
                currentRecipe = Optional.of(recipe)
                maxProgress = recipeTime
            }
        })
    }

    fun recipeDone(recipe: SingleFluidTestRecipe) {
        val assemble = recipe.assembleFluid(
            BMRecipeInputs.SingleFluid(
                tank.getFluidInTank(0),
                tank.getFluidInTank(0).amount,
                0
            )
        )
        if (tank.getFluidInTank(0).isEmpty) tank.fluid =
            assemble.copyWithAmount(recipe.rFluidOutput.amount) else tank.getFluidInTank(0).amount += recipe.rFluidOutput.amount
    }

    private fun syncToClients() = level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("fluid", CompoundTag().also { tank.writeToNBT(registries, it) })
        tag.putInt("progress", progress)
        tag.putInt("maxProgress", maxProgress)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        tank.readFromNBT(registries, tag.getCompound("fluid"))
        progress = tag.getInt("progress")
        maxProgress = tag.getInt("maxProgress")
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        super.getUpdateTag(registries).also { saveAdditional(it, registries) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        SingleFluidRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("SingleFluidRecipe")

}