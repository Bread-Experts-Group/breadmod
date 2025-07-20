package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.Vector3fAxisX
import org.bread_experts_group.breadmod.util.Vector3fAxisZ
import org.bread_experts_group.breadmod.util.Vector3fZero
import org.joml.Vector3f

/**
 * Draws a quad with a provided [textureLocation].
 *
 * @author Logan McLean
 * @since 1.0.0
 * @see drawQuad
 */
fun drawTexturedQuad(
	textureLocation: ResourceLocation,
	renderType: RenderType,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	color: Int = Color.WHITE,
	topLeft: Vector3f = Vector3fAxisX,
	topRight: Vector3f = Vector3fZero,
	bottomLeft: Vector3f = Vector3fAxisZ,
	bottomRight: Vector3f = Vector3f(1f, 0f, 1f),
	packedLight: Int = 0xFFFFFF,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY,
	extraElements: (VertexConsumer) -> Unit = {}
) {
	val sprite = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLocation)
	drawQuad(
		poseStack, buffer, renderType, color,
		topRight,
		topLeft,
		bottomLeft,
		bottomRight,
		sprite.u0, sprite.v0,
		sprite.u1, sprite.v1,
		packedLight, packedOverlay,
		extraElements
	)
}

/**
 * Draws a quad.
 *
 * @author Logan McLean
 * @since 1.0.0
 * @see drawTexturedQuad
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