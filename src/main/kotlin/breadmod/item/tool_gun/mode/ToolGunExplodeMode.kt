package breadmod.item.tool_gun.mode

import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.item.tool_gun.IToolGunMode
import breadmod.item.tool_gun.IToolGunMode.Companion.playModeSound
import breadmod.item.tool_gun.IToolGunMode.Companion.playToolGunSound
import breadmod.network.BeamPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.util.RayMarchResult.Companion.rayMarchBlock
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor

internal class ToolGunExplodeMode: IToolGunMode {
    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if(pLevel is ServerLevel) {
            if(pControl.id == "use") {
                val settings = pGunStack.orCreateTag.getCompound(pControl.categoryKey)

                pLevel.rayMarchBlock(pPlayer.position(), Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0, settings.getBoolean("hitFluid"))?.let {
                    NETWORK.send(
                        PacketDistributor.TRACKING_CHUNK.with { pLevel.getChunkAt(pPlayer.blockPosition()) },
                        BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 5.0f)
                    )
                    playToolGunSound(pLevel, pPlayer.blockPosition())

                    pLevel.explode(pPlayer, it.endPosition.x, it.endPosition.y, it.endPosition.z, 20f, Level.ExplosionInteraction.MOB)
                }
            } else {
                if(!pGunStack.orCreateTag.contains(pControl.categoryKey)) {
                    pGunStack.orCreateTag.put(pControl.categoryKey, CompoundTag().also {
                        it.putBoolean("hitFluid", false)
                    })
                }
                pGunStack.orCreateTag.getCompound(pControl.categoryKey).also {
                    val newState = !it.getBoolean("hitFluid")
                    it.putBoolean("hitFluid", newState)
                }
                playModeSound(pLevel, pPlayer.blockPosition())
            }
        }
    }
}