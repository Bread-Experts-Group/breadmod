package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import org.bread_experts_group.breadmod.util.Color
import org.joml.Vector3f

/**
 * Draws a quad.
 *
 * @author Logan McLean
 * @since 1.0.0
 * @see drawVertex
 */
fun drawQuad(
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	renderType: RenderType,
	color: Int = Color.WHITE,
	topLeft: Vector3f = Vector3f(0f, 0f, 0f),
	topRight: Vector3f = Vector3f(1f, 0f, 0f),
	bottomLeft: Vector3f = Vector3f(0f, -1f, 0f),
	bottomRight: Vector3f = Vector3f(1f, -1f, 0f),
	u0: Float = 0f, v0: Float = 0f,
	u1: Float = 1f, v1: Float = 1f,
	packedLight: Int = LightTexture.FULL_BRIGHT,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY,
	extraElements: (VertexConsumer) -> Unit = {}
) {
	drawVertex(
		poseStack, buffer, renderType, color,
		topLeft.x, topLeft.y, topLeft.z, u0, v0,
		packedLight, packedOverlay, extraElements
	)
	drawVertex(
		poseStack, buffer, renderType, color,
		bottomLeft.x, bottomLeft.y, bottomLeft.z, u0, v1,
		packedLight, packedOverlay, extraElements
	)
	drawVertex(
		poseStack, buffer, renderType, color,
		bottomRight.x, bottomRight.y, bottomRight.z, u1, v1,
		packedLight, packedOverlay, extraElements
	)
	drawVertex(
		poseStack, buffer, renderType, color,
		topRight.x, topRight.y, topRight.z, u1, v0,
		packedLight, packedOverlay, extraElements
	)
}

/**
 * Draws a vertex.
 *
 * @author Logan McLean
 * @since 1.0.0
 * @see drawQuad
 */
fun drawVertex(
	poseStack: PoseStack,
	pBuffer: MultiBufferSource,
	renderType: RenderType,
	color: Int,
	x: Float,
	y: Float,
	z: Float,
	u: Float,
	v: Float,
	packedLight: Int,
	packedOverlay: Int,
	extraElements: (VertexConsumer) -> Unit = {}
) {
	val buffer = pBuffer.getBuffer(renderType)
	buffer.addVertex(poseStack.last().pose(), x, y, z)
		.setColor(color)
		.setUv(u, v)
		.setOverlay(packedOverlay)
		.setLight(packedLight)
		.setNormal(0f, 1f, 0f)
		.also { extraElements.invoke(it) }
}