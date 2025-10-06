package org.bread_experts_group.breadmod.tool_gun.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.tool_gun.Model
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ModelScreen
import org.bread_experts_group.breadmod.util.Color

class ModelWidget(
	screen: ModelScreen,
	private val model: Model,
	x: Int,
	y: Int
) : ContainerWidget<ModelScreen>(x, y, 93, 40, "model_widget", screen) {
	private val stack: ItemStack = ItemStack(this.model.state.block)

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.WHITE, Color.BLACK)
		guiGraphics.renderItem(this.stack, this.x + 1, this.y + 1)
		this.model.isHighlighted = this.isHovered
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
		AbstractWidget.wrapDefaultNarrationMessage(this.message)
	}

	override fun initContainer() {
		this.addChild("move_horizontal", GenericButton(this.x + 1, this.y + 20, 20, 15, "R/L") { button, keyCode ->
			if (keyCode == 0) this.model.move(1.0, 0.0, 0.0)
			else this.model.move(-1.0, 0.0, 0.0)
		})
		this.addChild(
			"move_horizontal_alt",
			GenericButton(this.x + 23, this.y + 20, 25, 15, "F/B") { button, keyCode ->
				if (keyCode == 0) this.model.move(0.0, -1.0, 0.0)
				else this.model.move(0.0, 1.0, 0.0)
			})
		this.addChild(
			"move_vertical",
			GenericButton(this.x + 49, this.y + 20, 25, 15, "U/D") { button, keyCode ->
				if (keyCode == 0) this.model.move(0.0, 0.0, 1.0)
				else this.model.move(0.0, 0.0, -1.0)
			})
		this.addChild("state_iterator", GenericButton(this.x + 20, this.y + 1, 55, 15, "next state") { _, _ ->
			this.model.nextState()
		})
	}
}