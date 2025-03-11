package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.borderedFill
import java.awt.Color

/**
 * Container for holding "sub" widgets.
 * All actions of this [ContainerWidget] are delegated to its children.
 */
open class ContainerWidget<T : Screen>(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	val id: String,
	val screen: T
) : AbstractWidget(x, y, width, height, Component.literal(id)) {
	var debug: Boolean = false
	private val subWidgets: MutableMap<String, AbstractWidget> = mutableMapOf()

	fun getWidgets(): MutableCollection<AbstractWidget> = this.subWidgets.values

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
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

			this.getWidgets().forEach { if (it.visible) it.render(guiGraphics, mouseX, mouseY, partialTick) }
		}
	}

	open fun tick() {}

	override fun playDownSound(handler: SoundManager) {
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused) {
			return this.getWidgets().any { widget ->
				widget.isHoveredOrFocused && widget.mouseClicked(mouseX, mouseY, button).also {
					if (it) this.screen.focused = widget
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused) return this.getWidgets().any {
			it.isHoveredOrFocused && it.mouseReleased(
				mouseX,
				mouseY,
				button
			)
		}
		return super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused)
			return this.getWidgets()
				.any { it.isHoveredOrFocused && it.mouseDragged(mouseX, mouseY, button, dragX, dragY) }

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
	}

	override fun mouseMoved(mouseX: Double, mouseY: Double): Unit = this.subWidgets.values
		.firstOrNull()
		?.let { if (it.isHoveredOrFocused) it.mouseMoved(mouseX, mouseY) else super.mouseMoved(mouseX, mouseY) }
		?: super.mouseMoved(mouseX, mouseY)

	override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused)
			return this.getWidgets().any { it.isHoveredOrFocused && it.mouseScrolled(mouseX, mouseY, scrollX, scrollY) }

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
	}

	override fun setPosition(x: Int, y: Int) {
		super.setPosition(x, y)
		this.getWidgets().forEach { it.setPosition(x + this.x, y + this.y) }
	}

	override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused)
			return this.getWidgets().any { it.isHoveredOrFocused && it.charTyped(codePoint, modifiers) }
		return super.charTyped(codePoint, modifiers)
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		if (this.isHovered) this.getWidgets().any { return it.isMouseOver(mouseX, mouseY) }

		return super.isMouseOver(mouseX, mouseY)
	}

	fun addChild(
		id: String,
		widget: AbstractWidget,
		x: Int = 0,
		y: Int = 0,
		shouldRender: Boolean = true,
		isActive: Boolean = true
	) {
		widget.visible = shouldRender
		widget.active = isActive
		if (x != 0 || y != 0) widget.setPosition(x, y)
		this.subWidgets[id] = widget
	}

	fun positionChild(id: String, x: Int, y: Int) {
		this.subWidgets[id]?.let { it.x = x; it.y = y } ?: return
	}

	fun removeChild(id: String): AbstractWidget? = this.subWidgets.remove(id)

	fun getChild(id: String): AbstractWidget? = this.subWidgets[id]

	open fun init() {}
}