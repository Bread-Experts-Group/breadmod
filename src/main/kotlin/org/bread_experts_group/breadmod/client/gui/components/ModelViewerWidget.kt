package org.bread_experts_group.breadmod.client.gui.components

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Blocks
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.enablePositionedScissor
import org.bread_experts_group.breadmod.client.render.flushAndFinishScissor
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.util.Color

/*
Holding left click rotates the model
Holding right click moves the model
Scrolling zooms the model

Buttons for fine tuning the movement and zoom

Only start dragging when the mouse is in the preview window or the drag flag is set to true

Only allow zooming when mouse is within the preview window

Make this whole thing a widget and invoke a lambda containing the model rendering code
 */

class ModelViewerWidget(
	x: Int = 0,
	y: Int = 0,
	screen: Screen,
	val model: (ModelViewerWidget, PoseStack, MultiBufferSource) -> Unit
) : ContainerWidget<Screen>(x, y, 115, 140, "model_viewer", screen) {
	companion object {
		fun setupRender(
			modelViewer: ModelViewerWidget,
			posX: Double,
			posY: Double,
			scale: Double,
			poseStack: PoseStack
		) {
			poseStack.translate(posX, posY, 200.0)
			poseStack.mulPose(Axis.YN.rotationDegrees(modelViewer.getDragger().xRot))
			poseStack.mulPose(Axis.XN.rotationDegrees(modelViewer.getDragger().yRot))
			poseStack.translate(scale, 0.0, 0.0)
			poseStack.scaleFlat(-scale.toFloat())
			Lighting.setupForFlatItems()
		}

		fun renderBlock(poseStack: PoseStack, bufferSource: MultiBufferSource) {
			poseStack.pushPose()
			poseStack.translate(0.5, -0.5, -0.5)
			localClient.blockRenderer.renderSingleBlock(
				Blocks.GRASS_BLOCK.defaultBlockState(),
				poseStack,
				bufferSource,
				LightTexture.FULL_BRIGHT,
				OverlayTexture.NO_OVERLAY
			)
			poseStack.popPose()
		}

		fun renderAxis(poseStack: PoseStack, bufferSource: MultiBufferSource) {
			poseStack.pushPose()
			poseStack.scaleFlat(-50f)
			localClient.blockRenderer.modelRenderer.renderModel(
				poseStack.last(),
				bufferSource.getBuffer(RenderType.solid()),
				Blocks.GRASS_BLOCK.defaultBlockState(),
				localClient.getModel("block/axis"),
				1f,
				1f,
				1f,
				LightTexture.FULL_BRIGHT,
				OverlayTexture.NO_OVERLAY
			)
			poseStack.popPose()
		}
	}

	override fun initContainer() {
		this.addChild("dragger", this.Dragger())
		this.addChild(
			"move_left",
			GenericButton(0, 0, 20, 20, "<") {
				this.getDragger().offsetX -= 1.0
			},
			this.x + 2,
			this.y + 85
		)
		this.addChild(
			"move_right",
			GenericButton(0, 0, 20, 20, ">") {
				this.getDragger().offsetX += 1.0
			},
			this.x + 24,
			this.y + 85
		)
	}

	fun getDragger(): Dragger = this.getChild("dragger") as Dragger

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.WHITE, Color.BLACK)
	}

	inner class Dragger : AbstractWidget(this.x + 1, this.y + 1, 113, 80, Component.empty()) {
		private val logger: Logger = LogManager.getLogger("Model Viewer Widget Dragger")
		var xRot: Float = 0f
		var yRot: Float = 0f
		var scale: Float = 1f
		var offsetX: Double = 0.0
		var offsetY: Double = 0.0

		override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
			guiGraphics.borderedFillPositioned(
				this.x,
				this.y,
				this.width,
				this.height,
				if (this.isHoveredOrFocused) Color.GREEN else Color.RED,
				Color.BLACK
			)
			val poseStack = guiGraphics.pose()
			poseStack.pushPose()
			if (!this@ModelViewerWidget.debug) guiGraphics.enablePositionedScissor(
				this.x + 1,
				this.y + 1,
				this.width - 1,
				this.height - 1
			)
			val offsetX = this.rectangle.width / 2
			val offsetY = this.rectangle.height / 2
			poseStack.translate(this.x.toDouble(), this.y.toDouble(), 0.0)
			poseStack.translate(this.offsetX + offsetX, this.offsetY + offsetY, 0.0)
			poseStack.scaleFlat(this.scale)
			this@ModelViewerWidget.model.invoke(this@ModelViewerWidget, poseStack, guiGraphics.bufferSource())
			if (!this@ModelViewerWidget.debug) guiGraphics.flushAndFinishScissor()
			poseStack.popPose()
		}

		override fun playDownSound(handler: SoundManager) {
		}

		override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}

		override fun isValidClickButton(button: Int): Boolean = button == 0 || button == 1

		private var flag: Boolean = false
		override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean =
			if ((this.isMouseOverPreview(mouseX, mouseY) || this.flag) && this.isValidClickButton(button))
				when (button) {
					0    -> {
						this.flag = true
						this.xRot -= dragX.toFloat()
						this.yRot -= dragY.toFloat()
						true
					}
					1    -> {
						this.flag = true
						this.offsetX -= dragX.toFloat()
						this.offsetY -= dragY.toFloat()
						true
					}
					else -> false
				} else false

		override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
			this.flag = false
			return super.mouseReleased(mouseX, mouseY, button)
		}

		override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
			this.logger.info(button)
			return super.mouseClicked(mouseX, mouseY, button)
		}

		override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
			if (this.isHoveredOrFocused && (this.isMouseOverPreview(mouseX, mouseY))) {
				this.scale += (scrollY.toFloat() * 0.1f)
				return true
			}
			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
		}

		private fun isMouseOverPreview(mouseX: Double, mouseY: Double): Boolean =
			this.active && this.visible
					&& mouseX >= this.x
					&& mouseY >= this.y
					&& mouseX < this.x + this.width
					&& mouseY < this.y + this.height
	}
}