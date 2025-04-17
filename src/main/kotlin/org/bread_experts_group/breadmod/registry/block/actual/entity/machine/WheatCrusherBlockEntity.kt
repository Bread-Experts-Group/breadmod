package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.AbstractTickingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemBearingBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler
import java.math.BigDecimal
import java.util.Optional

class WheatCrusherBlockEntity(
	pos: BlockPos, state: BlockState
) : BreadModRecipeBlockEntity<FluidEnergyInput, WheatCrusherRecipe, WheatCrusherBlockEntity>(
	ModBlockEntityTypes.WHEAT_CRUSHER.get(),
	pos,
	state,
	ModRecipeTypes.WHEAT_CRUSHING.get()
), MenuProvider, ItemBearingBlockEntity, EnergyBearingBlockEntity {
	override val itemHandler: ExpansibleItemHandler = ExpansibleItemHandler(2)
	override val energyHandler: ExpansibleEnergyHandler = ExpansibleEnergyHandler(
		mutableListOf(
			ExpansibleEnergyHandler.ExpansibleCell(BigDecimal.valueOf(100_000))
		)
	)
	private var energyDivision: Int = -1
		set(value) {
			if (field == -1) field = value
		}

	override fun commonTick(
		level: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	) {
		if (this.getItem(0).isEmpty) return
		val powered = state.getValue(BlockStateProperties.POWERED)
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			if (!activeRecipe.itemStillValid(this.getItem(0))) this.resetRecipe(level)
			val recipeTime = activeRecipe.getTime()
			this.energyDivision = activeRecipe.setEnergyDivision()
			if (
				(this.energyDivision < 0) &&
				(this.energyHandler.energyStored + this.energyDivision > this.energyHandler.maxEnergyStored)
			) return@ifPresentOrElse
			val extractEnergy = this.energyHandler.extractEnergy(this.energyDivision, false)

			if (extractEnergy >= this.energyDivision && activeRecipe.canFitItemResult(this.getItem(1))) {
				if (!powered) level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true))
				if (this.progress >= recipeTime) this.finalizeAndReset(activeRecipe, level) else this.progress++
			}
		}, {
			val stack = this.getItem(0)
			val check = this.recipeDial.getRecipeFor(FluidEnergyInput(stack, stack.count), level)

			check.ifPresentOrElse({ present ->
				this.currentRecipe = Optional.of(present.value)
				this.maxProgress = present.value.getTime()
			}, { level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false)) })
		})
	}

	override fun resetRecipe(level: Level) {
		this.energyDivision = -1
		super.resetRecipe(level)
	}

	override fun finalizeRecipe(recipe: WheatCrusherRecipe, level: Level): Boolean {
		val stack = this.getItem(0)
		val assemble = recipe.assemble(FluidEnergyInput(stack, stack.count), level)
		recipe.consumeItems(listOf(this.getItem(0))).forEachIndexed(this::setItem)
		this.setOrGrowItem(1, assemble, assemble.count)
		return true
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		WheatCrusherMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
}