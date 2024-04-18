package breadmod.block.entity.renderer

import breadmod.block.entity.HeatingElementBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer

class SidedScreenRenderer: BlockEntityRenderer<HeatingElementBlockEntity> {
    override fun render(
        pBlockEntity: HeatingElementBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int,
    ) {

    }
}