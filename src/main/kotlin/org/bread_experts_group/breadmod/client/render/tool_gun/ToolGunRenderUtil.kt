package org.bread_experts_group.breadmod.client.render.tool_gun

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.renderText
import org.bread_experts_group.breadmod.client.render.scaleFlat

private const val SCREEN_TINT = 15728880
private fun initialScreenTranslations(
	poseStack: PoseStack,
	posX: Double,
	posY: Double,
	posZ: Double,
	scale: Float
) {
	poseStack.pushPose()
	poseStack.mulPose(Axis.XP.rotationDegrees(90f))
	poseStack.mulPose(Axis.YP.rotationDegrees(90f))
	poseStack.mulPose(Axis.ZP.rotationDegrees(90f))
	poseStack.mulPose(Axis.XN.rotationDegrees(22.5f))
	poseStack.translate(posX, -posY, posZ)
	poseStack.scaleFlat(scale)
}

/**
 * +X moves text forward on tool gun
 * -X moves text backward on tool gun
 *
 * +Z moves text right on tool gun
 * -Z moves text left on tool gun
 */
fun drawTextOnScreen(
	component: Component,
	color: Int,
	backgroundColor: Int,
	dropShadow: Boolean,
	fontRenderer: Font,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	posX: Double = -0.0434,
	posY: Double = 0.4215,
	posZ: Double = 0.8319,
	scale: Float = 0.0007f
) {
	initialScreenTranslations(poseStack, posX, posY, posZ, scale)
	renderText(
		component.visualOrderText, color, backgroundColor, fontRenderer,
		poseStack, buffer,
		dropShadow, SCREEN_TINT
	)
	poseStack.popPose()
}

/**
 * @see drawTextOnScreen
 */
fun drawTextOnScreen(
	text: String,
	color: Int,
	backgroundColor: Int,
	dropShadow: Boolean,
	fontRenderer: Font,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	posX: Double = -0.0434,
	posY: Double = 0.4215,
	posZ: Double = 0.8319,
	scale: Float = 0.0007f
): Unit = drawTextOnScreen(
	Component.literal(text),
	color, backgroundColor, dropShadow, fontRenderer, poseStack, buffer, posX, posY, posZ, scale
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