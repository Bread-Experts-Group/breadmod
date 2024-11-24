package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.client.render.tool_gun.ToolGunAnimationHandler
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider

internal class ToolGunPowerMode : IToolGunMode {
    var count = 0

    override fun action(
        level: Level,
        player: Player,
        gunStack: ItemStack,
        control: ToolGunModeProvider.Control
    ) {
        // Really important. Don't let computerSD run on the server.
        if (level.isClientSide) {
            ToolGunAnimationHandler.trigger()
            count++
            if (count < 5) return
//            computerSD(true)
        }
    }

    override fun close(level: Level, player: Player, gunStack: ItemStack, newMode: IToolGunMode?) {
        count = 0
    }
}