package org.bread_experts_group.breadmod.client.tool_gun.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.ModelManager
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderText
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.joml.Vector3f
import org.joml.Vector4f

class ToolGunRenderHelper(
	val modelManager: ModelManager = localClient.modelManager,
	val itemRenderer: ItemRenderer = localClient.itemRenderer,
	val blockModelRenderer: ModelBlockRenderer = localClient.blockRenderer.modelRenderer,
//	val blockRenderDispatcher: BlockRenderDispatcher = localClient.blockRenderer,
//	val entityRenderDispatcher: EntityRenderDispatcher = localClient.entityRenderDispatcher,
	val font: Font = localClient.font
) {
	/**
	 * Default screen brightness.
	 */
	val screenTint: Int = 15728880
	var shouldRecoil: Boolean = true
	var shouldRenderCoil: Boolean = true
	var shouldRenderMainBody: Boolean = true
	var shouldRenderScreenContents: Boolean = true

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

	fun renderScreenBackground(
		texture: ResourceLocation,
		textureWidth: Int,
		textureHeight: Int,
		poseStack: PoseStack,
		buffer: MultiBufferSource
	) {
		this.initialScreenTranslations(poseStack, -0.0357, 0.3545, 0.8319, 0.07f)
		poseStack.scale(1.025f, 0.856f, 1f)
		drawQuad(
			poseStack,
			buffer,
			RenderType.text(texture),
			Vector4f(1f, 1f, 1f, 1f),
			Vector3f(1f, 0f, 0f),
			Vector3f(0f, 0f, 0f),
			Vector3f(1f, -1f, 0f),
			Vector3f(0f, -1f, 0f),
			textureWidth.toFloat(),
			textureWidth.toFloat(),
			textureHeight.toFloat(),
			textureHeight.toFloat(),
			this.screenTint
		)
		poseStack.popPose()
	}

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
		posZ: Double = 0.8317,
		scale: Float = 0.0007f
	) {
		this.initialScreenTranslations(poseStack, posX, posY, posZ, scale)
		renderText(
			component.visualOrderText, color, backgroundColor, fontRenderer,
			poseStack, buffer,
			dropShadow, this.screenTint
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
		posZ: Double = 0.8317,
		scale: Float = 0.0007f
	): Unit = this.drawTextOnScreen(
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
}