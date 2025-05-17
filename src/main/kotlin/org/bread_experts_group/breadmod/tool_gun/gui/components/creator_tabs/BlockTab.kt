package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_tabs

import com.mojang.blaze3d.platform.Lighting
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.render.enablePositionedScissor
import org.bread_experts_group.breadmod.client.render.fillPositioned
import org.bread_experts_group.breadmod.client.render.flushAndFinishScissor
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.putBlockState

class BlockTab(screen: CreatorScreen, private val level: Level) : ContainerWidget<CreatorScreen>(
	screen.getLeftPos(),
	screen.getTopPos() + 12,
	256,
	244,
	"block_tab",
	screen
) {
	private var rotation: Float = 0f
	private var currentBlock: BlockState = ModBlocks.CREATIVE_GENERATOR.asBlock().defaultBlockState()

	init {
		this.init()
	}

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		val poseStack = guiGraphics.pose()
		this.rotation += 1f * partialTick
		guiGraphics.fillPositioned(this.x + 5, this.y + 5, 100, 100, Color.BLACK)
		guiGraphics.enablePositionedScissor(this.x + 5, this.y + 5, 100, 100)
		poseStack.pushPose()
		poseStack.translate(this.x + 55, this.y + 55, 200)
		poseStack.mulPose(Axis.XN.rotationDegrees(10f))
		poseStack.mulPose(Axis.YN.rotationDegrees(this.rotation))
		poseStack.scaleFlat(-64f)
		poseStack.translate(-0.5, -0.5, -0.5)
		Lighting.setupForFlatItems()
		localClient.blockRenderer.renderSingleBlock(
			this.currentBlock,
			poseStack,
			guiGraphics.bufferSource(),
			LightTexture.FULL_BRIGHT,
			OverlayTexture.NO_OVERLAY
		)
		this.getBlockEntityFromBlockState(this.currentBlock)?.let { entity ->
			val renderer = this.getBlockEntityRenderer(entity) ?: return@let
			renderer.render(
				entity,
				partialTick,
				poseStack,
				guiGraphics.bufferSource(),
				LightTexture.FULL_BRIGHT,
				OverlayTexture.NO_OVERLAY
			)
		}
		poseStack.popPose()
		guiGraphics.flushAndFinishScissor()
		poseStack.pushPose()
		var offset = this.y + 5
		for (property in this.currentBlock.properties) {
			guiGraphics.drawWordWrap(
				localClient.font,
				Component.literal("${property.name}: ${this.currentBlock.getValue(property)}"),
				this.x + 110,
				offset,
				145,
				Color.WHITE
			)
			offset += 10
		}
		poseStack.popPose()
	}

	private fun getBlockEntityFromBlockState(state: BlockState): BlockEntity? {
		val entityBlock = state.block as? EntityBlock ?: return null
		val blockEntity = entityBlock.newBlockEntity(BlockPos.ZERO, state) ?: return null
		blockEntity.level = this.level
		return blockEntity
	}

	private fun <T : BlockEntity> getBlockEntityRenderer(entity: T): BlockEntityRenderer<T>? =
		localClient.blockEntityRenderDispatcher.getRenderer(entity)

	override fun init() {
		this.addChild(
			"test_button",
			GenericButton(this.x, this.y + 150, 40, 12, "TEST") {
				this.screen.getData().setValueDirect { it.putBlockState("block", this.currentBlock) }
			}
		)
	}
}