package org.bread_experts_group.breadmod.experimental.physics_grid

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.debug.DebugRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.render.buffer.render.RenderBuffer
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import java.awt.Color

class PhysicsGridRenderer(private val grid: ClientPhysicsGrid) {
	private val blockRenderer = localClient.blockRenderer
	private val random = RandomSource.create()

	fun createRenderTask() {
		val bufferSource = localClient.renderBuffers().bufferSource()
		RenderBuffer.add(
			Stage.AFTER_TRANSLUCENT_BLOCKS,
			{ event, _ ->
				val poseStack = event.poseStack
				val camera = event.camera
				val frustum = event.frustum
				this.render(poseStack, camera, bufferSource, frustum)
				false
			}
		)
	}

	fun render(
		poseStack: PoseStack,
		camera: Camera,
		bufferSource: MultiBufferSource,
		frustum: Frustum
	) {
		if (!this.shouldRender(camera.position, this.grid.boundingBox.center)) return
		poseStack.pushPose()
		poseStack.offsetRenderToCameraPos(this.grid.position, camera, false)
		this.grid.blocks.forEach { (pos, state) ->
			if (this.shouldFrustumCull(this.grid.position.plus(pos.toVec3()), frustum)) return@forEach
			poseStack.pushPose()
			poseStack.translate(pos.toVec3())
			this.renderBlock(pos, state, poseStack, bufferSource)
			poseStack.popPose()
		}
//		this.grid.voxelShapes.forEach { (pos, shape) ->
//			LevelRenderer.renderVoxelShape(
//				poseStack,
//				bufferSource.getBuffer(RenderType.lines()),
//				shape,
//				pos.x.toDouble(),
//				pos.y.toDouble(),
//				pos.z.toDouble(),
//				0.8f,
//				0.8f,
//				1f,
//				0.5f,
//				false
//			)
//		}
//		val center = this.grid.position.minus(this.grid.boundingBox.center)
//		DebugRenderer.renderFilledBox(
//			poseStack,
//			bufferSource,
//			center.x - 0.25,
//			center.y - 0.25,
//			center.z - 0.25,
//			center.x + 0.25,
//			center.y + 0.25,
//			center.z + 0.25,
//			0f,
//			1f,
//			0f,
//			0.5f
//		)
		DebugRenderer.renderFloatingText(
			poseStack,
			bufferSource,
			"PHYSICS GRID #${this.grid.id}",
			this.grid.position.x,
			this.grid.position.y + 5.0,
			this.grid.position.z,
			Color.WHITE.rgb,
			0.1f,
			true,
			0f,
			true
		)
		poseStack.popPose()
	}

	private fun renderBlock(
		pos: BlockPos,
		state: BlockState,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource
	) {
		val model = this.blockRenderer.getBlockModel(state)
		model.getRenderTypes(state, this.random, ModelData.EMPTY).forEach {
			this.blockRenderer.renderBatched(
				state,
				pos,
				this.grid,
				poseStack,
				bufferSource.getBuffer(it),
				true,
				this.random,
				ModelData.EMPTY,
				it
			)
		}
	}

	private fun shouldFrustumCull(position: Position, frustum: Frustum): Boolean {
		val proximal = BlockPos.containing(position)
		return !frustum.isVisible(AABB(proximal).inflate(0.7))
	}

	private fun shouldRender(cameraPos: Vec3, originPos: Vec3): Boolean =
		Vec3.atCenterOf(originPos.toVec3i()).closerThan(cameraPos, this.getViewDistance())

	fun getViewDistance(): Double = 256.0
}