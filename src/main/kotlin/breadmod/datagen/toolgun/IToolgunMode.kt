package breadmod.datagen.toolgun

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface IToolgunMode {
    fun action(pPlayer: Player, pGunStack: ItemStack)
}