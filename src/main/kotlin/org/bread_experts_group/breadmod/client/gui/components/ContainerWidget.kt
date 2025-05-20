package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import java.awt.Color
import java.util.function.Consumer

/**
 * Container for holding "sub" or "child" widgets.
 *
 * All actions of this [ContainerWidget] are delegated to its children.
 *
 * Interactions with this [ContainerWidget] itself are not possible.
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

	/**
	 * Gets every widget in this [ContainerWidget], including nested [ContainerWidget]s.
	 *
	 * @see getContainerWidgets
	 */
	fun getWidgets(): MutableCollection<AbstractWidget> = this.subWidgets.values

	/**
	 * Gets every nested [ContainerWidget] in this [ContainerWidget].
	 */
	fun getContainerWidgets(): List<ContainerWidget<*>> =
		this.getWidgets().filterIsInstance<ContainerWidget<*>>()

	/**
	 * Gets every widget from this [ContainerWidget], including widgets from nested [ContainerWidget]s.
	 */
	fun getAllWidgets(): List<AbstractWidget> = buildList {
		val thisContainerWidgets = this@ContainerWidget.getWidgets().filterNot { it is ContainerWidget<*> }
		this.addAll(thisContainerWidgets)
		this@ContainerWidget.getContainerWidgets().forEach { this.addAll(it.getWidgets()) }
	}

	private fun tickContainerWidgets(): Unit = this.getContainerWidgets().forEach(ContainerWidget<*>::tick)

	protected open fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}
	protected open fun renderContainerAfterWidgets(
		guiGraphics: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		partialTick: Float
	) {
	}

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		if (this.visible) {
			if (this.debug) guiGraphics.borderedFill(
				this.x,
				this.y,
				this.x + this.width,
				this.y + this.height,
				Color.GREEN.rgb,
				Color.WHITE.rgb
			)
			this.renderContainer(guiGraphics, mouseX, mouseY, partialTick)
			this.getWidgets().forEach { if (it.visible) it.render(guiGraphics, mouseX, mouseY, partialTick) }
			this.renderContainerAfterWidgets(guiGraphics, mouseX, mouseY, partialTick)
			if (this.debug) guiGraphics.drawString(localClient.font, "DEBUG MODE ENABLED", 0, 0, Color.GREEN.rgb)
		}
	}

	private fun setChildrenVisibility() {
		if (this.getWidgets().isNotEmpty()) this.getWidgets().forEach {
			it.active = this.active
			it.visible = this.visible
		}
	}

	fun disable() {
		this.active = false
		this.visible = false
	}

	fun enable() {
		this.active = true
		this.visible = true
	}

	fun tick() {
		this.setChildrenVisibility()
		if (this.getContainerWidgets().isNotEmpty() && this.active && this.visible) this.tickContainerWidgets()
		if (this.active && this.visible) this.tickAdditional()
	}

	private fun setFocused(listener: GuiEventListener?) {
		this.screen.focused = listener
	}

	open fun tickAdditional() {}

	override fun playDownSound(handler: SoundManager) {
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (!this.active) return false
		return this.getAllWidgets().any { widget ->
			(widget.isHoveredOrFocused && widget.mouseClicked(mouseX, mouseY, button)).also {
				if (it) this.setFocused(widget)
			}
		}
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (!this.active) return false
		return this.getAllWidgets().any { it.keyPressed(keyCode, scanCode, modifiers) }
	}

	override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (!this.active) return false
		return this.getAllWidgets().any { it.keyReleased(keyCode, scanCode, modifiers) }
	}

	override fun visitWidgets(consumer: Consumer<AbstractWidget>): Unit = this.getAllWidgets().forEach(consumer)

	override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
		if (!this.active) return false
		return this.getAllWidgets().any { it.mouseReleased(mouseX, mouseY, button) }
	}

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
		if (!this.active) return false
		return this.getAllWidgets().any { it.mouseDragged(mouseX, mouseY, button, dragX, dragY) }
	}

	override fun mouseMoved(mouseX: Double, mouseY: Double) {
		if (!this.active) return
		this.getAllWidgets().any { widget ->
			widget.isHoveredOrFocused.also { if (it) widget.mouseMoved(mouseX, mouseY) }
		}
	}

	override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
		if (!this.active) return false
		return this.getAllWidgets().any { it.isHoveredOrFocused && it.mouseScrolled(mouseX, mouseY, scrollX, scrollY) }
	}

	override fun setPosition(x: Int, y: Int) {
		super.setPosition(x, y)
		this.getAllWidgets().forEach { it.setPosition(x + this.x, y + this.y) }
	}

	override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
		if (!this.active) return false
		return this.getAllWidgets().any { it.isHoveredOrFocused && it.charTyped(codePoint, modifiers) }
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		return if (this.getAllWidgets().any { it.isMouseOver(mouseX, mouseY) }) true
		else super.isMouseOver(mouseX, mouseY)
	}

	open fun addChild(
		id: String,
		widget: AbstractWidget,
		x: Int = 0,
		y: Int = 0,
		shouldRender: Boolean = true,
		isActive: Boolean = true
	) {
		check(this.subWidgets[id] == null) { "Widget with $id already exists!" }
		widget.visible = shouldRender
		widget.active = isActive
		if (x != 0 || y != 0) widget.setPosition(x, y)
		if (widget is ContainerWidget<*>) widget.init()
		this.subWidgets[id] = widget
	}

	fun positionChild(id: String, x: Int, y: Int) {
		this.subWidgets[id]?.let { it.x = x; it.y = y } ?: return
	}

	fun removeChild(id: String): AbstractWidget? = this.subWidgets.remove(id)

	/**
	 * Gets a child widget with a provided [id].
	 */
	fun getChild(id: String): AbstractWidget? = this.subWidgets[id]

	/**
	 * Initializes this [ContainerWidget].
	 *
	 * * override this method to add child widgets.
	 */
	open fun init() {}
}