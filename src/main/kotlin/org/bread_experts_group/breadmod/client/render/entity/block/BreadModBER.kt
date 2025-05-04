package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelManager
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.tessellateModel
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import java.awt.Color
import kotlin.jvm.optionals.getOrNull

/**
 * Bread Mod Specific [BlockEntityRenderer] with an extremely cursed "in-world" [GuiGraphics] implementation.
 */
abstract class BreadModBER<T : BreadModBlockEntity<T>>(
	val context: Context,
	private val snapGraphicsToBlockSide: Boolean = true
) : BlockEntityRenderer<T> {
	protected val random: RandomSource = RandomSource.create()
	protected val modelManager: ModelManager = localClient.modelManager
	private val modelData: ModelData = ModelData.builder().with(ModelProperty(), ExtraFaceData.DEFAULT).build()
	private val debugAxisModel = localClient.modelManager.getModel("${ModelProvider.BLOCK_FOLDER}/axis")

	/**
	 * Make sure to place this before the yRot mulPose, since this model's orientation is pulled from the BlockState.
	 */
	fun renderOriginalModel(
		blockEntity: T,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedOverlay: Int
	) {
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
	}

	protected fun renderModel(
		blockEntity: T,
		model: BakedModel,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedOverlay: Int
	) {
		localClient.blockRenderer.modelRenderer.tessellateModel(
			blockEntity,
			model,
			poseStack,
			bufferSource,
			this.random,
			packedOverlay,
			this.modelData
		)
	}

	protected fun renderDebugAxis(blockEntity: T, poseStack: PoseStack, bufferSource: MultiBufferSource) {
		this.renderModel(
			blockEntity,
			this.debugAxisModel,
			poseStack,
			bufferSource,
			OverlayTexture.NO_OVERLAY
		)
	}

	private companion object {
		const val TRANSLATE_OFFSET = 0.0001
		val LEVEL_GRAPHICS: GuiGraphics = object : GuiGraphics(
			localClient,
			localClient.renderBuffers().bufferSource()
		) {
			override fun containsPointInScissor(x: Int, y: Int): Boolean = TODO("Scissor")
			override fun enableScissor(minX: Int, minY: Int, maxX: Int, maxY: Int) = TODO("Scissor")
			override fun disableScissor() = TODO("Scissor")
			override fun fill(
				renderType: RenderType,
				minX: Int, minY: Int,
				maxX: Int, maxY: Int, z: Int,
				color: Int
			) {
				val matrix4f = this.pose().last()
				var minX = minX.toFloat()
				var maxX = maxX.toFloat()
				var minY = minY.toFloat()
				var maxY = maxY.toFloat()
				if (minX < maxX) {
					val i = minX
					minX = maxX
					maxX = i
				}

				if (minY < maxY) {
					val j = minY
					minY = maxY
					maxY = j
				}
				val plane = z * this@Companion.TRANSLATE_OFFSET.toFloat()
				val consumer = this.bufferSource().getBuffer(renderType)
				consumer.addVertex(matrix4f, minX, minY, plane).setColor(color)
				consumer.addVertex(matrix4f, minX, maxY, plane).setColor(color)
				consumer.addVertex(matrix4f, maxX, maxY, plane).setColor(color)
				consumer.addVertex(matrix4f, maxX, minY, plane).setColor(color)
				this.flush()
			}
		}
	}

	override fun render(
		blockEntity: T,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		Companion.LEVEL_GRAPHICS.pose().last().pose().set(poseStack.last().pose())
		// todo band-aid fix. replace with own innerBlit and override blit methods to use it.
		Companion.LEVEL_GRAPHICS.fill(0, 0, 0, 0, Color(0, 0, 0, 0).rgb)
		if (this.snapGraphicsToBlockSide) this.translateGraphicsToBlockSide(blockEntity)
		this.setupGraphicsPose()
		this.renderWithGraphics(
			blockEntity,
			partialTick,
			poseStack,
			Companion.LEVEL_GRAPHICS.pose(),
			bufferSource,
			Companion.LEVEL_GRAPHICS,
			packedLight,
			packedOverlay
		)
		this.teardownGraphicsPose()
	}

	open fun renderWithGraphics(
		blockEntity: T,
		partialTick: Float,
		poseStack: PoseStack,
		lgPoseStack: PoseStack,
		bufferSource: MultiBufferSource,
		levelGraphics: GuiGraphics,
		packedLight: Int,
		packedOverlay: Int
	) {
	}

	/**
	 * Rotates, scales, and positions this [PoseStack] to the proper values.
	 */
	private fun setupGraphicsPose() {
		val lgPoseStack = Companion.LEVEL_GRAPHICS.pose()
		lgPoseStack.pushPose()
		lgPoseStack.mulPose(Axis.ZP.rotationDegrees(180f))
		lgPoseStack.translate(-1f, -1f, 0f)
		lgPoseStack.scaleFlat(0.0625f)
	}

	/**
	 * Returns this [PoseStack] to its original values.
	 */
	private fun teardownGraphicsPose() {
		val lgPoseStack = Companion.LEVEL_GRAPHICS.pose()
		lgPoseStack.mulPose(Axis.ZN.rotationDegrees(180f))
		lgPoseStack.translate(1f, 1f, 0f)
		lgPoseStack.scaleFlat(1f)
		lgPoseStack.popPose()
	}

	private fun translateGraphicsToBlockSide(blockEntity: BlockEntity) {
		val lgPoseStack = Companion.LEVEL_GRAPHICS.pose()
		val facing =
			blockEntity.blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).getOrNull()
				?: blockEntity.blockState.getOptionalValue(BlockStateProperties.FACING).getOrNull() ?: return
		when (facing) {
			DOWN, UP -> {} // todo work on down/up for the FACING property
			NORTH    -> lgPoseStack.translate(0.0, 0.0, -Companion.TRANSLATE_OFFSET)
			SOUTH    -> {
				lgPoseStack.mulPose(Axis.YN.rotationDegrees(180f))
				lgPoseStack.translate(-1.0, 0.0, -1.0 - Companion.TRANSLATE_OFFSET)
			}
			WEST     -> {
				lgPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
				lgPoseStack.translate(-1.0, 0.0, -Companion.TRANSLATE_OFFSET)
			}
			EAST     -> {
				lgPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
				lgPoseStack.translate(0.0, 0.0, -1.0 - Companion.TRANSLATE_OFFSET)
			}
		}
	}
}