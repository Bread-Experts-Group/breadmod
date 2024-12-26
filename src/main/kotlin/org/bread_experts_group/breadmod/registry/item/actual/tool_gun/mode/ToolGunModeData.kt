package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.experimental.tool_gun_mode.ModeWidget

// todo integrate this into the tool gun data provider/loader addedModes instead of using a mess of pairs
data class ToolGunModeData<T : ToolGunMode>(
	val name : String,
	val displayName : Component,
	val tooltip : Component,
	val actionClass : Class<T>,
//	val previewImage : ResourceLocation,
	val widget : ModeWidget // todo this has it's own data that'll need to be most likely redone
)