package breadmod.client.render

import breadmod.block.entity.SoundBlockEntity
import breadmod.util.render.drawCenteredTextOnSide
import breadmod.util.render.drawTextOnSide
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.Util
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

internal class SoundBlockRenderer : BlockEntityRenderer<SoundBlockEntity> {
    override fun render(
        pBlockEntity: SoundBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        // TODO split into render general
        val pText = Component.literal("dummy")
        val pMinX = 0
        val pMaxX = 100
        val pMinY = 0
        val pMaxY = rgMinecraft.font.lineHeight

        val i: Int = rgMinecraft.font.width(pText)
        val j: Int = (pMinY + pMaxY - 9) / 2 + 1
        val k: Int = pMaxX - pMinX
        if (i > k) {
            val l = i - k
            val d0 = Util.getMillis().toDouble() / 1000.0
            val d1 = max(l.toDouble() * 0.5, 3.0)
            val d2 = sin((Math.PI / 2.0) * cos((Math.PI * 2.0) * d0 / d1)) / 2.0 + 0.5
            val d3 = Mth.lerp(d2, 0.0, l.toDouble())
//            pGuiGraphics.enableScissor(pMinX, pMinY, pMaxX, pMaxY)
            drawTextOnSide(
                rgMinecraft.font, pText,
                pMinX - d3, j.toDouble(),
                pPoseStack = pPoseStack, pBuffer = pBuffer,
                pBlockState = pBlockEntity.blockState,
                pScale = 0.0105f
            )
//            pGuiGraphics.disableScissor()
        } else {
            drawCenteredTextOnSide(
                rgMinecraft.font, pText,
                (pMinX + pMaxX) / 2.0, j.toDouble(),
                pPoseStack = pPoseStack, pBuffer = pBuffer,
                pBlockState = pBlockEntity.blockState,
                pScale = 0.0105f
            )
        }
    }
}