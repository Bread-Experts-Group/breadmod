package org.bread_experts_group.breadmod.experimental.lidar.handler

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.Camera
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.lidar.mesh.SectionMesh
import org.bread_experts_group.breadmod.registry.shader.ModPostChains
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color

class LidarSection(val sectionPos: SectionPos, private val level: Level) {
	val lidarBlocks: MutableMap<BlockPos, LidarBlock> = mutableMapOf()
	val sectionMesh: SectionMesh = SectionMesh(this)
	val minX: BlockPos = BlockPos(this.sectionPos.minBlockX(), this.sectionPos.minBlockY(), this.sectionPos.minBlockZ())
	val maxX: BlockPos = BlockPos(this.sectionPos.maxBlockX(), this.sectionPos.maxBlockY(), this.sectionPos.maxBlockZ())
	val bounding: AABB = AABB.of(BoundingBox.fromCorners(this.minX, this.maxX))
	private var renderBounding: Boolean = false
	var isRenderingMesh: Boolean = false
	var renderingTimeout: Int = 20

	fun getBlock(blockPos: BlockPos): LidarBlock = this.lidarBlocks.getOrPut(blockPos) {
		val state = this.level.getBlockState(blockPos)
		val mapColor = state.getMapColor(this.level, blockPos).col
		val adjusted = if (mapColor == 0) Color.DARK_GRAY else mapColor
		LidarBlock(blockPos, 0xFF000000.toInt() or adjusted)
	}

	/**
	 * Marks this [LidarSection] as needing to render each block individually, instead of as one mesh.
	 */
	fun setDynamicRendering() {
		if (!LidarHandler.tickingSections.contains(this)) LidarHandler.tickingSections.add(this)
		this.isRenderingMesh = false
		this.renderingTimeout = 20
	}

	fun tick() {
		if (this.renderingTimeout != 0) this.renderingTimeout--
		else {
			this.sectionMesh.markForRecompile()
			this.isRenderingMesh = true
		}
	}

	private fun renderBlocks(poseStack: PoseStack, camera: Camera, bufferSource: MultiBufferSource) {
		this.lidarBlocks.forEach { (_, block) ->
			block.render(poseStack, camera, bufferSource.getBuffer(ModRenderType.LIDAR), false)
		}
	}

	fun render(event: RenderLevelStageEvent, bufferSource: MultiBufferSource) {
		val poseStack = event.poseStack
		val camera = event.camera
		val player = localClient.player ?: return
		if (this.renderBounding) {
			poseStack.pushPose()
			poseStack.initialTranslate(camera)
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
		}
		if (!this.isRenderingMesh) {
			this.renderBlocks(poseStack, camera, bufferSource)
		} else {
			// Swap to dynamic rendering when the player is inside the section,
			// todo expand to include adjacent sections if the player is close enough for proximity based dot coloring in the future
			if (this.bounding.intersects(player.boundingBox)) {
				this.renderBlocks(poseStack, camera, bufferSource)
			} else {
				if (this.sectionMesh.ready()) {
					val buffer = this.sectionMesh.getBuffer()
					val shaderInstance = GameRenderer.getPositionColorShader() ?: return
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
				} else {
					// Keep rendering the blocks until the section is finished compiling,
					// this prevents the section from vanishing for a few milliseconds.
					this.renderBlocks(poseStack, camera, bufferSource)
					this.sectionMesh.compile(poseStack, camera)
				}
			}
		}
	}
}