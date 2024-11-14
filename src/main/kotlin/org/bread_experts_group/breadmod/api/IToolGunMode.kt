package org.bread_experts_group.breadmod.api

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.Breadmod.Companion.DATA_DIR
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import java.nio.file.Path

interface IToolGunMode {
    /**
     * An action carried out by this [IToolGunMode].
     * This will be executed whenever the player uses a key as defined by [ToolGunModeProvider], on
     * the [InputConstants.RELEASE] stage of a key.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun action(level: Level, player: Player, gunStack: ItemStack, control: ToolGunModeProvider.Control) {}

    /**
     * An action carried out by this [IToolGunMode].
     * This will be executed whenever the player uses a key as defined by [ToolGunModeProvider], on
     * the [InputConstants.PRESS] stage of a key.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun actionEarly(level: Level, player: Player, gunStack: ItemStack, control: ToolGunModeProvider.Control) {}


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
    fun close(level: Level, player: Player, gunStack: ItemStack, newMode: IToolGunMode?) {}

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
    fun open(level: Level, player: Player, gunStack: ItemStack, lastMode: IToolGunMode?) {}

    /**
     * Gateway to [ToolGunItemRenderer] for [IToolGunMode]s. This will run every frame.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun render(
        pGunStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
    }

    companion object {
        val BASE_TOOL_GUN_DATA_PATH: Path = DATA_DIR.resolve("tool_gun")

        fun playToolGunSound(pLevel: Level, at: BlockPos) =
            pLevel.playSound(null, at, ModSounds.TOOL_GUN.get(), SoundSource.PLAYERS, 2.0f, 1f)

        fun playModeSound(pLevel: Level, at: BlockPos) =
            pLevel.playSound(null, at, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 2.0f, 1f)
    }
}