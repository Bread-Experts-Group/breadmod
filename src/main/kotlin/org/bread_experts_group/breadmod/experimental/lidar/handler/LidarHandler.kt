package org.bread_experts_group.breadmod.experimental.lidar.handler

import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

object LidarHandler {
	private val lidarSections: MutableMap<SectionPos, LidarSection> = mutableMapOf()
	val tickingSections: MutableList<LidarSection> = mutableListOf()
	var dotCounter: Int = 0

	fun getSection(section: SectionPos, level: Level): LidarSection = this.lidarSections.getOrPut(section) {
		LidarSection(section, level)
	}

	fun getSection(pos: BlockPos, level: Level): LidarSection = this.getSection(SectionPos.of(pos), level)

	fun renderSections(event: RenderLevelStageEvent, bufferSource: MultiBufferSource) {
		if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return
		this.lidarSections.forEach { (_, section) -> section.render(event, bufferSource) }
	}

	fun tick() {
		if (this.tickingSections.isEmpty()) return
		this.tickingSections.removeIf {
			it.tick()
			it.renderingTimeout == 0 && it.isRenderingMesh
		}
	}
}