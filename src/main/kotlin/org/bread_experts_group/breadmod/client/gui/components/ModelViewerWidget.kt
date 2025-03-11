package org.bread_experts_group.breadmod.client.gui.components

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.MultiBufferSource
import org.apache.logging.log4j.LogManager
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

	private var previewWidth = 113
	private var previewHeight = 80
	private var previewX = 1
	private var previewY = 1

	override fun init() {
		Companion.xRot = 0f
		Companion.yRot = 0f
	}

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		val poseStack = guiGraphics.pose()
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.WHITE, Color.BLACK)
		guiGraphics.borderedFillPositioned(
			this.previewX + this.x,
			this.previewY + this.y,
			this.previewWidth,
			this.previewHeight,
			Color.RED,
			Color.BLACK
		)
		poseStack.pushPose()
		guiGraphics.enableScissor(
			this.x + this.previewX,
			this.y + this.previewY,
			this.x + this.previewWidth,
			this.y + this.previewHeight
		)
		poseStack.translate(this.x.toDouble(), this.y.toDouble(), 0.0)
		poseStack.scaleFlat(Companion.scale)
		this.model.invoke(poseStack, guiGraphics.bufferSource())
		guiGraphics.flush()
		guiGraphics.disableScissor()
		poseStack.popPose()
	}

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
			Companion.scale += scrollY.toFloat()
			LogManager.getLogger().warn(scrollY)
			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
	}

	private fun isMouseOverPreview(mouseX: Double, mouseY: Double): Boolean =
		this.active && this.visible
				&& mouseX >= this.previewX + this.x
				&& mouseY >= this.previewY + this.y
				&& mouseX < this.previewX + this.x + this.previewWidth
				&& mouseY < this.previewY + this.y + this.previewHeight
}