package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler.ExpansibleTank
import java.math.BigDecimal

class DieselGeneratorBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<DieselGeneratorBlockEntity>(ModBlockEntityTypes.DIESEL_GENERATOR.get(), pos, state),
	FluidBearingBlockEntity {
	var hasTurboUpgrade: Boolean = true
	var hasEnergyUpgrade: Boolean = true
	var hasChargingUpgrade: Boolean = true
	var doorOpen: Boolean = true
	override val fluidHandler: ExpansibleFluidHandler =
		ExpansibleFluidHandler(mutableListOf(ExpansibleTank(BigDecimal.valueOf(100_000))))
}