package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.util.Mth
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.util.Color
import kotlin.math.floor
import kotlin.math.max

// todo some edge cases occur that doesn't allow the widget to scroll (tool gun screen, mode tab for example)
class ScrollingContainerWidget<T : Screen>(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	id: String,
	screen: T,
	private var innerHeight: Int,
	private val innerColor: Int,
	private val outerColor: Int,
	private val scrollRate: Double = 10.0,
	initializer: (ScrollingContainerWidget<T>) -> Unit
) : ContainerWidget<T, ScrollingContainerWidget<T>>(x, y, width, height, id, screen, initializer) {
	private val innerPadding: Int = 0
	private var scrollAmount: Double = 0.0
		set(value) {
			field = Mth.clamp(value, 0.0, this.getMaxScrollAmount().toDouble())
		}

	override fun addChild(
		id: String,
		widget: AbstractWidget,
		x: Int,
		y: Int,
		shouldRender: Boolean,
		isActive: Boolean
	) {
		val newX = x + this.innerPadding
		val newY = y + this.innerPadding
		super.addChild(id, widget, newX, newY, shouldRender, isActive)
	}

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		if (this.visible) {
			if (this.debug) guiGraphics.renderDebugFill()
			guiGraphics.borderedFillPositioned(
				this.x, this.y,
				this.width, this.height,
				this.outerColor, this.innerColor
			)
			guiGraphics.enableScissor(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1)
			this.renderContainer(guiGraphics, mouseX, mouseY, partialTick)
			this.getWidgets().forEach { if (it.visible) it.render(guiGraphics, mouseX, mouseY, partialTick) }
			guiGraphics.disableScissor()
			if (this.scrollbarVisible()) this.renderScrollBar(guiGraphics)
			if (this.debug) guiGraphics.renderDebugText()
		}
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
		LogManager.getLogger(this.getMaxScrollAmount())
		if (!this.active) return false
		if (this.isHoveredOrFocused) {
			val scrollDirection = floor(scrollY * this.scrollRate).toInt()
			// account for the last scroll on the top and bottom of the widget
			val topAccount = if (this.scrollAmount - scrollDirection == 0.0 && scrollDirection > 0) 10 else 0
			val bottomAccount = if (this.scrollAmount.toInt() - scrollDirection == this.getMaxScrollAmount()) 10 else 0
			this.scrollAmount -= scrollDirection
			this.getWidgets().forEach { widget ->
				widget.y += topAccount - bottomAccount
				if (this.scrollAmount != 0.0 && this.scrollAmount < this.getMaxScrollAmount()) {
					widget.y += scrollDirection
				}
			}
			return true
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
	}

	private fun getMaxScrollAmount(): Int = max(0.0, this.getInnerHeight().toDouble()).toInt()
	private fun getInnerHeight(): Int = this.height + this.innerHeight
	private fun scrollbarWidth(): Int = 4
	private fun scrollbarVisible(): Boolean = this.getInnerHeight() > this.getHeight()

	private fun getScrollBarHeight(): Int {
		return Mth.clamp(
			((this.height * this.height).toFloat() / this.getInnerHeight().toFloat()).toInt(),
			32,
			this.height
		)
	}

	private fun renderScrollBar(guiGraphics: GuiGraphics) {
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