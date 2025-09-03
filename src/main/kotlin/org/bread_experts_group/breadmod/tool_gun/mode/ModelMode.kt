package org.bread_experts_group.breadmod.tool_gun.mode

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.KeyData
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ModelScreen

@ToolGunMode
class ModelMode : IToolGunMode {
	override fun action(
		level: Level,
		player: Player,
		stack: ItemStack
	) {
	}

	override fun registerKeys(into: MutableMap<Int, KeyData>) {
		into[InputConstants.KEY_F] = KeyData(Component.literal("open editor")) { event, stack, player, data ->
			if (localClient.screen == null) localClient.setScreen(ModelScreen())
		}
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("model_mode")
}