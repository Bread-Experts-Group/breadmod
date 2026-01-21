package org.bread_experts_group.breadmod.experimental.lidar.handler

import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.Level

object LidarHandler {
	private val lidarSections: MutableMap<SectionPos, LidarSection> = mutableMapOf()

	fun getSection(section: SectionPos, level: Level): LidarSection = this.lidarSections.getOrPut(section) {
		LidarSection(section, level)
	}

	fun getSection(pos: BlockPos, level: Level): LidarSection = this.getSection(SectionPos.of(pos), level)
}