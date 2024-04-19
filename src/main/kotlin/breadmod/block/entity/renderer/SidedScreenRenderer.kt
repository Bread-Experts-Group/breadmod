package breadmod.block.entity.renderer

import breadmod.block.entity.BreadScreenBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer

class SidedScreenRenderer: BlockEntityRenderer<BreadScreenBlockEntity> {
    override fun render(
        pBlockEntity: BreadScreenBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int,
    ) {
        pPoseStack.pushPose()

        pPoseStack.popPose()
    }
}