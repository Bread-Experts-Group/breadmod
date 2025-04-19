package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.PoseStack.Pose
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.toYRotFixed
import org.joml.Matrix4f
import org.joml.Vector3f
import java.awt.Color

class DoubleOrNothingRenderer(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntity> {
	private val vertexes: Array<Vector3f> = arrayOf(
		Vector3f(1f, 0f, 0f),
		Vector3f(0f, 0f, 0f),
		Vector3f(3f, -2f, 0f),
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
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		poseStack.pushPose()
		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
		poseStack.translate(0.0, 0.0, -1.0)
		this.drawRainbowQuad(
			poseStack,
			bufferSource,
			Vector3f(5f, 2f, 0f),
			Vector3f(0f, 2f, 0f),
			Vector3f(5f, -2f, 0f),
			Vector3f(0f, -2f, 0f)
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
		buffer.addVertex(pose, topLeft.x, topLeft.y, topLeft.z).setUv(0f, 0f)
		buffer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z).setUv(0f, 1f)
		buffer.addVertex(pose, bottomRight.x, bottomRight.y, bottomRight.z).setUv(1f, 1f)
		buffer.addVertex(pose, topRight.x, topRight.y, topRight.z).setUv(1f, 0f)
	}
}