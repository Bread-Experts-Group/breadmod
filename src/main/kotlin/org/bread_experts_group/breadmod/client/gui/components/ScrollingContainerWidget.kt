package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.Mth
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import java.awt.Color
import kotlin.math.max

open class ScrollingContainerWidget<T : Screen>(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	id: String,
	screen: T
) : ContainerWidget<T>(x, y, width, height, id, screen) {
	private var scrollAmount: Double = 0.0
	var scrolling: Boolean = false

	override fun init() {
		this.addChild("test", GenericButton(this.x + 5, this.y + 25, 15, 15, "x") {})
	}

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		val scrollY = mouseY - this.scrollAmount
		this.isHovered = guiGraphics.containsPointInScissor(mouseX, scrollY.toInt()) &&
				mouseX >= this.x &&
				scrollY >= this.y &&
				mouseX < this.x + this.width &&
				scrollY < this.y + this.height
		val poseStack = guiGraphics.pose()
		if (this.visible) {
			if (this.debug) guiGraphics.borderedFill(
				RenderType.gui(),
				this.x,
				this.y,
				this.x + this.width,
				this.y + this.height,
				Color.GREEN.rgb,
				Color.WHITE.rgb
			)
			guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.GRAY, Color.DARK_GRAY)
			guiGraphics.enableScissor(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1)
			poseStack.pushPose()
			poseStack.translate(0.0, -this.scrollAmount, 0.0)
			this.renderContainer(guiGraphics, mouseX, mouseY, partialTick)
			this.getWidgets().forEach { if (it.visible) it.render(guiGraphics, mouseX, mouseY, partialTick) }
			poseStack.popPose()
			guiGraphics.disableScissor()
			if (this.scrollbarVisible()) this.renderScrollBar(guiGraphics)
			if (this.debug) guiGraphics.drawString(localClient.font, "DEBUG MODE ENABLED", 0, 0, Color.GREEN.rgb)
		}
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		return super.isMouseOver(mouseX, mouseY - this.scrollAmount)
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		LogManager.getLogger().info("clicked")
		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (button == 0) this.scrolling = false
		return super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused) {
			this.setScrollAmount(this.scrollAmount - scrollY * this.scrollRate())
			// todo impl child widget movement after scrolling here
			return true
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
	}

	private fun setScrollAmount(scrollAmount: Double) {
		this.scrollAmount = Mth.clamp(scrollAmount, 0.0, this.getMaxScrollAmount().toDouble())
	}

	private fun getMaxScrollAmount(): Int = max(0.0, (this.getContentHeight() - (this.height - 4)).toDouble()).toInt()

	private fun getContentHeight(): Int = this.getInnerHeight() + 4

	protected open fun scrollRate(): Double = 10.0

	protected open fun getInnerHeight(): Int = this.height + 100

	protected open fun scrollbarWidth(): Int = 4

	protected open fun scrollbarVisible(): Boolean = this.getInnerHeight() > this.getHeight()

	fun getScrollBarHeight(): Int {
		return Mth.clamp(
			((this.height * this.height).toFloat() / this.getContentHeight().toFloat()).toInt(),
			32,
			this.height
		)
	}

	open fun renderScrollBar(guiGraphics: GuiGraphics) {
		val height = this.getScrollBarHeight()
		val width = this.scrollbarWidth()
		val x = this.x + this.width - width
		val y = max(
			this.y.toDouble(),
			(this.scrollAmount * (this.height - height) / this.getMaxScrollAmount() + this.y)
		).toInt()
		guiGraphics.borderedFillPositioned(x, y, width, height, Color.DARK_GRAY, Color.BLACK)
	}
}