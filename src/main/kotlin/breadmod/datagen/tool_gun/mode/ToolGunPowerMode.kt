package breadmod.datagen.tool_gun.mode

import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.IToolGunMode
import breadmod.util.computerSD
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

internal class ToolGunPowerMode: IToolGunMode {
    var count = 0

    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        // Really important. Do not let computerSD run on the server.
        if(pLevel.isClientSide) {
            count++
            println(count)
            if(count < 5) return
            //computerSD(true)
        }
    }

    override fun close(pLevel: Level, pPlayer: Player, pGunStack: ItemStack, newMode: IToolGunMode?) {
        count = 0
    }
}