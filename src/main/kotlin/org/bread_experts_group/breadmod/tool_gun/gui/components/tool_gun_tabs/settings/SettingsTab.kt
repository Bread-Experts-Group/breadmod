package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.gui.components.TabButton
import org.bread_experts_group.breadmod.client.render.drawCenteredWordWrap
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.ToolGunScreenTab
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnum.MAIN
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries.MainEntry
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries.RendererEntry
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries.SettingsEntry
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen
import org.bread_experts_group.breadmod.util.Color

class SettingsTab(
	screen: ToolGunScreen,
	stack: ItemStack
) : ToolGunScreenTab("settings", Color.color(b = 180), screen, stack) {
	companion object {
		var currentSettingsEntry: SettingsEntryEnum = MAIN
	}

	override fun getTabButton(): TabButton =
		TabButton(
			Component.literal("settings"),
			Color.BLUE,
			Color.color(b = 230),
			this
		)

	override fun initContainer() {
		Companion.currentSettingsEntry = MAIN
		this.addSettingsEntry(RendererEntry(this.screen, this.stack))
		this.addSettingsEntry(MainEntry(this.screen, this.stack))
		var entryPos = 30
		this.getWidgets().filterIsInstance<SettingsEntry>().forEach { entry ->
			this.addChild(
				"${entry.id}_button",
				entry.entryButton,
				this.x + 5,
				this.y + entryPos
			)
			entryPos += 12
		}
		this.getChild("setting_entry_main_button").setPosition(this.x + 5, this.y + 5)
	}

	override fun tickAdditional() {
		this.getChild("setting_entry_main_button").let {
			it.active = Companion.currentSettingsEntry != MAIN
			it.visible = Companion.currentSettingsEntry != MAIN
		}
	}

	private fun addSettingsEntry(entry: SettingsEntry) {
		entry.setPosition(this.x, this.y)
		this.addChild(entry.id, entry)
	}

	private val color: Int = Color.color(b = 230)
	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.fill(
			this.x,
			this.y,
			this.x + 243,
			this.y + 185,
			this.color
		)
		this.getWidgets().filterIsInstance<SettingsEntryButton>()
			.asSequence()
			.filter { it.isHovered && it.visible && Companion.currentSettingsEntry == MAIN }
			.forEach {
				guiGraphics.drawCenteredWordWrap(
					localClient.font,
					it.description,
					this.screen.width / 2 + 133,
					this.y + 150,
					260,
					Color.WHITE
				)
			}
	}
}