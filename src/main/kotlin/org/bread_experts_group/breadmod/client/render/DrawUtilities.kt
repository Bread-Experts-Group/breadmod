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
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.joml.Vector3f

val TOP_LEFT: Vector3f = Vector3f(0f, 0f, 0f)
val TOP_RIGHT: Vector3f = Vector3f(1f, 0f, 0f)
val BOTTOM_LEFT: Vector3f = Vector3f(0f, -1f, 0f)
val BOTTOM_RIGHT: Vector3f = Vector3f(1f, -1f, 0f)
val CUBE_NORTH: Array<Vector3f> = arrayOf(
	Vector3f(1f, 0f, 0f),
	Vector3f(1f, 0f, -1f),
	Vector3f(1f, -1f, 0f),
	Vector3f(1f, -1f, -1f)
)
val CUBE_SOUTH: Array<Vector3f> = arrayOf(
	Vector3f(0f, 0f, -1f),
	Vector3f(0f, 0f, 0f),
	Vector3f(0f, -1f, -1f),
	Vector3f(0f, -1f, 0f)
)
val CUBE_EAST: Array<Vector3f> = arrayOf(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT)
val CUBE_WEST: Array<Vector3f> = arrayOf(
	Vector3f(1f, 0f, -1f),
	Vector3f(0f, 0f, -1f),
	Vector3f(1f, -1f, -1f),
	Vector3f(0f, -1f, -1f)
)
val CUBE_UP: Array<Vector3f> = arrayOf(
	Vector3f(1f, 0f, -1f),
	Vector3f(1f, 0f, 0f),
	Vector3f(0f, 0f, -1f),
	Vector3f(0f, 0f, 0f)
)
val CUBE_DOWN: Array<Vector3f> = arrayOf(
	Vector3f(0f, -1f, -1f),
	Vector3f(0f, -1f, 0f),
	Vector3f(1f, -1f, -1f),
	Vector3f(1f, -1f, 0f)
)

fun drawBlockAtlasCube(
	spriteLoc: ResourceLocation,
	poseStack: PoseStack,
	buffer: MultiBufferSource.BufferSource = localClient.renderBuffers().bufferSource(),
	renderType: RenderType = RenderType.debugQuads(),
	color: Int = Color.WHITE,
	north: Array<Vector3f> = CUBE_NORTH,
	south: Array<Vector3f> = CUBE_SOUTH,
	east: Array<Vector3f> = CUBE_EAST,
	west: Array<Vector3f> = CUBE_WEST,
	up: Array<Vector3f> = CUBE_UP,
	down: Array<Vector3f> = CUBE_DOWN,
	packedLight: Int = LightTexture.FULL_BRIGHT,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY
) {
	drawBlockAtlasQuad( // North / Back
		spriteLoc,
		renderType, poseStack,
		buffer, color,
		packedLight, packedOverlay,
		north[0], north[1],
		north[2], north[3]
	)
	drawBlockAtlasQuad( // South / Front
		spriteLoc,
		renderType, poseStack,
		buffer, color,
		packedLight, packedOverlay,
		south[0], south[1],
		south[2], south[3],
	)
	drawBlockAtlasQuad( // East / Right
		spriteLoc,
		renderType, poseStack,
		buffer, color,
		packedLight, packedOverlay,
		east[0], east[1],
		east[2], east[3]
	)
	drawBlockAtlasQuad( // West / Left
		spriteLoc,
		renderType, poseStack,
		buffer, color,
		packedLight, packedOverlay,
		west[0], west[1],
		west[2], west[3]
	)
	drawBlockAtlasQuad( // Up
		spriteLoc,
		renderType, poseStack,
		buffer, color,
		packedLight, packedOverlay,
		up[0], up[1],
		up[2], up[3]
	)
	drawBlockAtlasQuad( // Down
		spriteLoc,
		renderType, poseStack,
		buffer, color,
		packedLight, packedOverlay,
		down[0], down[1],
		down[2], down[3]
	)
}

fun drawCube(
	poseStack: PoseStack,
	buffer: MultiBufferSource = localClient.renderBuffers().bufferSource(),
	renderType: RenderType = RenderType.debugQuads(),
	color: Int = Color.WHITE,
	north: Array<Vector3f> = CUBE_NORTH,
	south: Array<Vector3f> = CUBE_SOUTH,
	east: Array<Vector3f> = CUBE_EAST,
	west: Array<Vector3f> = CUBE_WEST,
	up: Array<Vector3f> = CUBE_UP,
	down: Array<Vector3f> = CUBE_DOWN,
	u0: Float = 0f, v0: Float = 0f,
	u1: Float = 1f, v1: Float = 1f,
	packedLight: Int = LightTexture.FULL_BRIGHT,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY,
	extraElements: (VertexConsumer) -> Unit = {}
) {
	drawQuad( // North / Back
		poseStack, buffer,
		renderType, color,
		north[0], north[1],
		north[2], north[3],
		u0, v0,
		u1, v1,
		packedLight,
		packedOverlay,
		extraElements
	)
	drawQuad( // South / Front
		poseStack, buffer,
		renderType, color,
		south[0], south[1],
		south[2], south[3],
		u0, v0,
		u1, v1,
		packedLight,
		packedOverlay,
		extraElements
	)
	drawQuad( // East / Right
		poseStack, buffer,
		renderType, color,
		east[0], east[1],
		east[2], east[3],
		u0, v0,
		u1, v1,
		packedLight,
		packedOverlay,
		extraElements
	)
	drawQuad( // West / Left
		poseStack, buffer,
		renderType, color,
		west[0], west[1],
		west[2], west[3],
		u0, v0,
		u1, v1,
		packedLight,
		packedOverlay,
		extraElements
	)
	drawQuad( // Up
		poseStack, buffer,
		renderType, color,
		up[0], up[1],
		up[2], up[3],
		u0, v0,
		u1, v1,
		packedLight,
		packedOverlay,
		extraElements
	)
	drawQuad( // Down
		poseStack, buffer,
		renderType, color,
		down[0], down[1],
		down[2], down[3],
		u0, v0,
		u1, v1,
		packedLight, packedOverlay,
		extraElements
	)
}

fun drawBlockAtlasQuad(
	spriteLoc: ResourceLocation,
	renderType: RenderType,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	color: Int,
	packedLight: Int = LightTexture.FULL_BRIGHT,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY,
	topLeft: Vector3f = TOP_LEFT,
	topRight: Vector3f = TOP_RIGHT,
	bottomLeft: Vector3f = BOTTOM_LEFT,
	bottomRight: Vector3f = BOTTOM_RIGHT
) {
	val sprite = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLoc)
	drawQuad(
		poseStack,
		buffer,
		renderType,
		color,
		topLeft, topRight,
		bottomLeft, bottomRight,
		sprite.u0, sprite.v0,
		sprite.u1, sprite.v1,
		packedLight,
		packedOverlay
	)
}

/**
 * Draws a quad.
 *
 * @author Logan McLean
 * @since 1.0.0
 * @see drawVertex
 */
fun drawQuad(
	poseStack: PoseStack,
	buffer: MultiBufferSource = localClient.renderBuffers().bufferSource(),
	renderType: RenderType = RenderType.debugQuads(),
	color: Int = Color.WHITE,
	topLeft: Vector3f = TOP_LEFT,
	topRight: Vector3f = TOP_RIGHT,
	bottomLeft: Vector3f = BOTTOM_LEFT,
	bottomRight: Vector3f = BOTTOM_RIGHT,
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
	buffer: MultiBufferSource,
	renderType: RenderType,
	color: Int,
	x: Float,
	y: Float,
	z: Float,
	u: Float,
	v: Float,
	packedLight: Int = LightTexture.FULL_BRIGHT,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY,
	extraElements: (VertexConsumer) -> Unit = {}
) {
	val (x, y, z) = poseStack.last().pose().transformPosition(x, y, z, Vector3f())
	drawVertex(x, y, z, buffer, renderType, color, u, v, packedLight, packedOverlay, extraElements)
}

fun drawVertex(
	x: Float,
	y: Float,
	z: Float,
	buffer: MultiBufferSource,
	renderType: RenderType,
	color: Int,
	u: Float,
	v: Float,
	packedLight: Int = LightTexture.FULL_BRIGHT,
	packedOverlay: Int = OverlayTexture.NO_OVERLAY,
	extraElements: (VertexConsumer) -> Unit = {}
) {
	val consumer = buffer.getBuffer(renderType)
	consumer.addVertex(x, y, z)
		.setColor(color)
		.setUv(u, v)
		.setOverlay(packedOverlay)
		.setLight(packedLight)
		.setNormal(0f, 1f, 0f)
		.also(extraElements)
}