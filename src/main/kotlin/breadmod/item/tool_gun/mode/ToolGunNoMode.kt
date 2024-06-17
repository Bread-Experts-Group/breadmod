package breadmod.item.tool_gun.mode

import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.tool_gun.IToolGunMode
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

internal val ToolGunNoMode = ModToolGunModeDataLoader.ToolgunMode(
    Component.literal("???"),
    Component.literal("???"),
    listOf(),
    object: IToolGunMode {
        override fun action(
            pLevel: Level,
            pPlayer: Player,
            pGunStack: ItemStack,
            pControl: BreadModToolGunModeProvider.Control
        ) {}
    }
)