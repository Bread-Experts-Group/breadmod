package org.bread_experts_group.breadmod.registry.block.actual.entity
//class DoughMachineBlockEntity(
//	pos: BlockPos, state: BlockState
//) : BreadModRecipeBlockEntity<FluidEnergyInput, DoughMachineRecipe, DoughMachineBlockEntity>(
//	ModBlockEntityTypes.DOUGH_MACHINE.get(),
//	pos,
//	state,
//	ModRecipeTypes.DOUGH_MACHINE.get()
//), MenuProvider, ItemBearingBlockEntity, FluidBearingBlockEntity, EnergyBearingBlockEntity {
//	private companion object {
//		val POWERED: BooleanProperty = BlockStateProperties.POWERED
//		var SINGLE_SLOT_CACHE: List<Optional<RecipeHolder<DoughMachineRecipe>>> = listOf()
//		var DUAL_SLOT_CACHE: List<Optional<RecipeHolder<DoughMachineRecipe>>> = listOf()
//	}
//
//	override val itemHandler: ExpansibleItemHandler = object : ExpansibleItemHandler(4) {
//		override fun onContentsChanged(slot: Int) {
//			val fluidInput = listOf(this@DoughMachineBlockEntity.getFluid(0))
//			val itemInputs = this@DoughMachineBlockEntity.getItemsInRange(0 .. 1)
//			val check = this@DoughMachineBlockEntity.getOptionalRecipe(
//				FluidEnergyInput(itemInputs, fluidInput),
//				this@DoughMachineBlockEntity.level ?: return
//			)
//			if (check.getOrNull()?.value != this@DoughMachineBlockEntity.currentRecipe.get()) {
//				this@DoughMachineBlockEntity.resetRecipe(this@DoughMachineBlockEntity.level ?: return)
//			}
//		}
//	}
//	override val fluidHandler: ExpansibleFluidHandler = ExpansibleFluidHandler(
//		mutableListOf(
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = true, allowOut = false),
//			ExpansibleFluidHandler.ExpansibleTank(10_000, allowIn = false, allowOut = true)
//		)
//	)
//	override val energyHandler: ExpansibleEnergyHandler = ExpansibleEnergyHandler(
//		mutableListOf(
//			ExpansibleEnergyHandler.ExpansibleCell(BigDecimal.valueOf(1_000_000))
//		)
//	)
//
//	override fun getOptionalRecipe(input: FluidEnergyInput, level: Level): Optional<RecipeHolder<DoughMachineRecipe>> {
//		if (Companion.SINGLE_SLOT_CACHE.isEmpty() && Companion.DUAL_SLOT_CACHE.isEmpty()) {
//			val recipes = this.getRecipeList(ModRecipeTypes.DOUGH_MACHINE, level)
//			Companion.SINGLE_SLOT_CACHE = recipes.filter { it.get().value.rItemInputs.size == 1 }
//			Companion.DUAL_SLOT_CACHE = recipes.filter { it.get().value.rItemInputs.size == 2 }
//		}
//		val recipes = if (input.iItems[1].isEmpty) Companion.SINGLE_SLOT_CACHE else Companion.DUAL_SLOT_CACHE
//		if (input.isEmpty) return Optional.empty()
//		return recipes.firstOrNull { it.get().value.matches(input, level) } ?: Optional.empty()
//	}
//
//	override fun runCurrentRecipe(
//		recipe: DoughMachineRecipe,
//		level: Level
//	) {
//		val powered = this.blockState.getValue(Companion.POWERED)
//		val fluidInput = listOf(this.getFluid(0))
//		val itemInputs = this.getItemsInRange(0 .. 1)
//
//		if (!recipe.inputsStillValid(itemInputs, fluidInput)) this.resetRecipe(level)
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
//	override fun runMissingRecipe(level: Level) {
//		level.setBlockAndUpdate(this.blockPos, this.blockState.setValue(Companion.POWERED, false))
//		val fluidInput = listOf(this.getFluid(0))
//		val itemInputs = this.getItemsInRange(0 .. 1)
//		val check = this.getOptionalRecipe(FluidEnergyInput(itemInputs, fluidInput), level)
//
//		check.ifPresent { present ->
//			val recipe = present.value
//			if (
//				recipe.canFitFluidResult(this.getFluid(1), this.getTankCapacity(1)) &&
//				recipe.canFitItemResult(this.getItem(2))
//			) {
//				this.setRecipe(recipe)
//				this.maxProgress = recipe.getTime()
//				level.setBlockAndUpdate(this.blockPos, this.blockState.setValue(Companion.POWERED, true))
//			}
//		}
//	}
//
//	override fun checkIsEmpty(level: Level): Boolean =
//		this.getItemsInRange(0 .. 1).isEmpty() || this.getFluid(0).isEmpty
//
//	override fun finalizeRecipe(recipe: DoughMachineRecipe, level: Level): Boolean {
//		val assemble = recipe.assembleOutputs()
//		recipe.consumeItemsAndSet(this.getItemsInRange(0 .. 1), this::setItem)
//		recipe.consumeFluidsAndSet(listOf(this.getFluid(0)), this::setFluid)
//		if (assemble.first.isNotEmpty()) this.setOrGrowItem(2, assemble.first[0], assemble.first[0].count)
//		if (assemble.second.isNotEmpty()) this.setOrGrowFluid(1, assemble.second[0], assemble.second[0].amount)
//		return true
//	}
//
//	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
//		DoughMachineMenu(containerId, playerInventory, this)
//
//	override fun getDisplayName(): Component = modTranslatable("block", "dough_machine")
//}