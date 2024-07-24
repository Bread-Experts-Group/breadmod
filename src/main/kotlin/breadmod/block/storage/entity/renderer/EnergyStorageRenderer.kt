package breadmod.block.storage.entity.renderer

import breadmod.block.storage.entity.EnergyStorageBlockEntity
import breadmod.util.render.drawTextOnSide
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import java.awt.Color

class EnergyStorageRenderer: BaseAbstractStorageBlockRenderer<EnergyStorageBlockEntity>() {

    override fun render(
        pBlockEntity: EnergyStorageBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val energyStored = energyHandler(pBlockEntity)?.energyStored
        val maxEnergyStored = energyHandler(pBlockEntity)?.maxEnergyStored

        drawTextOnSide(
            fontRenderer,
            Component.literal("$energyStored FE"),
            Color.GREEN.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            pPoseStack, pBuffer, pBlockEntity.blockState,
            pScale = 0.0105f, pPosX = 0.1, pPosY = -0.125
        )
        drawTextOnSide(
            fontRenderer,
            Component.literal("-------------"),
            Color.GREEN.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            pPoseStack, pBuffer, pBlockEntity.blockState,
            pScale = 0.0105f, pPosX = 0.095, pPosY = -0.185
        )
        drawTextOnSide(
            fontRenderer,
            Component.literal("$maxEnergyStored FE"),
            Color.GREEN.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            pPoseStack, pBuffer, pBlockEntity.blockState,
            pScale = 0.0105f, pPosX = 0.1, pPosY = -0.245
        )
    }
}