package org.bread_experts_group.breadmod.client.render.item

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.client.render.tool_gun.ToolGunAnimationHandler
import org.bread_experts_group.breadmod.client.render.tool_gun.drawTextOnScreen
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.modelLocation
import org.bread_experts_group.breadmod.client.render.renderBlockModel
import org.bread_experts_group.breadmod.client.render.renderItemModel
import org.bread_experts_group.breadmod.client.render.scaleFlat
import java.awt.Color

class ToolGunItemRenderer :
	BlockEntityWithoutLevelRenderer(localClient.blockEntityRenderDispatcher, localClient.entityModels) {
	private companion object {
		val mainModelLocation = modelLocation("item/$TOOL_GUN_DEF/item")
		val coilModelLocation = modelLocation("item/$TOOL_GUN_DEF/coil")
		val altModelLocation = modelLocation("item/$TOOL_GUN_DEF/alt/tool_gun_alt")
		val useAltModel = ModConfiguration.CLIENT.useAlternateToolGunModel
	}

	override fun renderByItem(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
//    val toolGunItem = stack.item as ToolGunItem
//      val toolGunMode = toolGunItem.getCurrentMode(stack)
		val modelManager = localClient.modelManager
		val itemRenderer = localClient.itemRenderer
		val blockModelRenderer = localClient.blockRenderer.modelRenderer
		val font = localClient.font
		val mainModel = modelManager.getModel(Companion.mainModelLocation)
		val coilModel = modelManager.getModel(Companion.coilModelLocation)
		val altModel = modelManager.getModel(Companion.altModelLocation)
		val animHandler = ToolGunAnimationHandler
		val rotation = animHandler.coilRotation
		val recoil = animHandler.recoil
		fun rotateCoilAndRender() {
			poseStack.mulPose(Axis.XN.rotationDegrees(rotation))
			itemRenderer.renderItemModel(
				coilModel,
				stack,
				displayContext,
				false,
				poseStack,
				buffer,
				packedOverlay,
				packedLight,
				true
			)
		}

		val millis = Util.getMillis()

		poseStack.pushPose()
		poseStack.translate(1.1 - recoil, 0.06, -0.05)
		poseStack.scaleFlat(0.08f)
		poseStack.translate(0.5, 0.0, 0.5)
		poseStack.mulPose(Axis.YP.rotationDegrees((millis.toFloat() / 50f) % 360f))
		poseStack.translate(-0.5, 0.0, -0.5)
		blockModelRenderer.renderBlockModel(
			poseStack.last(),
			buffer,
			Blocks.HAY_BLOCK.defaultBlockState(),
			packedLight,
			packedOverlay
		)
		poseStack.popPose()
		// todo proper recoil
		animHandler.clientTick()

		if (displayContext.firstPerson()) {
			poseStack.translate(-recoil, 0.0f, 0.0f)
			if (Companion.useAltModel.get()) {
				itemRenderer.renderItemModel(
					altModel,
					stack,
					displayContext,
					false,
					poseStack,
					buffer,
					packedOverlay,
					packedLight,
					true
				)
			} else {
				itemRenderer.renderItemModel(
					mainModel,
					stack,
					displayContext,
					false,
					poseStack,
					buffer,
					packedOverlay,
					packedLight,
					true
				)
				drawTextOnScreen(
					Component.literal("THE FUNNY"),
					Color.WHITE.rgb, Color(0, 0, 0, 0).rgb, false, font, poseStack, buffer,
					0.923, 0.065, -0.038, 0.0007f
				)

				drawTextOnScreen(
					"CASEOH: Not Available",
					Color.RED.rgb,
					Color(0, 0, 0, 0).rgb,
					false,
					font,
					poseStack,
					buffer,
					0.9,
					0.0175,
					-0.040,
					0.0007f
				)
				rotateCoilAndRender()
			}
		} else {
			if (Companion.useAltModel.get()) {
				itemRenderer.renderItemModel(
					altModel,
					stack,
					displayContext,
					false,
					poseStack,
					buffer,
					packedOverlay,
					packedLight,
					true
				)
			} else {
				itemRenderer.renderItemModel(
					mainModel,
					stack,
					displayContext,
					false,
					poseStack,
					buffer,
					packedOverlay,
					packedLight,
					true
				)
				rotateCoilAndRender()
			}
		}
	}
}