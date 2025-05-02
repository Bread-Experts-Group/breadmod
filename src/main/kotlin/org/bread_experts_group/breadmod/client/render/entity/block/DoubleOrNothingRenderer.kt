package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
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
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
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
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.toYRotFixed
import org.joml.Vector3f
import org.joml.Vector4f
import java.awt.Color

class DoubleOrNothingRenderer(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntity> {
	private val random = RandomSource.create()
	private val modelData = ModelData.EMPTY
	private val vertexes: Array<Vector3f> = arrayOf(
		Vector3f(0.95f, 2.375f, 0.0f),
		Vector3f(0.05f, 2.375f, 0f),
		Vector3f(0.95f, 0.55f, 0f),
		Vector3f(0.05f, 0.55f, 0f)
	)
	private val backgroundTexture = modLocation("textures/block/don_background.png")

	override fun render(
		blockEntity: DoubleOrNothingBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val originalModel = this.context.blockRenderDispatcher.getBlockModel(blockEntity.blockState)
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
		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))

		this.drawRainbowQuad(poseStack, bufferSource, blockRotation)

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
			RenderType.text(this.backgroundTexture),
			Vector4f(0.4f, 0.8f, 0.4f, 1.0f),
			Vector3f(0f, 0f, 1f),
			Vector3f(0f, 0f, 0f),
			Vector3f(2f, 0f, 1f),
			Vector3f(2f, 0f, 0f),
			0f,
			0f,
			1f,
			1f
		)
		poseStack.popPose()
		poseStack.popPose()

		if (blockEntity.timeStarted > 0 && !blockEntity.nothing) {
			this.drawText(
				poseStack,
				"${blockEntity.counter}x",
				Color.WHITE.rgb,
				blockEntity,
				0.20,
				0.65,
				bufferSource,
				0.05f
			)
		} else if (!blockEntity.nothing) {
			this.drawText(poseStack, "PRESS DOUBLE", Color.WHITE.rgb, blockEntity, 0.15, 0.55, bufferSource)
			this.drawText(poseStack, "TO START", Color.WHITE.rgb, blockEntity, 0.275, 0.45, bufferSource)
		} else {
			this.drawText(poseStack, "NOTHING", Color.RED.rgb, blockEntity, 0.15, 0.55, bufferSource)
		}

		this.drawText(
			poseStack,
			"current player: ${blockEntity.currentPlayer?.name}",
			Color.WHITE.rgb,
			blockEntity,
			0.25,
			-0.35,
			bufferSource,
			0.005f
		)
	}

	private fun drawText(
		poseStack: PoseStack,
		text: String,
		color: Int,
		blockEntity: DoubleOrNothingBlockEntity,
		x: Double,
		y: Double,
		bufferSource: MultiBufferSource,
		scale: Float = 0.01f
	) {
		poseStack.drawTextOnBlockSide(
			localClient.font,
			Component.literal(text),
			x,
			y,
			-0.561,
			bufferSource,
			blockEntity.blockState,
			scale = scale,
			color = color,
			dropShadow = true,
			dropShadowOffset = -0.05f
		)
	}

	private fun drawRainbowQuad(
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
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
		buffer.addVertex(pose, topLeft.x, topLeft.y, topLeft.z).setUv(0f, 0f)
		buffer.addVertex(pose, bottomLeft.x, bottomLeft.y, bottomLeft.z).setUv(0f, 1f)
		buffer.addVertex(pose, bottomRight.x, bottomRight.y, bottomRight.z).setUv(1f, 1f)
		buffer.addVertex(pose, topRight.x, topRight.y, topRight.z).setUv(1f, 0f)
		poseStack.popPose()
	}

	override fun getRenderBoundingBox(blockEntity: DoubleOrNothingBlockEntity): AABB {
		val pos = blockEntity.blockPos.toVec3()
		return AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 2.0, pos.z + 1)
	}
}