package org.bread_experts_group.breadmod.util.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import org.joml.Vector3f
import org.joml.Vector4f

/**
 * Draws a quad with a provided [textureLocation].
 *
 * @author Logan McLean
 * @since 1.0.0
 * @see drawQuad
 */
fun drawTexturedQuad(
	textureLocation : ResourceLocation,
	renderType : RenderType,
	poseStack : PoseStack,
	buffer : MultiBufferSource,
	color : Vector4f = Vector4f(1f, 1f, 1f, 1f),
	vertex0 : Vector3f = Vector3f(0f, 0f, 0f),
	vertex1 : Vector3f = Vector3f(0f, 0f, 1f),
	vertex2 : Vector3f = Vector3f(1f, 0f, 1f),
	vertex3 : Vector3f = Vector3f(1f, 0f, 0f)
) {
	val sprite = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLocation)
	drawQuad(
		poseStack, buffer, renderType, color,
		vertex0,
		vertex1,
		vertex2,
		vertex3,
		sprite.u0, sprite.v0,
		sprite.u1, sprite.v1
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
	poseStack : PoseStack,
	buffer : MultiBufferSource,
	renderType : RenderType,
	color : Vector4f,
	vertex0 : Vector3f,
	vertex1 : Vector3f,
	vertex2 : Vector3f,
	vertex3 : Vector3f,
	u0 : Float, v0 : Float,
	u1 : Float, v1 : Float
) {
	drawVertex(
		poseStack,
		buffer,
		renderType,
		color,
		vertex0.x,
		vertex0.y,
		vertex0.z,
		u0,
		v0
	)
	drawVertex(
		poseStack,
		buffer,
		renderType,
		color,
		vertex1.x,
		vertex1.y,
		vertex1.z,
		u0,
		v1
	)
	drawVertex(
		poseStack,
		buffer,
		renderType,
		color,
		vertex2.x,
		vertex2.y,
		vertex2.z,
		u1,
		v1
	)
	drawVertex(
		poseStack,
		buffer,
		renderType,
		color,
		vertex3.x,
		vertex3.y,
		vertex3.z,
		u1,
		v0
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
	poseStack : PoseStack,
	pBuffer : MultiBufferSource,
	renderType : RenderType,
	color : Vector4f,
	x : Float,
	y : Float,
	z : Float,
	u : Float,
	v : Float
) {
	val buffer = pBuffer.getBuffer(renderType)
	buffer.addVertex(poseStack.last().pose(), x, y, z)
		.setColor(color.x, color.y, color.z, color.w)
		.setUv(u, v)
		.setOverlay(OverlayTexture.NO_OVERLAY)
		.setLight(0xFFFFFF)
		.setNormal(0f, 1f, 0f)
}