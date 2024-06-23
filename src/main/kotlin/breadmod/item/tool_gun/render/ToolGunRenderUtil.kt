package breadmod.item.tool_gun.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence

private const val SCREEN_TINT = 0xFFFFFF

/**
 * Renders a given [Component] onto a [BlockEntityWithoutLevelRenderer]
 *
 * @param pComponent The text as a [Component.literal] or [Component.translatable] to be rendered onto the target model.
 * @param pColor The primary text color as an integer.
 * @param pBackgroundColor Secondary text color as an integer, applies to the background
 * @param pFontRenderer Draws the text onto the target model
 * @param pPoseStack Positions the text onto the target model
 * @param pBuffer see [MultiBufferSource]
 *
 * @see Font.drawInBatch
 * @author Logan McLean
 * @since 0.0.1
 */
fun renderText(
    pComponent: Component,
    pColor: Int,
    pBackgroundColor: Int,
    pFontRenderer: Font,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource
) {
    pFontRenderer.drawInBatch(
        pComponent,
        0f,
        0f,
        pColor,
        false,
        pPoseStack.last().pose(),
        pBuffer,
        Font.DisplayMode.NORMAL,
        pBackgroundColor,
        SCREEN_TINT
    )
}

fun drawTextOnScreen(
    pComponent: Component,
    pColor: Int,
    pBackgroundColor: Int,
    pFontRenderer: Font,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pPosX: Double,
    pPosY: Double,
    pPosZ: Double,
    pScale: Float
) {
    pPoseStack.pushPose()
    pPoseStack.translate(pPosX, pPosY, pPosZ)
    pPoseStack.scale(pScale, pScale, pScale)
    pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
    pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
    pPoseStack.mulPose(Axis.XP.rotationDegrees(-22.5f))
    renderText(pComponent, pColor, pBackgroundColor, pFontRenderer, pPoseStack, pBuffer)
    pPoseStack.popPose()
}

/**
 * @see drawTextOnScreen
 */
fun drawTextOnScreen(
    pText: String,
    pColor: Int,
    pBackgroundColor: Int,
    pFontRenderer: Font,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pPosX: Double,
    pPosY: Double,
    pPosZ: Double,
    pScale: Float
) = drawTextOnScreen(
    Component.literal(pText),
    pColor, pBackgroundColor, pFontRenderer, pPoseStack, pBuffer, pPosX, pPosY, pPosZ, pScale
)

//fun drawWrappedTextOnScreen( // Old wrapped text function using FormattedCharSequence, can possibly be repurposed
//    pFont: Font,
//    pText: FormattedText,
//    pPoseStack: PoseStack,
//    pBuffer: MultiBufferSource,
//    pColor: Int,
//    pBackgroundColor: Int,
//    pPosX: Double,
//    pPosY: Double,
//    pPosZ: Double,
//    pSplitY: Float,
//    pScale: Float,
//    pLineWidth: Int
//) {
//    pPoseStack.pushPose()
//    pPoseStack.translate(pPosX, pPosY, pPosZ)
//    pPoseStack.scale(pScale, pScale, pScale)
//    pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
//    pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
//    pPoseStack.mulPose(Axis.XP.rotationDegrees(-22.5f))
//    var split: Float = pSplitY
//    for(formattedCharSequence: FormattedCharSequence in pFont.split(pText, pLineWidth)) {
//        pFont.drawInBatch(
//            formattedCharSequence,
//            0f,
//            split,
//            pColor,
//            false,
//            pPoseStack.last().pose(),
//            pBuffer,
//            Font.DisplayMode.NORMAL,
//            pBackgroundColor,
//            SCREEN_TINT
//        )
//        split += 9f
//    }
//    pPoseStack.popPose()
//}

fun drawWrappedTextOnScreen(
    pFont: Font,
    pText: Component,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pColor: Int,
    pBackgroundColor: Int,
    pPosX: Double,
    pPosY: Double,
    pPosZ: Double,
    pSplitY: Float,
    pScale: Float,
    pLineWidth: Int
) {
    pPoseStack.pushPose()
    pPoseStack.translate(pPosX, pPosY, pPosZ)
    pPoseStack.scale(pScale, pScale, pScale)
    pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
    pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
    pPoseStack.mulPose(Axis.XP.rotationDegrees(-22.5f))
    var split: Float = pSplitY
    for(formattedCharSequence: FormattedCharSequence in componentSplit(pText, pLineWidth, pFont)) {
        pFont.drawInBatch(
            formattedCharSequence,
            0f,
            split,
            pColor,
            false,
            pPoseStack.last().pose(),
            pBuffer,
            Font.DisplayMode.NORMAL,
            pBackgroundColor,
            SCREEN_TINT
        )
        split += 9f
    }
    pPoseStack.popPose()
}

fun componentSplit(pText: Component, pMaxWidth: Int, pFont: Font): MutableList<FormattedCharSequence> =
    Language.getInstance().getVisualOrder(pFont.splitter.splitLines(pText, pMaxWidth, Style.EMPTY))