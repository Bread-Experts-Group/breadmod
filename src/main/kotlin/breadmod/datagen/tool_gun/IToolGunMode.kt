package breadmod.datagen.tool_gun

import breadmod.registry.sound.ModSounds
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

interface IToolGunMode {
    /**
     * An action carried out by this [IToolGunMode].
     * This will be executed whenever the player uses a key as defined by [BreadModToolGunModeProvider].
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun action(pLevel: Level, pPlayer: Player, pGunStack: ItemStack, pControl: BreadModToolGunModeProvider.Control)

    companion object {
        fun playToolGunSound(pLevel: Level, at: BlockPos) =
            pLevel.playSound(null, at, ModSounds.TOOL_GUN.get(), SoundSource.PLAYERS, 2.0f, 1f)
        fun playModeSound(pLevel: Level, at: BlockPos) =
            pLevel.playSound(null, at, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 2.0f, 1f)
    }
}