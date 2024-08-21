package breadmod.item.tool_gun.mode

import breadmod.ModMain.modTranslatable
import breadmod.client.render.tool_gun.ToolGunAnimationHandler
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.IToolGunMode
import breadmod.item.tool_gun.IToolGunMode.Companion.playToolGunSound
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.clientbound.BeamPacket
import breadmod.network.serverbound.tool_gun.remover.ToolGunRemoverSDPacket
import breadmod.util.RaycastResult.Companion.entityRaycast
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor

internal class ToolGunRemoverMode : IToolGunMode {
    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if (pLevel is ServerLevel) {
            pLevel.entityRaycast(pPlayer, pPlayer.eyePosition, Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0)
                ?.let {
                    fun rand() = (pPlayer.random.nextDouble() - 0.5) * 1.2
                    pLevel.sendParticles(
                        ParticleTypes.END_ROD,
                        it.entity.x, it.entity.y, it.entity.z, 60,
                        rand(), pPlayer.random.nextDouble(), rand(), 1.0
                    )
                    playToolGunSound(pLevel, pPlayer.blockPosition())
                    NETWORK.send(
                        PacketDistributor.TRACKING_CHUNK.with { pLevel.getChunkAt(it.entity.blockPosition()) },
                        BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 0.1f)
                    )

                    if (it.entity is ServerPlayer) {
                        NETWORK.send(PacketDistributor.PLAYER.with { it.entity }, ToolGunRemoverSDPacket(null))
                        it.entity.connection.disconnect(modTranslatable("item", TOOL_GUN_DEF, "remover", "player_left_game"))
                    } else {
                        it.entity.discard()
                        pLevel.server.playerList.players.forEach { player ->
                            player.sendSystemMessage(
                                modTranslatable(
                                    "item", TOOL_GUN_DEF, "remover", "entity_left_game",
                                    args = listOf(it.entity.name)
                                ).withStyle(ChatFormatting.YELLOW)
                            )
                        }
                    }
                }
        } else if (pControl.id == "use") {
            if (pLevel.isClientSide) {
                ToolGunAnimationHandler.trigger()
            }
        }
    }
}