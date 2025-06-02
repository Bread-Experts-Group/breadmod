package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.tool_gun.gui.components.SettingsEntryButton
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnum
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnum.MAIN
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsTab
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen

abstract class SettingsEntry(
	id: String,
	val entry: SettingsEntryEnum,
	screen: ToolGunScreen,
	val stack: ItemStack,
	val entryButton: SettingsEntryButton
) : ContainerWidget<ToolGunScreen, SettingsEntry>(0, 0, 143, 185, "setting_entry_$id", screen) {
	override fun tickAdditional() {
		this.visible = SettingsTab.currentSettingsEntry == this.entry
		this.active = SettingsTab.currentSettingsEntry == this.entry
		this.entryButton.let {
			it.active = SettingsTab.currentSettingsEntry == MAIN || it.entry == MAIN
			it.visible = SettingsTab.currentSettingsEntry == MAIN || it.entry == MAIN
		}
	}
}