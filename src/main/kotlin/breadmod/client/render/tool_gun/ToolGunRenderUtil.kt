package breadmod.client.render.tool_gun

import breadmod.util.render.renderText
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence

private const val SCREEN_TINT = 15728880
/**
 * +X moves text forward on tool gun
 * -X moves text backward on tool gun
 *
 * +Z moves text right on tool gun
 * -Z moves text left on tool gun
 */

fun drawTextOnScreen(
    pComponent: Component,
    pColor: Int,
    pBackgroundColor: Int,
    pDropShadow: Boolean,
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
    renderText(pComponent, pColor, pBackgroundColor, pFontRenderer, pPoseStack, pBuffer, pDropShadow, SCREEN_TINT)
    pPoseStack.popPose()
}

/**
 * @see drawTextOnScreen
 */
fun drawTextOnScreen(
    pText: String,
    pColor: Int,
    pBackgroundColor: Int,
    pDropShadow: Boolean,
    pFontRenderer: Font,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pPosX: Double,
    pPosY: Double,
    pPosZ: Double,
    pScale: Float
) = drawTextOnScreen(
    Component.literal(pText),
    pColor, pBackgroundColor, pDropShadow, pFontRenderer, pPoseStack, pBuffer, pPosX, pPosY, pPosZ, pScale
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
    pDropShadow: Boolean,
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
            pDropShadow,
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

// Font.split() converted to take in a Component instead of a FormattedCharSequence
fun componentSplit(pText: Component, pMaxWidth: Int, pFont: Font): MutableList<FormattedCharSequence> =
    Language.getInstance().getVisualOrder(pFont.splitter.splitLines(pText, pMaxWidth, Style.EMPTY))