package breadmod.datagen.tool_gun.mode

import breadmod.datagen.tool_gun.IToolGunMode
import breadmod.network.BeamPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.util.RayMarchResult.Companion.rayMarchBlock
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor

internal class ToolGunExplodeMode: IToolGunMode {
    override fun action(pPlayer: Player, pGunStack: ItemStack) {
        val level = pPlayer.level()
        if(level is ServerLevel) {
            level.rayMarchBlock(pPlayer.position(), Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0, false)?.let {
                NETWORK.send(
                    PacketDistributor.TRACKING_CHUNK.with { level.getChunkAt(pPlayer.blockPosition()) },
                    BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 0.1)
                )
                playToolGunSound(level, pPlayer.blockPosition())

                level.explode(pPlayer, it.endPosition.x, it.endPosition.y, it.endPosition.z, 20f, Level.ExplosionInteraction.MOB)
            }
        }
    }
}