package org.bread_experts_group.breadmod.item.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunMode.Companion.playModeSound
import org.bread_experts_group.breadmod.api.IToolGunMode.Companion.playToolGunSound
import org.bread_experts_group.breadmod.client.render.tool_gun.drawTextOnScreen
import org.bread_experts_group.breadmod.client.render.tool_gun.drawWrappedTextOnScreen
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider
import org.bread_experts_group.breadmod.client.render.tool_gun.ToolGunAnimationHandler
import org.bread_experts_group.breadmod.network.clientbound.BeamPacket
import org.bread_experts_group.breadmod.util.RaycastResult.Companion.blockRaycast
import org.bread_experts_group.breadmod.util.rgMinecraft
import java.awt.Color

// todo replace getOrCreateTag calls with the new DataComponents
internal class ToolGunExplodeMode : IToolGunMode {
    private var hitFluid = false

    override fun action(
        level: Level,
        player: Player,
        gunStack: ItemStack,
        control: ToolGunModeProvider.Control
    ) {
        if (level is ServerLevel) {
            Breadmod.LOGGER.info("ToolGunExplodeMode: triggering explode action on server side")
            Breadmod.LOGGER.info("ToolGunExplodeMode: Control key is $control")
            if (control.id == "use") {
//                val settings = gunStack.orCreateTag.getCompound(control.categoryKey)

                Breadmod.LOGGER.info("ToolGunExplodeMode: before blockRaycast")
                level.blockRaycast(
                    player.eyePosition,
                    Vec3.directionFromRotation(player.xRot, player.yRot),
                    1000.0,
                    /*settings.getBoolean("hitFluid")*/ false
                )?.let {
                    Breadmod.LOGGER.info("ToolGunExplodeMode: sending blockRayCast to server")
                    PacketDistributor.sendToPlayersTrackingChunk(
                        level,
                        player.chunkPosition(),
                        BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 1.0f)
                    )
                    playToolGunSound(level, player.blockPosition())
                    level.explode(
                        player,
                        it.endPosition.x,
                        it.endPosition.y,
                        it.endPosition.z,
                        20f,
                        Level.ExplosionInteraction.MOB
                    )
                }
            } else {
//                if (!gunStack.orCreateTag.contains(control.categoryKey)) {
//                    gunStack.orCreateTag.put(control.categoryKey, CompoundTag().also {
//                        it.putBoolean("hitFluid", false)
//                        hitFluid = false
//                    })
//                }
//                gunStack.orCreateTag.getCompound(control.categoryKey).also {
//                    val newState = !it.getBoolean("hitFluid")
//                    it.putBoolean("hitFluid", newState)
//                    hitFluid = newState
//                }
                playModeSound(level, player.blockPosition())
            }
        } else if (control.id == "use") {
            if (level.isClientSide) {
                ToolGunAnimationHandler.trigger()
            }
        }
    }

    override fun render(
        pGunStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        drawWrappedTextOnScreen(
            rgMinecraft.font,
            modTranslatable("tool_gun", "mode", "explode", "hit_fluid"),
            pPoseStack,
            pBuffer,
            Color.BLACK.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            0.925, 0.0635, -0.036,
            10f, 0.0007f, 100
        )
        drawTextOnScreen(
            modTranslatable("tool_gun", "mode", "explode", "hit_fluid", if (hitFluid) "enabled" else "disabled"),
            if (hitFluid) Color(35, 189, 0, 255).rgb else Color.RED.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            rgMinecraft.font,
            pPoseStack,
            pBuffer,
            0.92, 0.052, -0.036,
            0.0007f
        )
    }
}