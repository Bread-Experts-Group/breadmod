package org.bread_experts_group.breadmod.client.tool_gun.client_modes

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist.CLIENT
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.tool_gun.ModeWidget
import org.bread_experts_group.breadmod.client.tool_gun.ModeWidget.Builder

@ToolGunMode(CLIENT)
class EmptyModeClient : AbstractToolGunModeClient() {
	override fun getModeWidget(): ModeWidget = Builder().id(this.getUid()).build()

	override fun getDisplayName(): Component = Component.literal("???")

	override fun getUid(): ResourceLocation = modLocation("tool_gun", "empty_mode")
}