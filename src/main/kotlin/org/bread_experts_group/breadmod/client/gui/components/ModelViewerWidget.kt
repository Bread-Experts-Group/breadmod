package org.bread_experts_group.breadmod.client.gui.components

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.scaleFlat
import java.awt.Color

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
	val model: (PoseStack, MultiBufferSource) -> Unit
) : ContainerWidget<Screen>(x, y, 115, 140, "model_viewer", screen) {
	companion object {
		var xRot: Float = 0f
		var yRot: Float = 0f
		var scale: Float = 1f
		fun setupRender(
			posX: Double,
			posY: Double,
			scale: Double,
			poseStack: PoseStack
		) {
			poseStack.translate(posX, posY, 200.0)
			poseStack.translate(-scale, 0.0, 0.0)
			poseStack.mulPose(Axis.YN.rotationDegrees(this.xRot))
			poseStack.mulPose(Axis.XN.rotationDegrees(this.yRot))
			poseStack.translate(scale, 0.0, 0.0)
			poseStack.scaleFlat(-scale.toFloat())
			Lighting.setupFor3DItems()
		}
	}

	private var offsetX = 0.0
	private var offsetY = 0.0

	override fun init() {
		Companion.xRot = 0f
		Companion.yRot = 0f
		Companion.scale = 1f

		this.addChild("dragger", this.Dragger())
		this.addChild(
			"move_left",
			GenericButton(0, 0, 20, 20, "<") {
				this.offsetX -= 1.0
			},
			this.x + 2,
			this.y + 85
		)
		this.addChild(
			"move_right",
			GenericButton(0, 0, 20, 20, ">") {
				this.offsetX += 1.0
			},
			this.x + 24,
			this.y + 85
		)
	}

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.WHITE, Color.BLACK)
	}

	private inner class Dragger : AbstractWidget(this.x + 1, this.y + 1, 113, 80, Component.empty()) {
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
			if (!this@ModelViewerWidget.debug) guiGraphics.enableScissor(
				this.x,
				this.y,
				this.x + this.width - 1,
				this.y + this.height - 1
			)
			poseStack.translate(this.x.toDouble(), this.y.toDouble(), 0.0)
			poseStack.translate(this@ModelViewerWidget.offsetX, this@ModelViewerWidget.offsetY, 0.0)
			poseStack.scaleFlat(Companion.scale)
			this@ModelViewerWidget.model.invoke(poseStack, guiGraphics.bufferSource())
			guiGraphics.flush()
			if (!this@ModelViewerWidget.debug) guiGraphics.disableScissor()
			poseStack.popPose()
		}

		override fun playDownSound(handler: SoundManager) {
		}

		override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}

		override fun isValidClickButton(button: Int): Boolean = button == 0 || button == 1

		private var flag = false
		override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
			if (this.isHoveredOrFocused && (this.isMouseOverPreview(mouseX, mouseY) || this.flag)
				&& this.isValidClickButton(button)
			) {
				this.flag = true
				Companion.xRot -= dragX.toFloat()
				Companion.yRot -= dragY.toFloat()
				return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
			}
			return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
		}

		override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
			this.flag = false
			return super.mouseReleased(mouseX, mouseY, button)
		}

		override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
			if (this.isHoveredOrFocused && (this.isMouseOverPreview(mouseX, mouseY))) {
				Companion.scale += scrollY.toFloat() - 0.9f
				return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
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