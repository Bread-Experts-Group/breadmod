package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.datagen.tag.ModItemTags
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.AbstractTickingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemBearingBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler
import java.util.Optional

class ToasterBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModRecipeBlockEntity<FluidEnergyInput, ToasterRecipe, ToasterBlockEntity>(
	ModBlockEntityTypes.TOASTER.get(),
	pos,
	state,
	ModRecipeTypes.TOASTING.get()
), ItemBearingBlockEntity {
	override val itemHandler: ExpansibleItemHandler = ExpansibleItemHandler(1)

	override fun commonTick(
		level: Level,
		pos: BlockPos,
		state: BlockState,
		entity: AbstractTickingBlockEntity<*>
	) {
		if (this.getItem(0).`is`(ModItemTags.EXPLODES_IN_TOASTER)) {
			this.maxProgress = 60
			this.progress++
			if (this.progress == 35) level.playSound(null, pos, SoundEvents.TNT_PRIMED, BLOCKS)
			if (this.progress >= 60) {
				level.explode(
					null,
					pos.x.toDouble(),
					pos.y.toDouble(),
					pos.z.toDouble(),
					3f,
					Level.ExplosionInteraction.BLOCK
				)
			}
		} else {
			this.currentRecipe.ifPresentOrElse({ activeRecipe ->
				val stack = this.getItem(0)
				if (!activeRecipe.itemStillValid(stack)) this.resetRecipe(level)
				if (activeRecipe.canFitItemResults(listOf(stack))) {
					val recipeTime = activeRecipe.rTime ?: 0
					if (this.progress >= recipeTime) {
						this.finalizeRecipe(activeRecipe, level)
						this.resetRecipe(level)
						level.playSound(
							null,
							pos,
							SoundEvents.NOTE_BLOCK_BELL.value(),
							BLOCKS,
							0.2f,
							0.8f
						)
						level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.TRIGGERED, false))
					} else this.progress++
				} else this.resetRecipe(level)
			}, {
				val stack = this.getItem(0)
				val check = this.recipeDial.getRecipeFor(FluidEnergyInput(stack, stack.count), level)

				check.ifPresentOrElse({ present ->
					val recipe = present.value
					this.maxProgress = recipe.rTime ?: 0
					this.currentRecipe = Optional.of(recipe)
				}, {
					level.playSound(
						null,
						pos,
						SoundEvents.NOTE_BLOCK_BASS.value(),
						BLOCKS,
						0.2f,
						0.5f
					)
					level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.TRIGGERED, false))
				})
			})
		}
	}

	override fun finalizeRecipe(recipe: ToasterRecipe, level: Level): Boolean {
		val stack = this.getItem(0)
		val assemble = recipe.assemble(FluidEnergyInput(stack, stack.count), level)

		recipe.consumeItems(listOf(stack))
		this.setItem(0, assemble)
		return true
	}
}