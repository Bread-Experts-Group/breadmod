package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.core.Direction
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.WEST
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.drawTextOnBlockSide
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.tessellateModel
import org.bread_experts_group.breadmod.registry.block.actual.DoubleOrNothingBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.LOWER
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Vector3fAxisZ
import org.bread_experts_group.breadmod.util.Vector3fZero
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.toYRotFixed
import org.joml.Vector3f
import org.lwjgl.system.MemoryUtil
import java.awt.Color

class DoubleOrNothingRenderer(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntity> {
	private val random: RandomSource = RandomSource.create()
	private val modelData: ModelData = ModelData.EMPTY
	private val vertexes: Array<Vector3f> = arrayOf(
		Vector3f(0.95f, 2.375f, 0.0f),
		Vector3f(0.05f, 2.375f, 0f),
		Vector3f(0.95f, 0.55f, 0f),
		Vector3f(0.05f, 0.55f, 0f)
	)
	private val colors: IntArray = intArrayOf(
		Color(255, 57, 0).rgb,
		Color(255, 79, 59).rgb,
		Color(255, 59, 106).rgb,
		Color(255, 59, 135).rgb,
		Color(255, 59, 153).rgb,
		Color(232, 70, 170).rgb,
		Color(198, 53, 173).rgb,
		Color(198, 53, 201).rgb,
		Color(164, 73, 227).rgb,
		Color.BLACK.rgb
	)
	private val backgroundTexture: ResourceLocation = modLocation("textures/block/double_or_nothing/background.png")
	private val blockhead: ResourceLocation = modLocation("textures/tool_gun/gui/blockhead.png")
	private val redTexture: ResourceLocation = modLocation("textures/block/double_or_nothing/background_colorable.png")
	private val bluesScreenTexture: ResourceLocation =
		modLocation("textures/block/double_or_nothing/background_bluescreen.png")
	private val nothingColor: Int = Color(128, 0, 0).rgb
	private val good1Color: Int = Color(102, 204, 102).rgb
	private val good2Color: Int = Color(52, 240, 0).rgb
	private val jackpotColor: Int = Color(255, 214, 38).rgb
	private val jackpotBGColor: Int = Color(255, 207, 0).rgb

	override fun render(
		blockEntity: DoubleOrNothingBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val partial = localClient.timer.gameTimeDeltaTicks
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val originalModel = this.context.blockRenderDispatcher.getBlockModel(blockEntity.blockState)
		val blockHeadMode = if (blockEntity.blockhead) this.blockhead else this.backgroundTexture
		localClient.blockRenderer.modelRenderer.tessellateModel(
			blockEntity,
			originalModel,
			poseStack,
			bufferSource,
			this.random,
			packedOverlay,
			this.modelData
		)
		if (blockEntity.blockState.getValue(DoubleOrNothingBlock.TRIPLE_HALF) != LOWER) return
		poseStack.pushPose()
		if (blockEntity.rewired) this.drawText(
			poseStack,
			"REWIRED",
			Color.RED.rgb,
			blockEntity,
			0.1,
			1.35,
			bufferSource
		)
		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
		if (blockEntity.counter != 10 && !blockEntity.cashout) {
			if (!blockEntity.nothing) this.drawRainbowQuad(poseStack, bufferSource, blockEntity, blockRotation)
			else this.drawColorable(poseStack, bufferSource, blockRotation, Color.RED.rgb)
			val bgColor = if (blockEntity.counter == 0 && !blockEntity.nothing) this.good1Color
			else if (!blockEntity.nothing) this.colors[8 - (blockEntity.counter - 1)] else this.nothingColor
			this.drawBackground(poseStack, bufferSource, blockRotation, blockHeadMode, bgColor)
			// pop the pose for block rotation cause the text is translated onto the side automatically.
			poseStack.popPose()

			if (blockEntity.timeStarted > 0 && !blockEntity.nothing)
				this.drawMultiplier(poseStack, blockEntity, blockRotation, partial, bufferSource)
			else if (!blockEntity.nothing)
				this.drawDefaultText(poseStack, blockEntity, blockRotation, partial, bufferSource)
			else this.drawNothing(poseStack, blockEntity, blockRotation, partial, bufferSource)

			this.drawCurrentPlayer(poseStack, blockEntity, bufferSource)
		} else if (blockEntity.cashout) {
			this.drawColorable(poseStack, bufferSource, blockRotation, this.good2Color)
			this.drawBackground(
				poseStack,
				bufferSource,
				blockRotation,
				blockHeadMode,
				this.good1Color
			)
			poseStack.popPose()

			blockEntity.tilt = 0f
			this.doTiltAndShake(poseStack, blockEntity, blockRotation, partial, 1f, 1f)
			this.drawText(poseStack, "CASH", Color.WHITE.rgb, blockEntity, 0.25, 0.70, bufferSource, 0.02f, -0.540)
			this.drawText(poseStack, "OUT", Color.WHITE.rgb, blockEntity, 0.325, 0.50, bufferSource, 0.02f, -0.540)
		} else {
			poseStack.popPose()
			this.drawJackpot(poseStack, blockEntity, partial, bufferSource, blockRotation)
		}
	}

	private fun drawText(
		poseStack: PoseStack,
		text: String,
		color: Int,
		blockEntity: DoubleOrNothingBlockEntity,
		x: Double,
		y: Double,
		bufferSource: MultiBufferSource,
		scale: Float = 0.01f,
		z: Double = -0.561,
	) {
		poseStack.drawTextOnBlockSide(
			localClient.font,
			Component.literal(text),
			x,
			y,
			z,
			bufferSource,
			blockEntity.blockState,
			scale = scale,
			color = color,
			dropShadow = true,
			dropShadowOffset = -0.03f
		)
	}

	private fun VertexConsumer.setSpeed(speed: Float): VertexConsumer {
		val builder = this as? BufferBuilder ?: throw AssertionError("current consumer is not BufferBuilder!")
		val i = builder.beginElement(ModRenderType.SPEED_VERTEX_ELEMENT)
		if (i != -1L) MemoryUtil.memPutFloat(i, speed)
		return this
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

	// todo fix the messed up PoseStack translations with translateDiv16
	private val direction: Vec2 = Vec2(1.0f, -1.0f)
	private fun drawRainbowQuad(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		blockEntity: DoubleOrNothingBlockEntity,
		blockRotation: Direction
	) {
		poseStack.pushPose()
		val topLeft: Vector3f = this.vertexes[0]
		val topRight: Vector3f = this.vertexes[1]
		val bottomLeft: Vector3f = this.vertexes[2]
		val bottomRight: Vector3f = this.vertexes[3]
		val buffer = bufferSource.getBuffer(ModRenderType.rainbow())
		val pose = poseStack.last().pose()
		when (blockRotation) {
			NORTH -> poseStack.translate(0.0, 0.0, 0.562)
			SOUTH -> poseStack.translate(-1.0, 0.0, -0.438)
			EAST  -> poseStack.translate(0.0, 0.0, -0.438)
			WEST  -> poseStack.translate(-1.0, 0.0, 0.562)
			else  -> {}
		}
		val speed = when (blockEntity.counter) {
			5    -> 1300f
			6    -> 1400f
			7    -> 1600f
			8    -> 1900f
			9    -> 2300f
			10   -> 2400f
			else -> 1000f
		}
		buffer.addVertex(pose, topLeft.x, topLeft.y, topLeft.z).setUv(0f, 0f)
			.setSpeed(speed).setDirection(this.direction)
		buffer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z).setUv(0f, 1f)
			.setSpeed(speed).setDirection(this.direction)
		buffer.addVertex(pose, bottomRight.x, bottomRight.y, bottomRight.z).setUv(1f, 1f)
			.setSpeed(speed).setDirection(this.direction)
		buffer.addVertex(pose, topRight.x, topRight.y, topRight.z).setUv(1f, 0f)
			.setSpeed(speed).setDirection(this.direction)
		poseStack.popPose()
	}

	private fun drawBackground(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		blockRotation: Direction,
		texture: ResourceLocation,
		color: Int
	) {
		poseStack.pushPose()
		when (blockRotation) {
			NORTH -> poseStack.translate(0.073, 2.365, 0.5618)
			SOUTH -> poseStack.translate(-0.926, 2.365, -0.439)
			WEST  -> poseStack.translate(-0.926, 2.365, 0.5618)
			EAST  -> poseStack.translate(0.073, 2.365, -0.439)
			else  -> {}
		}
		poseStack.scaleFlat(0.88f)
		poseStack.scale(0.97f, 1f, 1f)
		poseStack.mulPose(Axis.YP.rotationDegrees(90f))
		poseStack.mulPose(Axis.ZN.rotationDegrees(90f))
		drawQuad(
			poseStack,
			bufferSource,
			RenderType.text(texture),
			color,
			Vector3fAxisZ,
			Vector3fZero,
			Vector3f(2f, 0f, 1f),
			Vector3f(2f, 0f, 0f),
			0f,
			0f,
			1f,
			1f
		)
		poseStack.popPose()
	}

	private fun drawColorable(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		blockRotation: Direction,
		color: Int
	) {
		poseStack.pushPose()
		when (blockRotation) {
			NORTH -> poseStack.translate(0.0, 2.375, 0.562)
			SOUTH -> poseStack.translate(-1.0, 2.375, -0.438)
			EAST  -> poseStack.translate(0.0, 2.375, -0.438)
			WEST  -> poseStack.translate(-1.0, 2.375, 0.562)
			else  -> {}
		}
		poseStack.scaleFlat(1f)
		poseStack.mulPose(Axis.YP.rotationDegrees(90f))
		poseStack.mulPose(Axis.ZN.rotationDegrees(90f))
		drawQuad(
			poseStack,
			bufferSource,
			RenderType.text(this.redTexture),
			color,
			Vector3fAxisZ,
			Vector3fZero,
			Vector3f(2f, 0f, 1f),
			Vector3f(2f, 0f, 0f),
			0f,
			0f,
			1f,
			1f
		)
		poseStack.popPose()
	}

	override fun getRenderBoundingBox(blockEntity: DoubleOrNothingBlockEntity): AABB {
		val pos = blockEntity.blockPos.toVec3()
		return AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 2.0, pos.z + 1)
	}

	private fun drawMultiplier(
		poseStack: PoseStack,
		blockEntity: DoubleOrNothingBlockEntity,
		blockRotation: Direction,
		partialTick: Float,
		bufferSource: MultiBufferSource
	) {
		poseStack.pushPose()
		val multiplier = when (blockEntity.counter) {
			7    -> 0.7f
			8    -> 0.4f
			9    -> 0.3f
			else -> 1f
		}
		this.doTiltAndShake(poseStack, blockEntity, blockRotation, partialTick, multiplier, multiplier)
		this.drawText(
			poseStack,
			"${blockEntity.counter}x",
			this.colors[Math.clamp(blockEntity.counter - 1f, 0f, 9f).toInt()],
			blockEntity,
			0.20,
			0.70,
			bufferSource,
			0.05f,
			-0.540
		)
		poseStack.popPose()
	}

	private fun doTiltAndShake(
		poseStack: PoseStack,
		blockEntity: DoubleOrNothingBlockEntity,
		blockRotation: Direction,
		partialTick: Float,
		zoomMulti: Float,
		tiltMulti: Float
	) {
		val shakeX = blockEntity.random.nextInt(0, 2)
		val shakeY = blockEntity.random.nextInt(0, 4)

		poseStack.translate(0.5, 1.5, 0.5)
		poseStack.scaleFlat(1f + blockEntity.zoom)

		if (blockRotation == WEST || blockRotation == EAST) {
			poseStack.mulPose(Axis.XN.rotationDegrees(blockEntity.tilt))
		} else poseStack.mulPose(Axis.ZN.rotationDegrees(blockEntity.tilt))

		if (blockEntity.zoom > 0f) blockEntity.zoom -= (0.1f * (zoomMulti - 0.1f)) * partialTick

		if (blockEntity.tilt != 0f) {
			if (blockEntity.tilt > 0f) blockEntity.tilt -= (2f * tiltMulti) * partialTick
			if (blockEntity.tilt < 0f) blockEntity.tilt += (2f * tiltMulti) * partialTick
		}

		if (blockEntity.counter > 5 && (blockEntity.tilt > 0.9f || blockEntity.tilt < -0.9f)) {
			val shake = (shakeX / (52.0 * tiltMulti))
			poseStack.translate(
				if (blockRotation == NORTH || blockRotation == SOUTH) shake * partialTick else 0.0,
				(shakeY / (64.0 * tiltMulti)) * partialTick,
				if (blockRotation == WEST || blockRotation == EAST) shake * partialTick else 0.0
			)
		}

		poseStack.translate(-0.5, -1.5, -0.5)
	}

	private fun drawDefaultText(
		poseStack: PoseStack,
		blockEntity: DoubleOrNothingBlockEntity,
		blockRotation: Direction,
		partialTick: Float,
		bufferSource: MultiBufferSource
	) {
		poseStack.pushPose()
		this.doTiltAndShake(poseStack, blockEntity, blockRotation, partialTick, 1f, 1f)
		this.drawText(poseStack, "PRESS DOUBLE", Color.WHITE.rgb, blockEntity, 0.15, 0.55, bufferSource, z = -0.545)
		this.drawText(poseStack, "TO START", Color.WHITE.rgb, blockEntity, 0.275, 0.45, bufferSource, z = -0.545)
		poseStack.popPose()
	}

	private fun drawNothing(
		poseStack: PoseStack,
		blockEntity: DoubleOrNothingBlockEntity,
		blockRotation: Direction,
		partialTick: Float,
		bufferSource: MultiBufferSource
	) {
		poseStack.pushPose()
		this.doTiltAndShake(poseStack, blockEntity, blockRotation, partialTick, 1f, 1f)
		this.drawText(poseStack, "NOTHING", Color.RED.rgb, blockEntity, 0.19, 0.55, bufferSource, 0.015f, -0.540)
		poseStack.popPose()
	}

	private fun drawCurrentPlayer(
		poseStack: PoseStack,
		blockEntity: DoubleOrNothingBlockEntity,
		bufferSource: MultiBufferSource
	) {
		this.drawText(
			poseStack,
			"current player: ${blockEntity.currentPlayer?.name?.string ?: "none"}",
			Color.WHITE.rgb,
			blockEntity,
			0.08,
			-0.35,
			bufferSource,
			0.005f,
		)
	}

	private fun drawJackpot(
		poseStack: PoseStack,
		blockEntity: DoubleOrNothingBlockEntity,
		partialTick: Float,
		bufferSource: MultiBufferSource,
		blockRotation: Direction
	) {
		val timer = blockEntity.jackpotTimer
		val blockHeadMode = if (blockEntity.blockhead) this.blockhead else this.backgroundTexture

		if (timer in 0 .. 70) {
			poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
			blockEntity.tilt = 0f
			blockEntity.zoom = 0f
			this.drawColorable(poseStack, bufferSource, blockRotation, Color.RED.rgb)
			this.drawBackground(poseStack, bufferSource, blockRotation, this.bluesScreenTexture, Color.WHITE.rgb)
		} else if (timer in 70 .. 110) {
			this.drawText(
				poseStack,
				"Don't worry",
				Color.WHITE.rgb,
				blockEntity,
				0.20,
				0.55,
				bufferSource
			)
		} else if (timer in 110 .. 150) {
			this.drawText(
				poseStack,
				"This isn't a glitch",
				Color.WHITE.rgb,
				blockEntity,
				0.074,
				0.55,
				bufferSource
			)
		} else if (timer in 150 .. 190) {
			this.drawText(poseStack, "I've got", Color.WHITE.rgb, blockEntity, 0.33, 0.65, bufferSource)
			this.drawText(poseStack, "something to", Color.WHITE.rgb, blockEntity, 0.20, 0.55, bufferSource)
			this.drawText(poseStack, "tell you", Color.WHITE.rgb, blockEntity, 0.33, 0.45, bufferSource)
		} else if (timer in 190 .. 230) {
			this.drawText(poseStack, "Something", Color.WHITE.rgb, blockEntity, 0.27, 0.61, bufferSource)
			this.drawText(poseStack, "important", Color.WHITE.rgb, blockEntity, 0.27, 0.51, bufferSource)
		} else if (timer in 230 .. 267) {
			this.drawText(poseStack, "Congratulations", Color.WHITE.rgb, blockEntity, 0.1, 0.61, bufferSource)
			this.drawText(
				poseStack,
				"${blockEntity.currentPlayer?.name?.string}",
				Color.WHITE.rgb,
				blockEntity,
				0.22,
				0.51,
				bufferSource
			)
		} else if (timer in 267 .. 300) {
			this.drawText(poseStack, "you've just", Color.WHITE.rgb, blockEntity, 0.24, 0.61, bufferSource)
			this.drawText(poseStack, "won the...", Color.WHITE.rgb, blockEntity, 0.26, 0.51, bufferSource)
		} else if (timer > 300) {
			poseStack.pushPose()
			poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
			this.drawRainbowQuad(poseStack, bufferSource, blockEntity, blockRotation)
			this.drawBackground(
				poseStack,
				bufferSource,
				blockRotation,
				blockHeadMode,
				this.jackpotBGColor
			)
			poseStack.popPose()
			// todo figure out tilt later
//			if (blockEntity.tilt < 0.1f && blockEntity.tilt > -0.1f)
//				if (this.random.nextInt(0, 2) == 1) blockEntity.tilt = 18f else blockEntity.tilt = -18f
			if (blockEntity.zoom == 0f) blockEntity.zoom = 0.5f
			this.doTiltAndShake(poseStack, blockEntity, blockRotation, partialTick, 0.63f, 0.8f)
			this.drawText(
				poseStack,
				"JACK",
				this.jackpotColor,
				blockEntity,
				0.23,
				0.65,
				bufferSource,
				0.02f,
				-0.535
			)
			this.drawText(
				poseStack,
				"POT",
				this.jackpotColor,
				blockEntity,
				0.33,
				0.49,
				bufferSource,
				0.02f,
				-0.535
			)
			if (timer > 600) {
				this.drawText(
					poseStack,
					"YOU WON:",
					Color.WHITE.rgb,
					blockEntity,
					0.29,
					1.05,
					bufferSource,
					z = -0.535
				)
			}
		}
	}
}