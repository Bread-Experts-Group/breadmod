package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import org.bread_experts_group.breadmod.datagen.tag.EXPLODES_IN_TOASTER
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemBearingBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler

class ToasterBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModRecipeBlockEntity<FluidEnergyInput, ToasterRecipe, ToasterBlockEntity>(
	ModBlockEntityTypes.TOASTER.get(),
	pos,
	state,
	ModRecipeTypes.TOASTING.get()
), ItemBearingBlockEntity {
	private companion object {
		val TRIGGERED: BooleanProperty = BlockStateProperties.TRIGGERED
	}

	override val itemHandler: ExpansibleItemHandler = ExpansibleItemHandler(1, this)

	override fun commonTick(
		level: Level,
		pos: BlockPos,
		state: BlockState,
		entity: ToasterBlockEntity
	) {
		if (this.getItem(0).`is`(EXPLODES_IN_TOASTER) && state.getValue(Companion.TRIGGERED)) {
			this.maxProgress = 60
			this.progress++
			if (this.progress == 35) level.playSound(null, pos, SoundEvents.TNT_PRIMED, BLOCKS)
			if (this.progress == 60) {
				if (!level.isClientSide) level.explode(
					null,
					pos.x.toDouble(),
					pos.y.toDouble(),
					pos.z.toDouble(),
					1f,
					Level.ExplosionInteraction.BLOCK
				)
			}
		} else super.commonTick(level, pos, state, entity)
	}

	override fun runCurrentRecipe(
		recipe: ToasterRecipe,
		level: Level,
		pos: BlockPos,
		state: BlockState,
		entity: ToasterBlockEntity
	) {
		if (!recipe.itemStillValid(this.getItem(0))) this.resetRecipe(level)
		val recipeTime = recipe.getTime()

		if (this.progress >= recipeTime) {
			this.finalizeAndReset(recipe, level)
			level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BELL.value(), BLOCKS, 0.2f, 0.8f)
			level.setBlockAndUpdate(pos, state.setValue(Companion.TRIGGERED, false))
		} else this.progress++
	}

	override fun runMissingRecipe(level: Level, pos: BlockPos, state: BlockState, entity: ToasterBlockEntity) {
		val stack = this.getItem(0)
		val check = this.getOptionalRecipe(FluidEnergyInput(stack), level)

		if (state.getValue(Companion.TRIGGERED)) check.ifPresentOrElse({ present ->
			val recipe = present.value
			this.maxProgress = recipe.getTime()
			this.setRecipe(recipe)
		}, {
			level.playSound(
				null,
				pos,
				SoundEvents.NOTE_BLOCK_BASS.value(),
				BLOCKS,
				0.2f,
				0.5f
			)
			level.setBlockAndUpdate(pos, state.setValue(Companion.TRIGGERED, false))
		})
	}

	override fun checkIsEmpty(level: Level): Boolean = this.getItem(0).isEmpty

	override fun finalizeRecipe(recipe: ToasterRecipe, level: Level): Boolean {
		val stack = this.getItem(0)
		val assemble = recipe.assembleItem(FluidEnergyInput(stack), level)

		recipe.consumeItems(listOf(stack))
		this.setItem(0, assemble)
		return true
	}
}