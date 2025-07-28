package org.bread_experts_group.breadmod.registry.block.actual.entity
//class MicrowaveBlockEntity(
//	pos: BlockPos,
//	state: BlockState
//) : BreadModRecipeBlockEntity<FluidEnergyInput, MicrowaveRecipe, MicrowaveBlockEntity>(
//	ModBlockEntityTypes.MICROWAVE.get(),
//	pos,
//	state,
//	ModRecipeTypes.MICROWAVE.get()
//), ItemBearingBlockEntity {
//	override val itemHandler: ExpansibleItemHandler = ExpansibleItemHandler(1)
//	private val campfireRecipeCache: CachedCheck<SingleRecipeInput, CampfireCookingRecipe> =
//		RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING)
//	private var currentCampfireRecipe: Optional<CampfireCookingRecipe> = Optional.empty()
//
//	override fun commonRecipeTickPre(level: Level): Boolean {
//		val stack = this.getItem(0)
//		this.currentCampfireRecipe.ifPresentOrElse({ recipe ->
//			this.setItem(0, recipe.assemble(SingleRecipeInput(stack), level.registryAccess()))
//		}, {
//			val check = this.campfireRecipeCache.getRecipeFor(SingleRecipeInput(stack), level)
//			check.ifPresent { this.currentCampfireRecipe = Optional.of(it.value) }
//		})
//		return true
//	}
//
//	override fun runMissingRecipe(level: Level) {
//		TODO("Not yet implemented")
//	}
//
//	override fun runCurrentRecipe(
//		recipe: MicrowaveRecipe,
//		level: Level
//	) {
//		TODO("Not yet implemented")
//	}
//
//	override fun checkIsEmpty(level: Level): Boolean {
//		TODO("Not yet implemented")
//	}
//
//	override fun finalizeRecipe(recipe: MicrowaveRecipe, level: Level): Boolean {
//		TODO("Not yet implemented")
//	}
//}