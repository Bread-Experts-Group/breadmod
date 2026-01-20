package org.bread_experts_group.breadmod.experimental.lidar.handler

import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient

class LidarSection(val sectionPos: SectionPos) {
	private val lidarBlocks: MutableMap<BlockPos, LidarBlock> = mutableMapOf()
	private var hasRenderer: Boolean = false
	val bounding: AABB = AABB.of(
		BoundingBox.fromCorners(
			BlockPos(this.sectionPos.minBlockX(), this.sectionPos.minBlockY(), this.sectionPos.minBlockZ()),
			BlockPos(this.sectionPos.maxBlockX(), this.sectionPos.maxBlockY(), this.sectionPos.maxBlockZ())
		)
	)

	fun getBlock(blockPos: BlockPos): LidarBlock = this.lidarBlocks.getOrPut(blockPos) { LidarBlock(blockPos) }

	fun addRenderer() {
		if (this.hasRenderer) return
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
			val poseStack = event.poseStack
			val bufferSource = localClient.renderBuffers().bufferSource()
			poseStack.pushPose()
			poseStack.initialTranslate(event.camera)
			LevelRenderer.renderLineBox(
				poseStack,
				bufferSource.getBuffer(RenderType.lines()),
				this.bounding,
				1f,
				1f,
				1f,
				1f
			)
			poseStack.popPose()
			false
		})
		this.hasRenderer = true
	}
}