package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.machine.CreativeGeneratorBlock
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler.ExpansibleCell
import java.math.BigDecimal

class CreativeGeneratorBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<CreativeGeneratorBlockEntity>(
	ModBlockEntityTypes.CREATIVE_GENERATOR.get(),
	pos,
	state
), EnergyBearingBlockEntity {
	override val energyHandler: ExpansibleEnergyHandler =
		ExpansibleEnergyHandler(mutableListOf(ExpansibleCell(BigDecimal.valueOf(Long.MAX_VALUE))))

	override fun commonTick(level: Level, pos: BlockPos, state: BlockState, entity: CreativeGeneratorBlockEntity) {
		val enabled = state.getValue(CreativeGeneratorBlock.ENABLED)
		if (this.energyHandler.energyStored != this.energyHandler.maxEnergyStored)
			this.energyHandler.receiveEnergy(Int.MAX_VALUE, false)

		if (level.gameTime % 80.0 == 0.0 && enabled)
			level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, BLOCKS, 1f, 1f)
	}
}