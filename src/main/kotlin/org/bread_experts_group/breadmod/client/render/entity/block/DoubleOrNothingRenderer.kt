package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
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
import org.bread_experts_group.breadmod.client.render.setDirection
import org.bread_experts_group.breadmod.client.render.setSpeed
import org.bread_experts_group.breadmod.client.render.solidColorTexture
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.block.handler.LerpTickerHandler.Companion.getLerpTicker
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.CASHOUT
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.DOUBLE_COUNTER
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.JACKPOT
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.NOTHING
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler.Companion.USE_NEGATIVE_TILT
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.toVec3
import org.joml.Vector3f

class DoubleOrNothingRenderer(
	context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<BreadModBlockEntity> {
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
		Color.color(255, 57),
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

	@Suppress("UNCHECKED_CAST")
	private fun BreadModBlockEntity.getStateHandler(): DoubleOrNothingStateHandler = this.getCapability(
		DoubleOrNothingStateHandler.BLOCK_VOID
	) as DoubleOrNothingStateHandler

	override fun render(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val half = blockEntity.blockState.getValue(ModBlockStateProperties.TRIPLE_BLOCK)
		if (half != ModBlockStateProperties.TripleBlockHalf.LOWER) return

		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState)
		this.renderOuterBG(poseStack, bufferSource, blockEntity)
		this.renderInnerBG(poseStack, bufferSource, blockEntity, this.backgroundTexture)
		val state = blockEntity.getStateHandler()
		context(blockEntity, partialTick, poseStack, bufferSource) {
//			this.setGlobalTextRotation(blockEntity, poseStack, partialTick)
			when {
				state.get(DOUBLE_COUNTER) > 0 -> this.drawTextNew(
					Component.literal("${state.get(DOUBLE_COUNTER)}x"),
					y = 15f,
					scale = 0.05f,
					color = this.colors[state.get(DOUBLE_COUNTER) - 1]
				)
				state.get(NOTHING)            -> this.drawTextNew(
					Component.literal("NOTHING"),
					y = 13f,
					scale = 0.018f,
					color = Color.RED
				)
				else                          -> this.drawTextNew(
					Component.literal("PRESS DOUBLE TO START"),
					y = 13f,
					color = Color.WHITE,
					maxWidth = 80
				)
			}
		}
		poseStack.popPose()
	}

	private val direction: Vec2 = Vec2(1.0f, -1.0f)
	fun renderOuterBG(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		blockEntity: BreadModBlockEntity,
	) {
		poseStack.pushPose()
		poseStack.translateDiv16(this.outerBGOffset)
		val state = blockEntity.getStateHandler()
		val outerBGRenderType =
			if (!state.get(NOTHING) && !state.get(JACKPOT) && !state.get(CASHOUT)) ModRenderType.RAINBOW
			else RenderType.text(this.colorableTexture)
		val outerBGColor =
			if (state.get(CASHOUT)) this.cashoutBGColor
			else if (state.get(NOTHING)) this.nothingBGColor
			else Color.WHITE
		val speed = when (state.get(DOUBLE_COUNTER)) {
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
			if (outerBGRenderType == ModRenderType.RAINBOW) consumer.setSpeed(speed).setDirection(this.direction)
		}
		poseStack.popPose()
	}

	fun renderInnerBG(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		blockEntity: BreadModBlockEntity,
		texture: ResourceLocation,
	) {
		val state = blockEntity.getStateHandler()
		val bgColor = if (state.get(DOUBLE_COUNTER) == 0 && !state.get(NOTHING)) this.defaultBGColor
		else if (!state.get(NOTHING)) this.colors[8 - (state.get(DOUBLE_COUNTER) - 1)]
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

	override fun getRenderBoundingBox(blockEntity: BreadModBlockEntity): AABB {
		val pos = blockEntity.blockPos.toVec3()
		return AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 2.0, pos.z + 1)
	}

	fun setGlobalTextRotation(
		blockEntity: BreadModBlockEntity,
		poseStack: PoseStack,
		partialTick: Float
	) {
		val lerp = blockEntity.getLerpTicker<LerpLabels>()
		val state = blockEntity.getStateHandler()
		val rawPositive = lerp.getRawValue(LerpLabels.TILT_P)
		val tiltPositive = if (rawPositive == 0f) 0f else lerp.getLerpedValue(LerpLabels.TILT_P, partialTick)
		val rawNegative = lerp.getRawValue(LerpLabels.TILT_N)
		val tiltNegative = if (rawNegative == 0f) 0f else lerp.getLerpedValue(LerpLabels.TILT_N, partialTick)
		val tilt = if (state.get(USE_NEGATIVE_TILT)) tiltNegative else tiltPositive

		poseStack.translateDiv16(8f, 8f, 0f)
		poseStack.mulPose(Axis.ZN.rotationDegrees(tilt))
		poseStack.translateDiv16(-8f, -8f, 0f)
	}

	context(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource
	)
	fun drawTextNew(
		text: Component,
		x: Float = 0f,
		y: Float = 0f,
		scale: Float = 0.01f,
		color: Int = Color.WHITE,
		maxWidth: Int = 75
	) {
		val font = localClient.font
		val split = font.split(text, maxWidth)
		val splitSize = split.size.toFloat()
		val state = blockEntity.getStateHandler()
		val lerp = blockEntity.getLerpTicker<LerpLabels>()
		val rawPositive = lerp.getRawValue(LerpLabels.TILT_P)
		val tiltPositive = if (rawPositive == 0f) 0f else lerp.getLerpedValue(LerpLabels.TILT_P, partialTick)
		val rawNegative = lerp.getRawValue(LerpLabels.TILT_N)
		val tiltNegative = if (rawNegative == 0f) 0f else lerp.getLerpedValue(LerpLabels.TILT_N, partialTick)
		val tilt = if (state.get(USE_NEGATIVE_TILT)) tiltNegative else tiltPositive
		val zoom = lerp.getLerpedValue(LerpLabels.ZOOM, partialTick)

		poseStack.pushPose()
		// set the initial position of the text, use x and y to adjust
		poseStack.translateDiv16(8f + x, 20.5f - y, -8.95f)
		// set the text tilt with the block entity's lerped tilt
		if (splitSize == 1f) poseStack.translate(0f, scale * 2, 0f)
		poseStack.mulPose(Axis.ZP.rotationDegrees(tilt))
		if (splitSize == 1f) poseStack.translate(0f, -scale * 2, 0f)
		// flip the text around so it isn't facing backwards
		poseStack.mulPose(Axis.XN.rotationDegrees(180f))
		// set the text scale with the block entity's lerped zoom
		if (splitSize == 1f) poseStack.translate(0f, -scale * 2, 0f)
		poseStack.scaleFlat(1f + zoom)
		if (splitSize == 1f) poseStack.translate(0f, scale * 2, 0f)
		var splitYOffset = 0f
		repeat(splitSize.toInt()) {
			val component = split[it]
			val center = (-font.width(component).toFloat() / 2f) * scale
			poseStack.pushPose()
			// position the split text below the previous text
			poseStack.translateDiv16(0f, splitYOffset, 0f)
			// center the text on the screen
			poseStack.translate(center, 0f, 0f)
			// correct position before scaling
			poseStack.translate(0f, -(scale * 8), 0f)
			poseStack.scaleFlat(scale)

			font.renderText(
				component,
				color,
				Color.color(a = 0),
				poseStack,
				bufferSource,
				true,
				LightTexture.FULL_BRIGHT,
				-0.03f
			)
			poseStack.popPose()
			splitYOffset += scale * 135
		}
		poseStack.popPose()
	}

	enum class LerpLabels {
		ZOOM,
		TILT_P,
		TILT_N
	}
}