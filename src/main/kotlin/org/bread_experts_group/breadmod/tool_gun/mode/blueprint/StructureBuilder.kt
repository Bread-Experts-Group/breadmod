package org.bread_experts_group.breadmod.tool_gun.mode.blueprint

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3

class StructureBuilder(
	val level: Level,
	val blocks: Iterator<Pair<BlockPos, Int>>,
	val targetPos: BlockPos
) {
	init {
		Thread.ofVirtual().start {
			var target = System.currentTimeMillis() + 10
			do {
				if (System.currentTimeMillis() >= target) {
					val pair = this.blocks.next()
					val state = Block.BLOCK_STATE_REGISTRY.byId(pair.second) ?: return@start
					val (x, y, z) = this.targetPos.offset(pair.first)
					val sound = state.getSoundType(this.level, pair.first, null).placeSound
					val offsetPos = this.targetPos.offset(pair.first)
					this.level.setBlock(offsetPos, state, 2)
					this.level.playSound(
						null,
						x.toDouble(),
						y.toDouble(),
						z.toDouble(),
						sound,
						SoundSource.BLOCKS,
						1f,
						1f
					)
					target += 10
				}
			} while (this.blocks.hasNext())
		}
	}
}