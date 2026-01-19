package org.bread_experts_group.breadmod.experimental.lidar.handler

import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos

class LidarSection(val sectionPos: SectionPos) {
	private val lidarBlocks: MutableMap<BlockPos, LidarBlock> = mutableMapOf()

	fun getBlock(blockPos: BlockPos): LidarBlock = this.lidarBlocks.getOrPut(blockPos) { LidarBlock(blockPos) }
}