package org.bread_experts_group.breadmod.registry.block.actual.entity.machine
//class WheatCrusherBlockEntity(
//	pos: BlockPos, state: BlockState
//) : BreadModRecipeBlockEntity<FluidEnergyInput, WheatCrusherRecipe, WheatCrusherBlockEntity>(
//	ModBlockEntityTypes.WHEAT_CRUSHER.get(),
//	pos,
//	state,
//	ModRecipeTypes.WHEAT_CRUSHING.get()
//), MenuProvider, ItemBearingBlockEntity, EnergyBearingBlockEntity {
//	private companion object {
//		val POWERED: BooleanProperty = BlockStateProperties.POWERED
//	}
//
//	override val itemHandler: ExpansibleItemHandler = ExpansibleItemHandler(2)
//	override val energyHandler: ExpansibleEnergyHandler = ExpansibleEnergyHandler(
//		mutableListOf(
//			ExpansibleEnergyHandler.ExpansibleCell(BigDecimal.valueOf(100_000))
//		)
//	)
//
//	init {
//		val handler = this.itemHandler
//		handler.setAllowedSides(UP, DOWN)
//		handler.setMaxInOut(0, 64, 0)
//		handler.setMaxInOut(1, 0, 64)
//	}
//
//	override fun runCurrentRecipe(
//		recipe: WheatCrusherRecipe,
//		level: Level
//	) {
//		val powered = this.blockState.getValue(Companion.POWERED)
//		if (!recipe.itemStillValid(this.getItem(0))) {
//			this.resetRecipe(level)
//		}
//		val recipeTime = recipe.getTime()
//
//		this.energyDivision = recipe.setEnergyDivision()
//		if (this.handleEnergy(this.energyHandler)) return
//		if (this.energyHandler.extractEnergy(this.energyDivision, false) < this.energyDivision) return
//
//		if (!powered) level.setBlockAndUpdate(this.blockPos, this.blockState.setValue(Companion.POWERED, true))
//		if (this.progress >= recipeTime) this.finalizeAndReset(recipe, level) else this.progress++
//	}
//
//	override fun runMissingRecipe(
//		level: Level
//	) {
//		level.setBlockAndUpdate(this.blockPos, this.blockState.setValue(Companion.POWERED, false))
//		val stack = this.getItem(0)
//		val check = this.getOptionalRecipe(FluidEnergyInput(stack), level)
//
//		check.ifPresent { present ->
//			val recipe = present.value
//			if (recipe.canFitItemResult(this.getItem(1))) {
//				this.setRecipe(recipe)
//				this.maxProgress = present.value.getTime()
//				level.setBlockAndUpdate(this.blockPos, this.blockState.setValue(Companion.POWERED, true))
//			}
//		}
//	}
//
//	override fun checkIsEmpty(level: Level): Boolean = this.getItem(0).isEmpty
//
//	override fun resetRecipe(level: Level) {
//		this.energyDivision = -1
//		super.resetRecipe(level)
//	}
//
//	override fun finalizeRecipe(recipe: WheatCrusherRecipe, level: Level): Boolean {
//		val stack = this.getItem(0)
//		val assemble = recipe.assembleItem(FluidEnergyInput(stack), level)
////		recipe.consumeItems(listOf(this.getItem(0))).forEachIndexed(this::setItem)
//		recipe.consumeItemsAndSet(listOf(this.getItem(0)), this::setItem)
//		this.setOrGrowItem(1, assemble, assemble.count)
//		return true
//	}
//
//	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
//		WheatCrusherMenu(containerId, playerInventory, this)
//
//	override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
//}