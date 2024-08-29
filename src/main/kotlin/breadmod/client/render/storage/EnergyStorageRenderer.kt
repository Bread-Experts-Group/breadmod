package breadmod.client.render.storage

import breadmod.block.entity.storage.EnergyStorageBlockEntity
import breadmod.util.render.drawTextOnSide
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import java.awt.Color

internal class EnergyStorageRenderer : BaseAbstractStorageBlockRenderer<EnergyStorageBlockEntity>() {
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
            0.1, -0.125,
            pPoseStack = pPoseStack, pBuffer = pBuffer,
            pBlockState = pBlockEntity.blockState,
            pColor = Color.GREEN.rgb,
            pScale = 0.0105f
        )
        drawTextOnSide(
            fontRenderer,
            Component.literal("-------------"),
            0.095, -0.185,
            pPoseStack = pPoseStack, pBuffer = pBuffer,
            pBlockState = pBlockEntity.blockState,
            pColor = Color.GREEN.rgb,
            pScale = 0.0105f
        )
        drawTextOnSide(
            fontRenderer,
            Component.literal("$maxEnergyStored FE"),
            0.1, pPosY = -0.245,
            pPoseStack = pPoseStack, pBuffer = pBuffer,
            pBlockState = pBlockEntity.blockState,
            pColor = Color.GREEN.rgb,
            pScale = 0.0105f
        )
    }
}