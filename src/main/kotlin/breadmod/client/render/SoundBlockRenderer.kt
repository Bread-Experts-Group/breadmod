package breadmod.client.render

import breadmod.block.entity.SoundBlockEntity
import breadmod.util.render.drawTextOnSide
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer

// todo adapt GuiGraphics scrolling text to BER
class SoundBlockRenderer: BlockEntityRenderer<SoundBlockEntity> {
    override fun render(
        pBlockEntity: SoundBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
//        drawTextOnSide()
    }
}