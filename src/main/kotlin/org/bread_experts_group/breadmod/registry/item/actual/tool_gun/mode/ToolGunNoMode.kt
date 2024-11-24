package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
internal val ToolGunNoMode = ToolGunModeDataLoader.ToolgunMode(
    Component.literal("???"),
    Component.literal("???"),
    listOf(),
    object : IToolGunMode {
        override fun action(
            level: Level,
            player: Player,
            gunStack: ItemStack,
            control: ToolGunModeProvider.Control
        ) {
        }
    }
)