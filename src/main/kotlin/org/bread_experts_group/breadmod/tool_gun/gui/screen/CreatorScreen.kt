package org.bread_experts_group.breadmod.tool_gun.gui.screen

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Cow
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.screens.HoldScreen
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.enablePositionedScissor
import org.bread_experts_group.breadmod.client.render.flushAndFinishScissor
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.redirectFocusFromContainerWidgets
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.BlockTab
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.EntityTab
import org.bread_experts_group.breadmod.tool_gun.sound.KSPSoundInstance
import org.bread_experts_group.breadmod.util.Color

class CreatorScreen(
	private val level: Level,
	val data: ToolGunData
) : HoldScreen(Component.empty(), InputConstants.KEY_F) {
	companion object {
		val bgSound: KSPSoundInstance = KSPSoundInstance()

		fun renderBlockPreview(
			state: BlockState,
			blockEntity: BlockEntity?,
			guiGraphics: GuiGraphics,
			random: RandomSource,
			x: Int,
			y: Int,
			rotation: Float = 0f,
			partialTick: Float = 0f
		) {
			val model = localClient.blockRenderer.getBlockModel(state)
			val level = localClient.level ?: return
			val poseStack = guiGraphics.pose()
			val modelData = model.getModelData(level, BlockPos.ZERO, state, ModelData.EMPTY)
			guiGraphics.borderedFillPositioned(x, y, 100, 100, Color.GRAY, Color.BLACK)
			guiGraphics.enablePositionedScissor(x + 1, y + 1, 98, 98)
			Lighting.setupForFlatItems()
			poseStack.pushPose()
			poseStack.translate(x + 50, y + 50, 200)
			poseStack.mulPose(Axis.XN.rotationDegrees(10f))
			poseStack.mulPose(Axis.YN.rotationDegrees(rotation))
			poseStack.scaleFlat(-64f)
			poseStack.translate(-0.5, -0.5, -0.5)
			model.getRenderTypes(state, random, modelData).forEach { renderType ->
				localClient.blockRenderer.renderSingleBlock(
					state,
					poseStack,
					guiGraphics.bufferSource(),
					LightTexture.FULL_BRIGHT,
					OverlayTexture.NO_OVERLAY,
					modelData,
					renderType
				)
			}
			blockEntity?.let {
				val renderer = localClient.blockEntityRenderDispatcher.getRenderer(it) ?: return@let
				renderer.render(
					it,
					partialTick,
					poseStack,
					guiGraphics.bufferSource(),
					LightTexture.FULL_BRIGHT,
					OverlayTexture.NO_OVERLAY
				)
			}
			poseStack.popPose()
			guiGraphics.flushAndFinishScissor()
		}
	}

	var leftPos: Int = 0
	var topPos: Int = 0
	var currentBlock: BlockState = Blocks.GRASS_BLOCK.defaultBlockState()
	var currentEntity: Entity = Cow(EntityType.COW, this.level)

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.renderBlurredBackground(partialTick)
		RenderSystem.enableBlend()
		guiGraphics.fillGradient(0, 0, this.width, this.height, Color.color(a = 180), Color.color(a = 240))
		RenderSystem.disableBlend()
	}

	override val shouldClose: Boolean
		get() = false

	fun getGuiTicks(): Int = localClient.gui.guiTicks

	override fun init() {
		this.leftPos = (this.width - 256) / 2
		this.topPos = (this.height - 256) / 2
		this.addRenderableWidget(BlockTab(this, this.level))
		this.addRenderableWidget(EntityTab(this).also(EntityTab::disable))
		this.addRenderableWidget(GenericButton(this.leftPos, this.topPos + 3, 44, 12, "BLOCK") {
			(this.children()[0] as ContainerWidget<*>).enable()
			(this.children()[1] as ContainerWidget<*>).disable()
		})
		this.addRenderableWidget(GenericButton(this.leftPos + 44, this.topPos + 3, 44, 12, "ENTITY") {
			(this.children()[0] as ContainerWidget<*>).disable()
			(this.children()[1] as ContainerWidget<*>).enable()
		})
		Companion.bgSound.shouldPlay = true
		if (!localClient.soundManager.isActive(Companion.bgSound)) localClient.soundManager.play(Companion.bgSound)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean =
		this.redirectFocusFromContainerWidgets(mouseX, mouseY, button)

	override fun onClose() {
		Companion.bgSound.shouldPlay = false
		super.onClose()
	}

	override fun isPauseScreen(): Boolean = false
}