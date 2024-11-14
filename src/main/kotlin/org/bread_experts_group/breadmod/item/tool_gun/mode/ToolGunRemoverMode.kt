package org.bread_experts_group.breadmod.item.tool_gun.mode

import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.Breadmod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunMode.Companion.playToolGunSound
import org.bread_experts_group.breadmod.client.render.tool_gun.ToolGunAnimationHandler
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.network.clientbound.BeamPacket
import org.bread_experts_group.breadmod.util.RaycastResult.Companion.entityRaycast

internal class ToolGunRemoverMode : IToolGunMode {
    override fun action(
        level: Level,
        player: Player,
        gunStack: ItemStack,
        control: ToolGunModeProvider.Control
    ) {
        if (level is ServerLevel) {
            level.entityRaycast(
                player,
                player.position().add(0.0, player.eyeHeight.toDouble(), 0.0),
                Vec3.directionFromRotation(player.xRot, player.yRot),
                1000.0
            )?.let {
                fun rand() = (player.random.nextDouble() - 0.5) * 1.2
                level.sendParticles(
                    ParticleTypes.END_ROD,
                    it.entity.x, it.entity.y, it.entity.z, 60,
                    rand(), player.random.nextDouble(), rand(), 1.0
                )
                playToolGunSound(level, player.blockPosition())
                PacketDistributor.sendToPlayersTrackingChunk(
                    level,
                    player.chunkPosition(),
                    BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 0.1f)
                )

                if (it.entity is ServerPlayer) {
//                    NETWORK.send(PacketDistributor.PLAYER.with { it.entity }, ToolGunRemoverSDPacket(null))
                    it.entity.connection.disconnect(
                        modTranslatable(
                            "item",
                            TOOL_GUN_DEF,
                            "remover",
                            "player_left_game"
                        )
                    )
                } else {
                    it.entity.discard()
                    level.server.playerList.players.forEach { player ->
                        player.sendSystemMessage(
                            modTranslatable(
                                "item", TOOL_GUN_DEF, "remover", "entity_left_game",
                                args = listOf(it.entity.name)
                            ).withStyle(ChatFormatting.YELLOW)
                        )
                    }
                }
            }
        } else if (control.id == "use") {
            if (level.isClientSide) {
                ToolGunAnimationHandler.trigger()
            }
        }
    }
}