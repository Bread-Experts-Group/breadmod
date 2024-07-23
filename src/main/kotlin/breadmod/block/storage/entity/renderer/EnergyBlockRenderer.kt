package breadmod.block.storage.entity.renderer

import breadmod.block.storage.entity.EnergyStorageBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import java.awt.Color

class EnergyBlockRenderer: BaseAbstractStorageBlockRenderer<EnergyStorageBlockEntity>() {

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
            Component.literal("$energyStored FE"),
            Color.GREEN.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            pPoseStack, pBuffer, pBlockEntity,
            0.0105f,0.1, 0.14
        )
        drawTextOnSide(
            Component.literal("-------------"),
            Color.GREEN.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            pPoseStack, pBuffer, pBlockEntity,
            0.0105f,0.095, 0.2
        )
        drawTextOnSide(
            Component.literal("$maxEnergyStored FE"),
            Color.GREEN.rgb,
            Color(0, 0, 0, 0).rgb,
            false,
            pPoseStack, pBuffer, pBlockEntity,
            0.0105f,0.1, 0.26
        )
    }
}