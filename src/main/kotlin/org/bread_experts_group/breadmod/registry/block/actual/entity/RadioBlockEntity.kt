package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class RadioBlockEntity(
	pos: BlockPos,
	state: BlockState,
) : BreadModBlockEntity<RadioBlockEntity>(ModBlockEntityTypes.RADIO.get(), pos, state) {
	companion object {
		var instance: StereoSoundInstance? = null
	}

	init {

	}
}