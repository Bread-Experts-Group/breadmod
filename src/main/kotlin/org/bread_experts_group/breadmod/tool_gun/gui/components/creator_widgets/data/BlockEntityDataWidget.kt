package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.data

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.Color

class BlockEntityDataWidget<T : Tag>(
	screen: CreatorScreen,
	x: Int,
	y: Int,
	private val dataKey: String,
	private val data: T
) : ContainerWidget<CreatorScreen, BlockEntityDataWidget<T>>(
	x,
	y,
	140,
	40,
	"block_entity_data_widget",
	screen
) {
	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.LIGHT_GRAY, Color.GRAY)
		guiGraphics.drawScrollingString(
			localClient.font,
			Component.literal("${this.dataKey} : ${this.data.type.name}"),
			this.x + 2,
			this.x + 138,
			this.y + 2,
			Color.WHITE
		)
	}

	override fun initContainer() {
		super.initContainer()
	}
}