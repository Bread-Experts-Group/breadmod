package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryButton
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryEnums
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryEnums.MAIN
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsTab
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen

abstract class SettingsEntry(
	id: String,
	val entry: SettingsEntryEnums,
	screen: ToolGunScreen,
	val stack: ItemStack,
	val entryButton: SettingsEntryButton
) : ContainerWidget<ToolGunScreen>(0, 0, 143, 185, "setting_entry_$id", screen) {
	override fun tickAdditional() {
		this.visible = SettingsTab.currentSettingsEntry == this.entry
		this.active = SettingsTab.currentSettingsEntry == this.entry
		this.entryButton.let {
			it.active = SettingsTab.currentSettingsEntry == MAIN || it.entry == MAIN
			it.visible = SettingsTab.currentSettingsEntry == MAIN || it.entry == MAIN
		}
	}
}