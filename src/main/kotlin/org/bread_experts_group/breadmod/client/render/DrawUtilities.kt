package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.PoseStack.Pose
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer
import org.joml.Vector3f
import org.joml.Vector4f
import java.awt.Color

// todo vertex coords need to be redone and renamed on drawTexturedQuad and drawQuad
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
	color: Vector4f = Vector4f(1f, 1f, 1f, 1f),
	topLeft: Vector3f = Vector3f(1f, 0f, 0f),
	topRight: Vector3f = Vector3f(0f, 0f, 0f),
	bottomLeft: Vector3f = Vector3f(0f, 0f, 1f),
	bottomRight: Vector3f = Vector3f(1f, 0f, 1f),
	packedLight: Int = 0xFFFFFF,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY
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
		packedLight, packedOverlay
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
	color: Vector4f,
	topLeft: Vector3f,
	topRight: Vector3f,
	bottomLeft: Vector3f,
	bottomRight: Vector3f,
	u0: Float, v0: Float,
	u1: Float, v1: Float,
	packedLight: Int = 0xFFFFFF,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY
) {
	drawVertex(
		poseStack, buffer, renderType, color,
		topLeft.x, topLeft.y, topLeft.z, u0, v0,
		packedLight, packedOverlay
	)
	drawVertex(
		poseStack, buffer, renderType, color,
		bottomLeft.x, bottomLeft.y, bottomLeft.z, u0, v1,
		packedLight, packedOverlay
	)
	drawVertex(
		poseStack, buffer, renderType, color,
		bottomRight.x, bottomRight.y, bottomRight.z, u1, v1,
		packedLight, packedOverlay
	)
	drawVertex(
		poseStack, buffer, renderType, color,
		topRight.x, topRight.y, topRight.z, u1, v0,
		packedLight, packedOverlay
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
	color: Vector4f,
	x: Float,
	y: Float,
	z: Float,
	u: Float,
	v: Float,
	packedLight: Int,
	packedOverlay: Int
) {
	val buffer = pBuffer.getBuffer(renderType)
	buffer.addVertex(poseStack.last().pose(), x, y, z)
		.setColor(color.x, color.y, color.z, color.w)
		.setUv(u, v)
		.setOverlay(packedOverlay)
		.setLight(packedLight)
		.setNormal(0f, 1f, 0f)
}

// todo learned the existence of QuadBakingVertexConsumer
private val quadBuilder = QuadBakingVertexConsumer()

fun bakedQuadTest(
	color: Int,
	topLeft: Vector3f = Vector3f(1f, 0f, 0f), // top left
	topRight: Vector3f = Vector3f(0f, 0f, 0f), // top right
	bottomLeft: Vector3f = Vector3f(1f, -1f, 0f), // bottom left
	bottomRight: Vector3f = Vector3f(0f, -1f, 0f), // bottom right
	u0: Float, v0: Float,
	u1: Float, v1: Float,
	useAmbientOcclusion: Boolean,
	packedLight: Int = 0xFFFFFF,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY
): BakedQuad {
	quadBuilder.setHasAmbientOcclusion(useAmbientOcclusion)
	quadBuilder.setShade(true)
	quadBuilder.addVertex(topLeft.x, topLeft.y, topLeft.z, color, u0, v0, packedLight, packedOverlay)
	quadBuilder.addVertex(topRight.x, topRight.y, topRight.z, color, u0, v1, packedLight, packedOverlay)
	quadBuilder.addVertex(bottomLeft.x, bottomLeft.y, bottomLeft.z, color, u1, v1, packedLight, packedOverlay)
	quadBuilder.addVertex(bottomRight.x, bottomRight.y, bottomRight.z, color, u1, v0, packedLight, packedOverlay)
	return quadBuilder.bakeQuad()
}

fun texturedBakedQuadTest(
	textureLocation: ResourceLocation,
	color: Int = Color.WHITE.rgb,
	useAmbientOcclusion: Boolean = false,
	topLeft: Vector3f = Vector3f(1f, 0f, 0f), // top left
	topRight: Vector3f = Vector3f(0f, 0f, 0f), // top right
	bottomLeft: Vector3f = Vector3f(1f, -1f, 0f), // bottom left
	bottomRight: Vector3f = Vector3f(0f, -1f, 0f), // bottom right
	packedLight: Int = 0xFFFFFF,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY
): BakedQuad {
	val sprite = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLocation)
	return bakedQuadTest(
		color,
		topLeft, // top left
		bottomLeft, // bottom left
		bottomRight, // bottom right
		topRight, // top right
		sprite.u0,
		sprite.v0,
		sprite.u1,
		sprite.v1,
		useAmbientOcclusion,
		packedLight,
		packedOverlay
	)
}

private fun QuadBakingVertexConsumer.addVertex(
	x: Float,
	y: Float,
	z: Float,
	color: Int,
	u: Float,
	v: Float,
	packedLight: Int,
	packedOverlay: Int
): Unit = this.addVertex(x, y, z, color, u, v, packedLight, packedOverlay, 0f, 1f, 0f)

fun renderBakedQuads(
	pose: Pose, consumer: VertexConsumer,
	red: Float, green: Float, blue: Float,
	quads: List<BakedQuad>,
	packedLight: Int, packedOverlay: Int
) {
	val light = LightTexture.pack(0, 15)
	quads.forEach {
		consumer.putBulkData(
			pose,
			it,
			floatArrayOf(1f, 1f, 1f, 1f),
			red,
			green,
			blue,
			1f,
			intArrayOf(1, 1, light, light),
			packedLight,
			false
		)
	}
}