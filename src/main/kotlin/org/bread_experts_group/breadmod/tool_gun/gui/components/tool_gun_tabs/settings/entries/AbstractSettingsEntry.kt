package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryButton
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnum
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnum.MAIN
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsTab
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen

abstract class AbstractSettingsEntry(
	id: String,
	val entry: SettingsEntryEnum,
	screen: ToolGunScreen,
	val stack: ItemStack,
	private val message: Component,
	private val description: Component = Component.empty()
) : ContainerWidget<ToolGunScreen>(0, 0, 143, 185, "setting_entry_$id", screen) {
	val entryButton: SettingsEntryButton = SettingsEntryButton(this.message, this.description, this.entry)
	override fun tickAdditional() {
		this.visible = SettingsTab.currentSettingsEntry == this.entry
		this.active = SettingsTab.currentSettingsEntry == this.entry
		this.entryButton.let {
			it.active = SettingsTab.currentSettingsEntry == MAIN || it.entry == MAIN
			it.visible = SettingsTab.currentSettingsEntry == MAIN || it.entry == MAIN
		}
	}
}