package breadmod.block.entity.renderer

import breadmod.block.entity.BreadScreenBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import java.awt.Color

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
        pPoseStack.translate(0.5f, 0.75f * 0.6666667f, 0.5f)
        Minecraft.getInstance().font.drawInBatch(
            "A",
            0F,
            0F,
            Color.WHITE.rgb,
            false,
            pPoseStack.last().pose(),
            pBuffer,
            Font.DisplayMode.NORMAL,
            Color(0,0,255,128).rgb,
            15728880
        )
        pPoseStack.popPose()
    }
}