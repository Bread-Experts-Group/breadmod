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
import org.bread_experts_group.breadmod.util.handlers.ExtendedItemStackHandler
import java.math.BigDecimal
import java.util.Optional
import kotlin.math.max

class WheatCrusherBlockEntity(
	pos: BlockPos, state: BlockState
) : BreadModRecipeBlockEntity<FluidEnergyInput, WheatCrusherRecipe, WheatCrusherBlockEntity>(
	ModBlockEntityTypes.WHEAT_CRUSHER.get(),
	pos,
	state,
	ModRecipeTypes.WHEAT_CRUSHING.get()
), MenuProvider, ItemBearingBlockEntity, EnergyBearingBlockEntity {
	override val itemHandler: ExtendedItemStackHandler = ExtendedItemStackHandler(2)
	override val energyHandler: ExpansibleEnergyHandler = ExpansibleEnergyHandler(
		listOf(
			ExpansibleEnergyHandler.ExpansibleCell(BigDecimal.valueOf(100000))
		)
	)
	private var energyDivision: Int = -1

	override fun commonTick(
		clientLevel: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			if (!activeRecipe.itemsStillValid(listOf(this.getItem(0)))) this.resetRecipe()
			val recipeTime = activeRecipe.rTime ?: 0
			if (this.energyDivision == -1) {
				this.energyDivision = (activeRecipe.rEnergy ?: 0) / max(
					recipeTime,
					1
				)
			}
			if (
				(this.energyDivision < 0) &&
				(this.energyHandler.energyStored + this.energyDivision > this.energyHandler.maxEnergyStored)
			) return@ifPresentOrElse
			val extractEnergy = this.energyHandler.extractEnergy(this.energyDivision, false)

			if (
				extractEnergy >= this.energyDivision &&
				activeRecipe.canFitItemResults(listOf(this.getItem(1)))
			) {
				clientLevel.setBlockAndUpdate(
					pos,
					state.setValue(BlockStateProperties.POWERED, true)
				)
				if (this.progress >= recipeTime) {
					this.finalizeRecipe(activeRecipe, clientLevel)
					this.resetRecipe()
				} else this.progress++
			}
		}, {
			val inputList = listOf(this.getItem(0))
			val check = this.recipeDial.getRecipeFor(
				FluidEnergyInput(
					inputList,
					listOf(inputList[0].count),
					listOf(),
					listOf()
				), clientLevel
			)

			check.ifPresent { present ->
				val recipe = present.value
				this.currentRecipe = Optional.of(recipe)
				this.maxProgress = recipe.rTime ?: 0
			}
			clientLevel.setBlockAndUpdate(
				pos,
				state.setValue(BlockStateProperties.POWERED, false)
			)
		})
	}

	override fun resetRecipe() {
		this.level?.setBlockAndUpdate(this.blockPos, this.blockState.setValue(BlockStateProperties.POWERED, false))
		this.energyDivision = -1
		super.resetRecipe()
	}

	override fun finalizeRecipe(recipe: WheatCrusherRecipe, level: Level) {
		val inputList = listOf(this.getItem(0))
		val assemble = recipe.assemble(
			FluidEnergyInput(
				inputList,
				listOf(inputList[0].count),
				listOf(),
				listOf()
			), level.registryAccess()
		)

		recipe.consumeItems(inputList)
		if (this.getItem(1).isEmpty) this.setItem(1, assemble) else
			this.growItem(1, assemble.count)
	}

	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
		WheatCrusherMenu(containerId, playerInventory, this)

	override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
}