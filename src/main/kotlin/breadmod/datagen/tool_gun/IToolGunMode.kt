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

    /**
     * Function to run whenever this action is closed
     * (either from the player equipping something else, or switching to another mode).
     *
     * **NOTE:** This will fire for every tick the toolgun is unequipped on both sides.
     * The mode implementation is expected to handle this possibility.
     *
     * **TODO:** w/ [newMode] will only execute server-side. Make run on client-side aswell.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun close(pLevel: Level, pPlayer: Player, pGunStack: ItemStack, newMode: IToolGunMode?) {}

    /**
     * Function to run whenever this action is opened
     * (either from the player equipping the toolgun, or switching to this mode).
     *
     * This executes before [action].
     *
     * **NOTE:** This will fire for every tick the toolgun is equipped on both sides.
     * The mode implementation is expected to handle this possibility.
     *
     * **TODO:** w/ [lastMode] will only execute server-side. Make run on client-side aswell.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun open(pLevel: Level, pPlayer: Player, pGunStack: ItemStack, lastMode: IToolGunMode?) {}

    companion object {
        fun playToolGunSound(pLevel: Level, at: BlockPos) =
            pLevel.playSound(null, at, ModSounds.TOOL_GUN.get(), SoundSource.PLAYERS, 2.0f, 1f)
        fun playModeSound(pLevel: Level, at: BlockPos) =
            pLevel.playSound(null, at, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 2.0f, 1f)
    }
}