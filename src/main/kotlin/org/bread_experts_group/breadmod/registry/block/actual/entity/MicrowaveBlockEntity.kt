package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.MicrowaveRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput

class MicrowaveBlockEntity(
	pos : BlockPos,
	state : BlockState
) : BreadModRecipeBlockEntity<FluidEnergyInput, MicrowaveRecipe, MicrowaveBlockEntity>(
	ModBlockEntityTypes.MICROWAVE.get(),
	pos,
	state,
	ModRecipeTypes.MICROWAVE.get(),
	1
) {
	override fun commonTick(
		clientLevel : Level,
		pos : BlockPos,
		state : BlockState,
		entity : AbstractTickingBlockEntity<*>
	) {
		super.commonTick(clientLevel, pos, state, entity)
	}

	override fun finalizeRecipe(recipe : MicrowaveRecipe, level : Level) {
		TODO("Not yet implemented")
	}
}