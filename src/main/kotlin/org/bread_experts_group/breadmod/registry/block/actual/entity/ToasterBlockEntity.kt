package org.bread_experts_group.breadmod.registry.block.actual.entity
//class ToasterBlockEntity(
//	pos: BlockPos,
//	state: BlockState
//) : BreadModRecipeBlockEntity<FluidEnergyInput, ToasterRecipe, ToasterBlockEntity>(
//	ModBlockEntityTypes.TOASTER.get(),
//	pos,
//	state,
//	ModRecipeTypes.TOASTING.get()
//), ItemBearingBlockEntity {
//	private companion object {
//		val TRIGGERED: BooleanProperty = BlockStateProperties.TRIGGERED
//	}
//
//	override val itemHandler: ExpansibleItemHandler = ExpansibleItemHandler(1)
//
//	override fun commonRecipeTickPre(level: Level): Boolean =
//		if (this.getItem(0).`is`(EXPLODES_IN_TOASTER) && this.blockState.getValue(Companion.TRIGGERED)) {
//			this.maxProgress = 60
//			this.progress++
//			if (this.progress == 35) level.playSound(null, this.blockPos, SoundEvents.TNT_PRIMED, BLOCKS)
//			if (this.progress == 60) {
//				if (!level.isClientSide) level.explode(
//					null,
//					this.blockPos.x.toDouble(),
//					this.blockPos.y.toDouble(),
//					this.blockPos.z.toDouble(),
//					1f,
//					Level.ExplosionInteraction.BLOCK
//				)
//			}
//			false
//		} else true
//
//	override fun runCurrentRecipe(
//		recipe: ToasterRecipe,
//		level: Level
//	) {
//		if (!recipe.itemStillValid(this.getItem(0))) this.resetRecipe(level)
//		val recipeTime = recipe.getTime()
//
//		if (this.progress >= recipeTime) {
//			this.finalizeAndReset(recipe, level)
//			level.playSound(null, this.blockPos, SoundEvents.NOTE_BLOCK_BELL.value(), BLOCKS, 0.2f, 0.8f)
//			level.setBlockAndUpdate(this.blockPos, this.blockState.setValue(Companion.TRIGGERED, false))
//		} else this.progress++
//	}
//
//	override fun runMissingRecipe(level: Level) {
//		val stack = this.getItem(0)
//		val check = this.getOptionalRecipe(FluidEnergyInput(stack), level)
//
//		if (this.blockState.getValue(Companion.TRIGGERED)) check.ifPresentOrElse({ present ->
//			val recipe = present.value
//			this.maxProgress = recipe.getTime()
//			this.setRecipe(recipe)
//		}, {
//			level.playSound(
//				null,
//				this.blockPos,
//				SoundEvents.NOTE_BLOCK_BASS.value(),
//				BLOCKS,
//				0.2f, 0.5f
//			)
//			level.setBlockAndUpdate(this.blockPos, this.blockState.setValue(Companion.TRIGGERED, false))
//		})
//	}
//
//	override fun checkIsEmpty(level: Level): Boolean = this.getItem(0).isEmpty
//
//	override fun finalizeRecipe(recipe: ToasterRecipe, level: Level): Boolean {
//		val stack = this.getItem(0)
//		val assemble = recipe.assembleItem(FluidEnergyInput(stack), level)
//
//		recipe.consumeItems(listOf(stack))
//		this.setItem(0, assemble)
//		return true
//	}
//}