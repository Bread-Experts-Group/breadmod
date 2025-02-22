package org.bread_experts_group.breadmod.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.borderedFill
import java.awt.Color

/**
 * Experimental widget "holder" for holding sub-widgets.
 */
open class ContainerWidget(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	id: String
) : AbstractWidget(x, y, width, height, Component.literal(id)) {
	var debug: Boolean = false
	val subWidgets: MutableMap<String, AbstractWidget> = mutableMapOf()
	var focusedWidget: GuiEventListener? = null

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

			this.subWidgets.forEach { (_, widget) ->
				if (widget.visible) widget.render(guiGraphics, mouseX, mouseY, partialTick)
			}
		}
	}

	// todo work on a better solution for setting child focus without short circuiting the click functionality
	//  AbstractContainerEventHandler#setFocused
	fun setFocused(listener: GuiEventListener?) {
		this.focusedWidget = listener
		listener?.isFocused = true
	}

	override fun playDownSound(handler: SoundManager) {
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused) {
			// todo refer to the todo above
			//  unfocusing the widget when clicking anywhere else probably isn't ideal, but it works for now
			this.focusedWidget = null
			this.subWidgets.values.forEach { it.isFocused = false }
			for (widget: AbstractWidget in this.subWidgets.values) {
				if (widget.isHoveredOrFocused) {
					this.setFocused(widget)
					return widget.mouseClicked(mouseX, mouseY, button)
				}
			}
		}

		return super.mouseClicked(mouseX, mouseY, button)
	}

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused) {
			for (widget: AbstractWidget in this.subWidgets.values) {
				if (widget.isHoveredOrFocused) return widget.mouseReleased(mouseX, mouseY, button)
			}
		}

		return super.mouseReleased(mouseX, mouseY, button)
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
		if (!this.active) return false
		if (this.isHoveredOrFocused) {
			for (widget: AbstractWidget in this.subWidgets.values) {
				if (widget.isHoveredOrFocused) return widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)
			}
		}

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
	}

	override fun mouseMoved(mouseX: Double, mouseY: Double): Unit = this.subWidgets.values
		.firstOrNull()
		?.let { if (it.isHoveredOrFocused) it.mouseMoved(mouseX, mouseY) else super.mouseMoved(mouseX, mouseY) }
		?: super.mouseMoved(mouseX, mouseY)

	override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
		if (!this.active) return false
		for (widget: AbstractWidget in this.subWidgets.values) {
			if (widget.isHoveredOrFocused) widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY) else
				super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
		}

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
	}

	override fun setPosition(x: Int, y: Int) {
		super.setPosition(x, y)
		this.subWidgets.forEach { (_, widget) -> widget.setPosition(x + this.x, y + this.y) }
	}

	override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
		if (!this.active) return false
		return this.subWidgets.values
			.firstOrNull { it.isHoveredOrFocused }
			?.charTyped(codePoint, modifiers)
			?: super.charTyped(codePoint, modifiers)
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		if (this.isHovered) this.subWidgets.values.any { return it.isMouseOver(mouseX, mouseY) }

		return super.isMouseOver(mouseX, mouseY)
	}

	fun addChild(
		id: String,
		widget: AbstractWidget,
		shouldRender: Boolean = true,
		isActive: Boolean = true
	) {
		widget.visible = shouldRender
		widget.active = isActive
		this.subWidgets[id] = widget
	}

	fun positionChild(id: String, x: Int, y: Int) {
		this.subWidgets[id]?.let { it.x = x; it.y = y } ?: return
	}

	fun removeChild(id: String): AbstractWidget? = this.subWidgets.remove(id)
}