package breadmod.datagen.toolgun

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface IToolgunMode {
    /**
     * An action carried out by this [IToolgunMode].
     * This will be executed whenever the player uses a key as defined by [BreadModToolgunModeProvider].
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun action(pPlayer: Player, pGunStack: ItemStack)
}