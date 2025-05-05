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
import org.bread_experts_group.breadmod.util.Vector3fAxisX
import org.bread_experts_group.breadmod.util.Vector3fAxisZ
import org.bread_experts_group.breadmod.util.Vector3fZero
import org.joml.Vector3f
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
	color: Int = Color.WHITE.rgb,
	topLeft: Vector3f = Vector3fAxisX,
	topRight: Vector3f = Vector3fZero,
	bottomLeft: Vector3f = Vector3fAxisZ,
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
	color: Int,
	topLeft: Vector3f,
	topRight: Vector3f,
	bottomLeft: Vector3f,
	bottomRight: Vector3f,
	u0: Float, v0: Float,
	u1: Float, v1: Float,
	packedLight: Int = LightTexture.FULL_BRIGHT,
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
	color: Int,
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
		.setColor(color)
		.setUv(u, v)
		.setOverlay(packedOverlay)
		.setLight(packedLight)
		.setNormal(0f, 1f, 0f)
}

private val quadBuilder: QuadBakingVertexConsumer = QuadBakingVertexConsumer()

/**
 * Creates a [BakedQuad].
 */
fun buildBakedQuad(
	color: Int,
	topLeft: Vector3f = Vector3fAxisX, // top left
	topRight: Vector3f = Vector3fZero, // top right
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

/**
 * Creates a textured [BakedQuad]
 * @see buildBakedQuad
 */
fun buildTexturedBakedQuad(
	textureLocation: ResourceLocation,
	color: Int = Color.WHITE.rgb,
	useAmbientOcclusion: Boolean = false,
	topLeft: Vector3f = Vector3fAxisX, // top left
	topRight: Vector3f = Vector3fZero, // top right
	bottomLeft: Vector3f = Vector3f(1f, -1f, 0f), // bottom left
	bottomRight: Vector3f = Vector3f(0f, -1f, 0f), // bottom right
	packedLight: Int = 0xFFFFFF,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY
): BakedQuad {
	val sprite = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLocation)
	return buildBakedQuad(
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

@Suppress("SameParameterValue")
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

// todo work in progress
fun renderBakedQuads(
	pose: Pose, consumer: VertexConsumer,
	red: Float, green: Float, blue: Float,
	quads: List<BakedQuad>,
	packedLight: Int
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
			intArrayOf(light, light, light, light),
			packedLight,
			false
		)
	}
}