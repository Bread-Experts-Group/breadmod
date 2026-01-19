package org.bread_experts_group.breadmod.experimental.lidar.handler

import net.minecraft.core.SectionPos

object LidarHandler {
	private val lidarSections: MutableMap<SectionPos, LidarSection> = mutableMapOf()

	fun getSection(section: SectionPos): LidarSection = this.lidarSections.getOrPut(section) {
		LidarSection(section)
	}
}