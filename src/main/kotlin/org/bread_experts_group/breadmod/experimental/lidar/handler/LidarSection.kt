package org.bread_experts_group.breadmod.experimental.lidar.handler

import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.shader.ModPostChains
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color

class LidarSection(val sectionPos: SectionPos, private val level: Level) {
	val lidarBlocks: MutableMap<BlockPos, LidarBlock> = mutableMapOf()
	val sectionMesh: SectionMesh = SectionMesh(this)
	private var hasRenderer: Boolean = false
	val minX: BlockPos = BlockPos(this.sectionPos.minBlockX(), this.sectionPos.minBlockY(), this.sectionPos.minBlockZ())
	val maxX: BlockPos = BlockPos(this.sectionPos.maxBlockX(), this.sectionPos.maxBlockY(), this.sectionPos.maxBlockZ())
	val bounding: AABB = AABB.of(BoundingBox.fromCorners(this.minX, this.maxX))
//	private var renderBounding: Boolean = false

	fun getBlock(blockPos: BlockPos): LidarBlock = this.lidarBlocks.getOrPut(blockPos) {
		val state = this.level.getBlockState(blockPos)
		val mapColor = state.getMapColor(this.level, blockPos).col
		val adjusted = if (mapColor == 0) Color.DARK_GRAY else mapColor
		LidarBlock(blockPos, 0xFF000000.toInt() or adjusted)
	}

	fun addRenderer() {
		if (this.hasRenderer) return
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
			val poseStack = event.poseStack
			val camera = event.camera
			val bufferSource = localClient.renderBuffers().bufferSource()
			val player = localClient.player ?: return@add true
//			poseStack.pushPose()
//			poseStack.initialTranslate(camera)
//			LevelRenderer.renderLineBox(
//				poseStack,
//				bufferSource.getBuffer(RenderType.lines()),
//				this.bounding,
//				1f,
//				1f,
//				1f,
//				1f
//			)
//			poseStack.popPose()
			if (this.bounding.intersects(player.boundingBox)) {
				this.lidarBlocks.forEach { (_, block) ->
					block.renderSides(poseStack, camera, bufferSource.getBuffer(ModRenderType.LIDAR), false)
				}
			} else {
				if (this.sectionMesh.ready()) {
					val buffer = this.sectionMesh.getBuffer()
					val shaderInstance = GameRenderer.getPositionColorShader() ?: return@add false
					poseStack.pushPose()
					poseStack.mulPose(event.modelViewMatrix)
					poseStack.initialTranslate(camera)
					ModPostChains.lidarTarget.bindWrite(false)
					buffer.bind()
					buffer.drawWithShader(
						poseStack.last().pose(),
						event.projectionMatrix,
						shaderInstance
					)
					VertexBuffer.unbind()
					localClient.mainRenderTarget.bindWrite(false)
					poseStack.popPose()
				} else this.sectionMesh.compile(poseStack, camera)
			}
			false
		})
		this.hasRenderer = true
	}
}