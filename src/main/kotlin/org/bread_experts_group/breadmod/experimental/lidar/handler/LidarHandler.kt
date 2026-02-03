package org.bread_experts_group.breadmod.experimental.lidar.handler

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.util.Mth.clamp
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.registry.item.ModItems

object LidarHandler {
	private val lidarSections: MutableMap<SectionPos, LidarSection> = mutableMapOf()
	val tickingSections: MutableList<LidarSection> = mutableListOf()
	var dotCounter: Int = 0
	var isBurstScanning: Boolean = false
	var burstScanTimeRemaining: Int = 0
		set(value) {
			field = clamp(value, 0, Int.MAX_VALUE)
		}
	var burstY: Float = 0f
	var currentDeviation: Float = 0.1f
		set(value) {
			field = clamp(value, 0f, 1f)
		}

	// Trying to instantiate this too early just causes it to not play at all.
	// So we set it when the player uses the lidar gun.
	var lidarSound: LidarSound? = null

	fun getSection(section: SectionPos, level: Level): LidarSection = this.lidarSections.getOrPut(section) {
		LidarSection(section, level)
	}

	fun getSection(pos: BlockPos, level: Level): LidarSection = this.getSection(SectionPos.of(pos), level)

	fun renderSections(event: RenderLevelStageEvent, bufferSource: MultiBufferSource) {
		if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return
		this.lidarSections.forEach { (_, section) -> section.render(event, bufferSource) }
	}

	fun tick(player: LocalPlayer) {
		this.lidarSound?.setPos(player.position())
		if (!player.isHolding(ModItems.LIDAR_GUN.get())) {
			this.lidarSound?.stop()
			return
		}

		if (this.tickingSections.isNotEmpty()) this.tickingSections.removeIf {
			it.tick()
			it.renderingTimeout == 0 && it.isRenderingMesh
		}
	}

	fun onMouseInput(event: InputEvent.MouseButton.Pre) {
		val sound = this.lidarSound ?: return
		val isPress = event.action == InputConstants.PRESS
		val isRelease = event.action == InputConstants.RELEASE

		if (!sound.isActive() && isPress) sound.play()
		else if (isRelease) sound.stop(true)
	}
}