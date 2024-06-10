package breadmod.item.rendering

import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.RenderTypeHelper
import net.minecraftforge.client.model.generators.ModelProvider
import java.awt.Color


class ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {
    private val mainModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item")
    private val coilModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil")

    private val screenTint = 0xFFFFFF
    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val renderer = Minecraft.getInstance().itemRenderer
        val fontRenderer = Minecraft.getInstance().font
        val mainModel = Minecraft.getInstance().modelManager.getModel(mainModelLocation)
        val coilModel = Minecraft.getInstance().modelManager.getModel(coilModelLocation)

        pPoseStack.pushPose()
        renderModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay)
        // todo decouple idle spinning from create's rotation logic
        // todo recoil and increased coil spin when using tool gun
        pPoseStack.mulPose(Axis.XN.rotationDegrees(ScrollValueHandler.getScroll(AnimationTickHolder.getPartialTicks())))
        renderModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay)
        pPoseStack.popPose()
        // x, y, z after rotations
        // x: back and forward, y: up and down, z: left and right

        // big text
        drawTextOnScreen("☠☠", Color.BLACK.rgb, Color(0,0,0,0).rgb, fontRenderer, pPoseStack, pBuffer,
            0.9215, 0.0555, -0.028, 0.0035f, 0.0035f, 0.0035f
        )

        // smol text
        drawTextOnScreen("Mode: Remover", Color.WHITE.rgb, Color(0,0,0,0).rgb, fontRenderer, pPoseStack, pBuffer,
            0.923, 0.065, -0.038, 0.0007f, 0.0007f, 0.0007f
        )

        // bottom text
        drawTextOnScreen("WARNING: your fat", Color.RED.rgb, Color(0,0,0,0).rgb, fontRenderer, pPoseStack, pBuffer,
            0.9, 0.0175, -0.040, 0.0007f, 0.0007f, 0.0007f
        )

    }

    private fun renderText(pText: String, pColor: Int, pBackgroundColor: Int, pFontRenderer: Font, pPoseStack: PoseStack, pBuffer: MultiBufferSource) {
        pFontRenderer.drawInBatch(
            Component.literal(pText),
            0f,
            0f,
            pColor,
            false,
            pPoseStack.last().pose(),
            pBuffer,
            Font.DisplayMode.NORMAL,
            pBackgroundColor,
            16777215
        )
    }

    private fun drawTextOnScreen(
        pText: String,
        pColor: Int,
        pBackgroundColor: Int,
        pFontRenderer: Font,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPosX: Double,
        pPosY: Double,
        pPosZ: Double,
        pScaleX: Float,
        pScaleY: Float,
        pScaleZ: Float
    ) {
        pPoseStack.pushPose()
        pPoseStack.translate(pPosX, pPosY, pPosZ)
        pPoseStack.scale(pScaleX, pScaleY, pScaleZ)
        pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
        pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
        pPoseStack.mulPose(Axis.XP.rotationDegrees(-22.5f))
        renderText(pText, pColor, pBackgroundColor, pFontRenderer, pPoseStack, pBuffer)
        pPoseStack.popPose()
    }

    private fun renderModel(
        pModel: BakedModel,
        pRenderer: ItemRenderer,
        pStack: ItemStack,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedOverlay: Int
    ) {
        val glint = pStack.hasFoil()
        for(type in pModel.getRenderTypes(pStack, false)) {
            val helper: RenderType = RenderTypeHelper.getEntityRenderType(type, false)
            val consumer = ItemRenderer.getFoilBuffer(pBuffer, helper, true, glint)
            pRenderer.renderModelLists(pModel, pStack, screenTint, pPackedOverlay, pPoseStack, consumer)
        }
    }
}
