package breadmod.client.render

import breadmod.block.entity.SoundBlockEntity
import breadmod.util.render.drawTextOnSide
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.Util
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth.lerp
import net.minecraftforge.registries.ForgeRegistries
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
        val soundString = pBlockEntity.currentSound ?: return
        val sound = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation(soundString))?.location ?: return
        val convertedString = if (sound.namespace == "minecraft") "subtitles." + sound.path else sound.path

        // TODO split into render general
        // TODO fix the scrolling not working properly
        val pText = Component.literal("dummydummydummydummy")
        val pMinX = 0
        val pMaxX = 12

        // is line height needed here?
        val pMinY = 0
        val pMaxY = rgMinecraft.font.lineHeight

        val textWidth: Int = pText.string.length
        val yPos: Int = (pMinY + pMaxY - 9) / 2
        val maxWidth: Int = pMaxX - pMinX
        if (textWidth > maxWidth) {
            val scrollAmount = pText.string.length.toDouble() / 20.0
            val scrollSpeed = Util.getMillis().toDouble() / 1000.0
            // scroll delta? the math that smooths out the scrolling?
            val d1 = max(scrollAmount * 0.5, 3.0)
            // the thing that smoothly scrolls it forward and back
            val d2 = sin((Math.PI / 2.0) * cos((Math.PI * 2.0) * scrollSpeed / d1)) / 2.0 + 0.5
            // does this even do anything? I replaced it with d2 in posX, yet it didn't change the scrolling
            val d3 = lerp(d2, 0.0, scrollAmount)
//            pGuiGraphics.enableScissor(pMinX, pMinY, pMaxX, pMaxY)
            drawTextOnSide(
                rgMinecraft.font, pText,
                pMinX - d2, yPos.toDouble(),
                pPoseStack = pPoseStack, pBuffer = pBuffer,
                pBlockState = pBlockEntity.blockState,
                pScale = 0.0105f
            )
//            pGuiGraphics.disableScissor()
        } else {
            drawTextOnSide(
                rgMinecraft.font, pText,
                0.0, yPos.toDouble(),
                pPoseStack = pPoseStack, pBuffer = pBuffer,
                pBlockState = pBlockEntity.blockState,
                pScale = 0.0105f
            )
        }

        drawTextOnSide(
            rgMinecraft.font, Component.translatable(convertedString),
            0.1, -0.1,
            pPoseStack = pPoseStack, pBuffer = pBuffer,
            pBlockState = pBlockEntity.blockState,
            pScale = 0.0105f
        )
    }
}