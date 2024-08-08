package breadmod.item.tool_gun.mode

import breadmod.ModMain.modTranslatable
import breadmod.client.render.tool_gun.ToolGunAnimationHandler
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.item.tool_gun.IToolGunMode
import breadmod.item.tool_gun.IToolGunMode.Companion.playModeSound
import breadmod.item.tool_gun.IToolGunMode.Companion.playToolGunSound
import breadmod.client.render.tool_gun.drawTextOnScreen
import breadmod.client.render.tool_gun.drawWrappedTextOnScreen
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.client.BeamPacket
import breadmod.util.RayMarchResult.Companion.rayMarchBlock
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor
import java.awt.Color

internal class ToolGunExplodeMode: IToolGunMode {
    private var hitFluid = false

    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if(pLevel is ServerLevel) {
            if(pControl.id == "use") {
                val settings = pGunStack.orCreateTag.getCompound(pControl.categoryKey)

                pLevel.rayMarchBlock(pPlayer.eyePosition, Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0, settings.getBoolean("hitFluid"))?.let {
                    NETWORK.send(
                        PacketDistributor.TRACKING_CHUNK.with { pLevel.getChunkAt(pPlayer.blockPosition()) },
                        BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 1.0f)
                    )
                    playToolGunSound(pLevel, pPlayer.blockPosition())

                    pLevel.explode(pPlayer, it.endPosition.x, it.endPosition.y, it.endPosition.z, 20f, Level.ExplosionInteraction.MOB)
                }
            } else {
                if(!pGunStack.orCreateTag.contains(pControl.categoryKey)) {
                    pGunStack.orCreateTag.put(pControl.categoryKey, CompoundTag().also {
                        it.putBoolean("hitFluid", false)
                        hitFluid = false
                    })
                }
                pGunStack.orCreateTag.getCompound(pControl.categoryKey).also {
                    val newState = !it.getBoolean("hitFluid")
                    it.putBoolean("hitFluid", newState)
                    hitFluid = newState
                }
                playModeSound(pLevel, pPlayer.blockPosition())
            }
        } else if(pControl.id == "use") {
            if(pLevel.isClientSide) {
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
        val fontRenderer = Minecraft.getInstance().font

        drawWrappedTextOnScreen(
            fontRenderer,
            modTranslatable("tool_gun", "mode", "explode", "hit_fluid"),
            pPoseStack,
            pBuffer,
            Color.BLACK.rgb,
            Color(0,0,0,0).rgb,
            false,
            0.925, 0.0635, -0.036,
            10f, 0.0007f, 100
        )
        drawTextOnScreen(
            modTranslatable("tool_gun", "mode", "explode", "hit_fluid", if(hitFluid) "enabled" else "disabled"),
            if (hitFluid) Color(35,189,0,255).rgb else Color.RED.rgb,
            Color(0,0,0,0).rgb,
            false,
            fontRenderer,
            pPoseStack,
            pBuffer,
            0.92, 0.052, -0.036,
            0.0007f
        )
    }
}