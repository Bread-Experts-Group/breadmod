package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import org.bread_experts_group.breadmod.client.gui.overlays.TestOverlay
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.joml.Vector3f

// todo on the backburner until i figure out the shader code...
class DoubleOrNothingRenderer(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntity> {
	private val vertexes: Array<Vector3f> = arrayOf(
		Vector3f(1f, 0f, 0f),
		Vector3f(0f, 0f, 0f),
		Vector3f(1f, -2f, 0f),
		Vector3f(0f, -2f, 0f)
	)

	override fun render(
		blockEntity: DoubleOrNothingBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		poseStack.pushPose()
		this.drawRainbowQuad(
			poseStack,
			bufferSource,
			this.vertexes[0],
			this.vertexes[1],
			this.vertexes[2],
			this.vertexes[3]
		)
		poseStack.popPose()
	}

	private fun drawRainbowQuad(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		topLeft: Vector3f,
		topRight: Vector3f,
		bottomLeft: Vector3f,
		bottomRight: Vector3f,
	) {
		val buffer = bufferSource.getBuffer(ModRenderType.rainbow())
		val pose = poseStack.last().pose()
		fun transform(pos: Vector3f) = pose.transformPosition(pos.x, pos.y, pos.z, Vector3f())
		buffer.addVertex(pose, topLeft.x, topLeft.y, topLeft.z)
		TestOverlay.vector3f = transform(topLeft)
		buffer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z)
		TestOverlay.vector3f1 = transform(bottomLeft)
		buffer.addVertex(pose, bottomRight.x, bottomRight.y, bottomRight.z)
		TestOverlay.vector3f2 = transform(bottomRight)
		buffer.addVertex(pose, topRight.x, topRight.y, topRight.z)
		TestOverlay.vector3f3 = transform(topRight)
	}
}