package breadmod.datagen.tool_gun.mode

import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.datagen.tool_gun.IToolGunMode
import breadmod.network.BeamPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.util.RayMarchResult.Companion.rayMarchEntity
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor
import kotlin.random.Random

internal class ToolGunRemoverMode: IToolGunMode {
    private val random = Random(-3428960)
    override fun action(pPlayer: Player, pGunStack: ItemStack) {
        val level = pPlayer.level()
        if(level is ServerLevel) {
            level.rayMarchEntity(pPlayer, pPlayer.position(), Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0)?.let {
                fun rand() = (random.nextDouble() - 0.5)*1.2
                level.sendParticles(ParticleTypes.END_ROD, it.entity.x, it.entity.y, it.entity.z, 40, rand(), random.nextDouble(), rand(), 1.0)
                playToolGunSound(level, pPlayer.blockPosition())
                NETWORK.send(
                    PacketDistributor.TRACKING_CHUNK.with { level.getChunkAt(it.entity.blockPosition()) },
                    BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 0.1)
                )

                if(it.entity is ServerPlayer) it.entity.connection.disconnect(modTranslatable("item", "tool_gun", "player_left_game"))
                else {
                    it.entity.discard()
                    level.server.playerList.players.forEach { player -> player.sendSystemMessage(modTranslatable("item", TOOL_GUN_DEF, "entity_left_game", args = listOf(it.entity.name)).withStyle(ChatFormatting.YELLOW)) }
                }
            }
        }
    }
}