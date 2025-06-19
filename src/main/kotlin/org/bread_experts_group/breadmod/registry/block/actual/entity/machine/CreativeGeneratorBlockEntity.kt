package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
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

	override fun serverTick(serverLevel: ServerLevel) {
		val enabled = this.blockState.getValue(CreativeGeneratorBlock.ENABLED)
		if (enabled) {
			if (this.energyHandler.energyStored != this.energyHandler.maxEnergyStored) this.energyHandler.receiveEnergy(
				Int.MAX_VALUE,
				false
			)
			Direction.entries
				.asSequence()
				.mapNotNull {
					serverLevel.getCapability(
						Capabilities.EnergyStorage.BLOCK,
						this.blockPos.relative(it),
						it
					)
				}
				.forEach { it.receiveEnergy(Int.MAX_VALUE, false) }
		}
	}

	override fun clientTick(
		clientLevel: ClientLevel
	) {
		val enabled = this.blockState.getValue(CreativeGeneratorBlock.ENABLED)
		if (clientLevel.gameTime % 80.0 == 0.0 && enabled)
			clientLevel.playLocalSound(
				this.blockPos,
				SoundEvents.BEACON_AMBIENT,
				BLOCKS,
				1f, 1f, false
			)
	}
}