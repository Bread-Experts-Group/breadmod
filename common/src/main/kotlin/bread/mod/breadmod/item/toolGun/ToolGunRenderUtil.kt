package bread.mod.breadmod.item.toolGun

import bread.mod.breadmod.util.render.renderText
import bread.mod.breadmod.util.render.scaleFlat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component

private const val SCREEN_TINT = 15728880

private fun initialTranslations(pPoseStack: PoseStack, pPosX: Double, pPosY: Double, pPosZ: Double, pScale: Float) {
    pPoseStack.pushPose()
    pPoseStack.translate(pPosX, pPosY, pPosZ)
    pPoseStack.scaleFlat(pScale)
    pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
    pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
    pPoseStack.mulPose(Axis.XP.rotationDegrees(-22.5f))
}

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
    initialTranslations(pPoseStack, pPosX, pPosY, pPosZ, pScale)
    renderText(
        pComponent.visualOrderText, pColor, pBackgroundColor, pFontRenderer,
        pPoseStack, pBuffer,
        pDropShadow, SCREEN_TINT
    )
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
): Unit = drawTextOnScreen(
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

// --Commented out by Inspection START (9/10/2024 03:55):
//fun drawWrappedTextOnScreen(
//    font: Font,
//    text: Component,
//    poseStack: PoseStack,
//    buffer: MultiBufferSource,
//    color: Int,
//    backgroundColor: Int,
//    dropShadow: Boolean,
//    posX: Double,
//    posY: Double,
//    posZ: Double,
//    splitY: Float,
//    scale: Float,
//    lineWidth: Int
//) {
//    initialTranslations(poseStack, posX, posY, posZ, scale)
//    var split: Float = splitY
//    for (formattedCharSequence: FormattedCharSequence in componentSplit(text, lineWidth, font)) {
//        font.drawInBatch(
//            formattedCharSequence,
//            0f,
//            split,
//            color,
//            dropShadow,
//            poseStack.last().pose(),
//            buffer,
//            Font.DisplayMode.NORMAL,
//            backgroundColor,
//            SCREEN_TINT
//        )
//        split += 9f
//    }
//    poseStack.popPose()
//}
// --Commented out by Inspection STOP (9/10/2024 03:55)

