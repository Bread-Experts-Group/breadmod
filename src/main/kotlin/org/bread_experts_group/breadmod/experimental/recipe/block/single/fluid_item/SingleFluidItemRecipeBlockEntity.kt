package org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid_item

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.experimental.fluid_tank.CustomFluidTank
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidItemRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*

class SingleFluidItemRecipeBlockEntity(
	pos: BlockPos,
	state: BlockState
) : AbstractTestItemRecipeBlockEntity<BMRecipeInputs.SingleFluidItem, SingleFluidItemRecipe>(
	pos,
	state,
	ModBlockEntityTypes.SINGLE_FLUID_ITEM_TEST.get(),
	ModRecipeTypes.SINGLE_FLUID_ITEM.get(),
	2
) {
	val tank: CustomFluidTank by lazy {
		object : CustomFluidTank(10000, 2) {
			override fun onContentsChanged() {
				this@SingleFluidItemRecipeBlockEntity.syncToClients()
			}
		}
	}

	override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.saveAdditional(tag, registries)

		tag.put("fluid", CompoundTag().also { this.tank.writeToNBT(registries, it) })
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.loadAdditional(tag, registries)

		this.tank.readFromNBT(registries, tag.getCompound("fluid"))
	}

	override fun tick(level: Level, tPos: BlockPos, tState: BlockState) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			if (!activeRecipe.inputStillValid(
					this.items[0],
					this.tank.getFluid(0)
				)
			) this.resetRecipe()
			val recipeTime = activeRecipe.rTime ?: 0
			this.progress++
			if (this.progress >= recipeTime) {
				this.finalizeRecipe(activeRecipe, level)
				this.resetRecipe()
			}
		}, {
			val check = this.recipeDial.getRecipeFor(
				BMRecipeInputs.SingleFluidItem(
					this.items[0],
					this.items[0].count,
					this.tank.getFluid(0),
					this.tank.getFluid(0).amount, 1
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				val recipeTime = recipe.rTime ?: 0
				if (!recipe.canFitResults(
						this.tank,
						1,
						this.items,
						1
					)
				) return@ifPresent
				this.currentRecipe = Optional.of(recipe)
				this.maxProgress = recipeTime
			}
		})
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		SingleFluidItemRecipeMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = Component.literal("SingleFluidItemRecipe")
	override fun getWidth(): Int = 1
	override fun getHeight(): Int = 1
	override fun finalizeRecipe(recipe: SingleFluidItemRecipe, level: Level) {
		val assemble = recipe.assembleOutputs(
			BMRecipeInputs.SingleFluidItem(
				this.items[0],
				this.items[0].count,
				this.tank.getFluid(0),
				this.tank.getFluid(0).amount, 1
			)
		)
		if (this.itemSlots[1].isEmpty) this.itemSlots[1] = assemble.second.copyWithCount(recipe.rItemOutput.count)
		else this.itemSlots[1].grow(recipe.rItemOutput.count)
		if (this.tank.getFluid(1).isEmpty) this.tank.setFluidInTank(
			1,
			assemble.first.copyWithAmount(recipe.rFluidOutput.amount)
		) else this.tank.getFluid(1).grow(recipe.rFluidOutput.amount)
		recipe.consumeInputs(this.tank, 0, this.items, 0)
	}
}