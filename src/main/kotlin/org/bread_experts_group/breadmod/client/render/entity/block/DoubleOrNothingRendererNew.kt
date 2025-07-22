package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderText
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.solidColorTexture
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntityNew
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.toVec3
import org.joml.Vector3f
import org.lwjgl.system.MemoryUtil

class DoubleOrNothingRendererNew(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntityNew> {
	// Background vertex positions
	private val outerBGVertexes: Array<Vector3f> = arrayOf(
		Vector3f(0.05f, 0f, 0f), // top left
		Vector3f(0.95f, 0f, 0f), // top right
		Vector3f(0.05f, -1.8f, 0f), // bottom left
		Vector3f(0.95f, -1.8f, 0f) // bottom right
	)
	private val innerBGVertexes: Array<Vector3f> = arrayOf(
		Vector3f(0.075f, 0f, 0f), // top left
		Vector3f(0.925f, 0f, 0f), // top right
		Vector3f(0.075f, -1.755f, 0f), // bottom left
		Vector3f(0.925f, -1.755f, 0f) // bottom right
	)

	// Colors
	private val colors: IntArray = intArrayOf(
		Color.color(255, 57, 0),
		Color.color(255, 79, 59),
		Color.color(255, 59, 106),
		Color.color(255, 59, 135),
		Color.color(255, 59, 153),
		Color.color(232, 70, 170),
		Color.color(198, 53, 173),
		Color.color(198, 53, 201),
		Color.color(164, 73, 227),
		Color.BLACK
	)
	private val nothingBGColor: Int = Color.color(128)
	private val defaultBGColor: Int = Color.color(102, 204, 102)
	private val cashoutBGColor: Int = Color.color(52, 240)
	private val jackpotBGColor: Int = Color.color(255, 207)
	private val jackpotTextColor: Int = Color.color(255, 214, 38)

	// Textures
	private val backgroundTexture: ResourceLocation = modLocation("textures/block/double_or_nothing/background.png")
	private val blockhead: ResourceLocation = modLocation("textures/tool_gun/gui/blockhead.png")
	private val colorableTexture: ResourceLocation = solidColorTexture(Color.WHITE, "double_or_nothing", 14, 28)
	private val blueScreenTexture: ResourceLocation =
		modLocation("textures/block/double_or_nothing/background_bluescreen.png")

	// Offsets
	private val outerBGOffset: Vec3 = Vec3(0.0, 22.0, -9.0)
	private val innerBGOffset: Vec3 = Vec3(0.0, 21.8, -8.99)

	override fun render(
		blockEntity: DoubleOrNothingBlockEntityNew,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val half = blockEntity.blockState.getValue(ModBlockStateProperties.TRIPLE_BLOCK_HALF)
		if (half != ModBlockStateProperties.TripleBlockHalf.LOWER) return

		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState)
		this.renderOuterBG(poseStack, bufferSource, blockEntity)
		this.renderInnerBG(poseStack, bufferSource, blockEntity, this.backgroundTexture)
		context(blockEntity, partialTick, poseStack, bufferSource) {
//			this.setGlobalTextRotation(blockEntity, poseStack, partialTick)
			when {
				blockEntity.doubleCounter > 0 -> this.drawText(
					Component.literal("${blockEntity.doubleCounter}x"),
					5.5f,
					0.04f,
					this.colors[blockEntity.doubleCounter - 1]
				)
				else                          -> this.drawText(
					Component.literal("PRESS DOUBLE TO START"),
					4.5f,
					0.01f,
					Color.WHITE,
					maxWidth = 80
				)
			}
		}
		poseStack.popPose()
	}

	private fun VertexConsumer.setDirection(direction: Vec2): VertexConsumer {
		val builder = this as? BufferBuilder ?: throw AssertionError("current consumer is not BufferBuilder!")
		val i = builder.beginElement(ModRenderType.DIRECTION_VERTEX_ELEMENT)
		if (i != -1L) {
			MemoryUtil.memPutFloat(i, direction.x)
			MemoryUtil.memPutFloat(i + 4L, direction.y)
		}
		return this
	}

	private fun VertexConsumer.setSpeed(speed: Float): VertexConsumer {
		val builder = this as? BufferBuilder ?: throw AssertionError("current consumer is not BufferBuilder!")
		val i = builder.beginElement(ModRenderType.SPEED_VERTEX_ELEMENT)
		if (i != -1L) MemoryUtil.memPutFloat(i, speed)
		return this
	}

	private val direction: Vec2 = Vec2(1.0f, -1.0f)
	fun renderOuterBG(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		blockEntity: DoubleOrNothingBlockEntityNew,
	) {
		poseStack.pushPose()
		poseStack.translateDiv16(this.outerBGOffset)
		val outerBGRenderType =
			if (!blockEntity.nothing && !blockEntity.jackpot && !blockEntity.cashout) ModRenderType.rainbow()
			else RenderType.text(this.colorableTexture)
		val outerBGColor =
			if (blockEntity.cashout) this.cashoutBGColor
			else if (blockEntity.nothing) this.nothingBGColor
			else Color.WHITE
		val speed = when (blockEntity.doubleCounter) {
			5    -> 1300f
			6    -> 1400f
			7    -> 1600f
			8    -> 1900f
			9    -> 2300f
			10   -> 2400f
			else -> 1000f
		}

		drawQuad(
			poseStack,
			bufferSource,
			outerBGRenderType,
			outerBGColor,
			this.outerBGVertexes[0],
			this.outerBGVertexes[1],
			this.outerBGVertexes[2],
			this.outerBGVertexes[3]
		) { consumer ->
			if (outerBGRenderType == ModRenderType.rainbow()) consumer.setSpeed(speed).setDirection(this.direction)
		}
		poseStack.popPose()
	}

	fun renderInnerBG(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		blockEntity: DoubleOrNothingBlockEntityNew,
		texture: ResourceLocation,
	) {
		val bgColor = if (blockEntity.doubleCounter == 0 && !blockEntity.nothing) this.defaultBGColor
		else if (!blockEntity.nothing) this.colors[8 - (blockEntity.doubleCounter - 1)]
		else this.nothingBGColor

		poseStack.pushPose()
		poseStack.translateDiv16(this.innerBGOffset)
		drawQuad(
			poseStack,
			bufferSource,
			RenderType.text(texture),
			bgColor,
			this.innerBGVertexes[0],
			this.innerBGVertexes[1],
			this.innerBGVertexes[2],
			this.innerBGVertexes[3]
		)
		poseStack.popPose()
	}

	override fun getRenderBoundingBox(blockEntity: DoubleOrNothingBlockEntityNew): AABB {
		val pos = blockEntity.blockPos.toVec3()
		return AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 2.0, pos.z + 1)
	}

	fun setLocalZoom(
		blockEntity: DoubleOrNothingBlockEntityNew,
		poseStack: PoseStack,
		partialTick: Float,
		centeringOffset: Float,
		offset: Int,
		scale: Float
	) {
		val rawZoom = blockEntity.getRawValue(0)
		val zoom = if (rawZoom == 0f) 0f else blockEntity.getLerpedValue(0, partialTick)
		val zoomOffset = offset.toFloat() / 11f
		poseStack.translate(-centeringOffset, -(zoomOffset - scale - 0.1f), 0f)
		poseStack.scaleFlat(1f + zoom)
		poseStack.translate(centeringOffset, zoomOffset - scale - 0.1f, 0f)
	}

//	fun setTextZoom(blockEntity: DoubleOrNothingBlockEntityNew, poseStack: PoseStack, partialTick: Float) {
//		val rawZoom = blockEntity.getRawValue(0)
//		val zoom = if (rawZoom == 0f) 0f else blockEntity.getLerpedValue(0, partialTick)
//		poseStack.translate(0f, 0.25f, 0f)
//		poseStack.scaleFlat(1f + zoom)
//		poseStack.translate(0f, -0.25f, 0f)
//	}

	fun setTextRotation(
		blockEntity: DoubleOrNothingBlockEntityNew,
		poseStack: PoseStack,
		partialTick: Float
	) {
		val rawPositive = blockEntity.getRawValue(1)
		val tiltPositive = if (rawPositive == 0f) 0f else blockEntity.getLerpedValue(1, partialTick)
		val rawNegative = blockEntity.getRawValue(2)
		val tiltNegative = if (rawNegative == 0f) 0f else blockEntity.getLerpedValue(2, partialTick)
		val tilt = if (blockEntity.useNegativeTilt) tiltNegative else tiltPositive
		poseStack.mulPose(Axis.ZN.rotationDegrees(tilt))
	}

	fun setGlobalTextRotation(
		blockEntity: DoubleOrNothingBlockEntityNew,
		poseStack: PoseStack,
		partialTick: Float
	) {
		val rawPositive = blockEntity.getRawValue(1)
		val tiltPositive = if (rawPositive == 0f) 0f else blockEntity.getLerpedValue(1, partialTick)
		val rawNegative = blockEntity.getRawValue(2)
		val tiltNegative = if (rawNegative == 0f) 0f else blockEntity.getLerpedValue(2, partialTick)
		val tilt = if (blockEntity.useNegativeTilt) tiltNegative else tiltPositive

		poseStack.translateDiv16(8f, 8f, 0f)
		poseStack.mulPose(Axis.ZN.rotationDegrees(tilt))
		poseStack.translateDiv16(-8f, -8f, 0f)
	}

	context(
		blockEntity: DoubleOrNothingBlockEntityNew,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource
	)
	fun drawText(
		text: Component,
		y: Float,
		scale: Float,
		color: Int,
		useZoom: Boolean = true,
		useTilt: Boolean = true,
		maxWidth: Int = 86
	) {
		val font = localClient.font
		var yOffset = y
		var zoomOffset = 0
		poseStack.pushPose()
		poseStack.translateDiv16(8f, y, -8.98f)
		poseStack.translate(0.0, 0.2 + scale, 0.0)
		if (useTilt) this.setTextRotation(blockEntity, poseStack, partialTick)
		poseStack.translate(0.0, -0.2 + scale, 0.0)
//		if (useZoom) this.setTextZoom(blockEntity, poseStack, partialTick)
		for (sequence in font.split(text, maxWidth)) {
			val center = (-font.width(sequence).toFloat() / 2f) * scale
			poseStack.pushPose()
			poseStack.translateDiv16(0f, yOffset, 0f)
			poseStack.translate(center, 0f, 0f)
			poseStack.mulPose(Axis.XN.rotationDegrees(180f))
			if (useZoom) this.setLocalZoom(blockEntity, poseStack, partialTick, center, zoomOffset, scale)
			poseStack.scaleFlat(scale)
			font.renderText(
				sequence,
				color,
				Color.color(a = 0),
				poseStack,
				bufferSource,
				true,
				LightTexture.FULL_BRIGHT,
				-0.03f
			)
			yOffset -= 1.4f
			zoomOffset++
			poseStack.popPose()
		}
		poseStack.popPose()
	}
}