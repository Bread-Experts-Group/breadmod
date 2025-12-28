package org.bread_experts_group.breadmod.registry.block.handler

import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.ModDataComponents.RECIPE
import org.bread_experts_group.breadmod.ModDataComponents.RECIPE_PROGRESS
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe
import org.bread_experts_group.breadmod.util.Color.DARK_GRAY
import org.bread_experts_group.breadmod.util.Color.GRAY
import org.bread_experts_group.breadmod.util.Color.LAVENDER_MAGENTA
import org.bread_experts_group.breadmod.util.Color.component
import org.bread_experts_group.breadmod.util.floatRoundEven
import java.math.BigDecimal

// TODO recipe assembling tries to put outputs in the input slots
class FERecipeHandler<T : FluidEnergyRecipe>(
	recipeType: RecipeType<T>
) : ParentedHandler<BreadModBlockEntity>, INBTSerializable<Tag>, DataComponentSerializable {
	companion object {
		@Suppress("UNCHECKED_CAST")
		fun <T : FluidEnergyRecipe> BreadModBlockEntity.getRecipeHandler(): FERecipeHandler<T> =
			this.getCapability(this@Companion.BLOCK_VOID) as FERecipeHandler<T>

		val BLOCK_VOID: BlockCapability<FERecipeHandler<*>, Void?> = BlockCapability.createVoid<FERecipeHandler<*>>(
			modLocation("recipe_handler"),
			FERecipeHandler::class.java
		)
	}

	lateinit var input: FluidEnergyInput
	override lateinit var parent: BreadModBlockEntity
	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
	private val recipeDial: RecipeManager.CachedCheck<FluidEnergyInput, T> = RecipeManager.createCheck(recipeType)
	var recipeComputationSuspended: Boolean = false
		private set
	var progress: ULong = 0u
		private set
	var recipe: RecipeHolder<T>? = null
		private set

	/**
	 * Advances this [recipe], and assembles the results upon completion.
	 */
	fun advanceAndFinishRecipe(): Boolean {
		val recipe = this.recipe ?: return false
		val level = this.parent.level ?: return false
		this.progress++
		if (this.progress >= recipe.value.rTime) {
			recipe.value.assemble(this.input, level.registryAccess())
			this.parent.setChanged()
			this.reset()
			return true
		}
		return false
	}

	private fun computeRecipe() {
		if (this.recipe != null || this.recipeComputationSuspended) return
		this.recipeDial.getRecipeFor(this.input, this.parent.level ?: return).ifPresent {
			this.input.lastConsumed = BigDecimal.ZERO
			this.progress = 0u
			this.recipe = it
		}
	}

	private fun checkInput() {
		val recipe = this.recipe?.value ?: return
		val level = this.parent.level ?: return
		val matches = recipe.matches(this.input, level)
		if (!matches) this.reset()
	}

	override fun onParentReady() {
		val item = this.parent.getCapabilityOrNull(Capabilities.ItemHandler.BLOCK) as? ExtendedItemHandler
		val fluid = this.parent.getCapabilityOrNull(Capabilities.FluidHandler.BLOCK) as? ExtendedFluidHandler
		val energy = this.parent.getCapabilityOrNull(Capabilities.EnergyStorage.BLOCK) as? ExtendedEnergyHandler
		this.input = FluidEnergyInput(item, fluid, energy)
		item?.stateListeners?.add { this.computeRecipe(); this.checkInput() }
		fluid?.stateListeners?.add { this.computeRecipe(); this.checkInput() }
		energy?.stateListeners?.add { this.computeRecipe(); this.checkInput() }
	}

	fun reset() {
		this.recipe = null
		this.progress = 0u
	}

	override fun serializeNBT(provider: HolderLookup.Provider): Tag = CompoundTag().also {
		it.putLong("progress", this.progress.toLong())
		it.putString("lastConsumed", this.input.lastConsumed.toString())
		this.recipe?.let { recipe ->
			val encoded = Recipe.CODEC.encodeStart(NbtOps.INSTANCE, recipe.value)
			encoded.ifSuccess { tag ->
				it.put("recipe", tag)
				it.putString("recipeID", recipe.id.toString())
			}
		}
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, nbt: Tag) {
		if (nbt !is CompoundTag) return
		val recipe = nbt.get("recipe")
		val recipeID = nbt.getString("recipeID")
		if (recipe != null && recipeID != "") {
			val decoded = Recipe.CODEC.decode(NbtOps.INSTANCE, recipe)
			decoded.ifSuccess { data ->
				@Suppress("UNCHECKED_CAST")
				this.recipe = RecipeHolder(
					ResourceLocation.parse(recipeID),
					data.first as T
				)
				this.progress = nbt.getLong("progress").toULong()
			}
		}
	}

	override fun serializeDataComponent(map: DataComponentMap.Builder) {
		this.recipe?.let {
			map.set(RECIPE, it)
			map.set(RECIPE_PROGRESS, this.progress.toLong())
		}
	}

	override fun deserializeDataComponent(from: BlockEntity.DataComponentInput) {
		val holder = from.get(RECIPE)
		if (holder != null) {
			@Suppress("UNCHECKED_CAST")
			this.recipe = holder as RecipeHolder<T>
			this.progress = from.getOrDefault(RECIPE_PROGRESS, 0).toULong()
		}
	}

	val big100: BigDecimal = BigDecimal.valueOf(100)
	override fun collectHoverText(tooltipComponents: MutableList<Component>) {
		val recipe = this.recipe
		if (recipe != null) {
			val recipeComponent = '['.component(DARK_GRAY)
			recipeComponent.append(recipe.id.toString().component(LAVENDER_MAGENTA))
			recipeComponent.append("] (".component(DARK_GRAY))
			val percentage = BigDecimal(this.progress.toString())
				.divide(BigDecimal(recipe.value.rTime.toString()), floatRoundEven)
				.multiply(this.big100)
				.setScale(2)
			recipeComponent.append(percentage.toString().component(LAVENDER_MAGENTA))
			recipeComponent.append('%'.component(GRAY))
			recipeComponent.append(')'.component(DARK_GRAY))
			tooltipComponents.add(recipeComponent)
		} else {
			tooltipComponents.add("<no recipe in progress>".component(DARK_GRAY))
		}
	}
}