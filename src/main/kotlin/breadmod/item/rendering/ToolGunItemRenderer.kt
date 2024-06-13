package breadmod.item.rendering

import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.ToolGunItem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.minecraft.ChatFormatting
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
import java.security.SecureRandom
import kotlin.math.round


class ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
    minecraft.blockEntityRenderDispatcher,
    minecraft.entityModels
) {
    private companion object {
        const val SCREEN_TINT = 0xFFFFFF

        val minecraft: Minecraft = Minecraft.getInstance()
        val secureRandom = SecureRandom()
    }

    private val mainModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item")
    private val coilModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil")
    private val testModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/textureplane_test")

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val toolgunItem = pStack.item as ToolGunItem
        val renderer = Minecraft.getInstance().itemRenderer
        val fontRenderer = Minecraft.getInstance().font
        val modelManager = Minecraft.getInstance().modelManager
        val mainModel = modelManager.getModel(mainModelLocation)
        val coilModel = modelManager.getModel(coilModelLocation)
        val testModel = modelManager.getModel(testModelLocation)

        pPoseStack.pushPose()
        renderModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        renderModel(testModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        // todo smooth rotation after firing toolgun, quickly tapering off
        // todo recoil and increased coil spin when using tool gun

        pPoseStack.mulPose(Axis.XN.rotationDegrees(ScrollValueHandler.getScroll(AnimationTickHolder.getPartialTicks()) * 100))

        renderModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        pPoseStack.popPose()
        // x, y, z after rotations
        // x: back and forward, y: up and down, z: left and right

        // big text
        val byteArray = ByteArray(2)
        secureRandom.nextBytes(byteArray)
        drawTextOnScreen(byteArray.decodeToString(), Color.BLACK.rgb, Color(25,25,25,0).rgb, fontRenderer, pPoseStack, pBuffer,
            0.9215, 0.0555, -0.028, 0.0035f
        )

        drawTextOnScreen(
            (toolgunItem.getCurrentMode(pStack)?.displayName?.copy() ?: Component.literal("???")).withStyle(ChatFormatting.BOLD),
            Color.WHITE.rgb, Color(0,0,0,0).rgb, fontRenderer, pPoseStack, pBuffer,
            0.923, 0.065, -0.038, 0.0007f
        )

        drawTextOnScreen("CASEOH: ${round(secureRandom.nextDouble() * 5000).toUInt()}lbs", Color.RED.rgb, Color(0,0,0,0).rgb, fontRenderer, pPoseStack, pBuffer,
            0.9, 0.0175, -0.040, 0.0007f
        )

    }

    private fun renderText(
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
        pScale: Float
    ) = drawTextOnScreen(
        Component.literal(pText),
        pColor, pBackgroundColor, pFontRenderer, pPoseStack, pBuffer, pPosX, pPosY, pPosZ, pScale
    )

    private fun drawTextOnScreen(
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

    private fun renderModel(
        pModel: BakedModel,
        pRenderer: ItemRenderer,
        pStack: ItemStack,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedOverlay: Int,
        pPackedLight: Int
    ) {
        val glint = pStack.hasFoil()
        for(type in pModel.getRenderTypes(pStack, false)) {
            val helper: RenderType = RenderTypeHelper.getEntityRenderType(type, false)
            val consumer = ItemRenderer.getFoilBuffer(pBuffer, helper, true, glint)
            pRenderer.renderModelLists(pModel, pStack, pPackedLight, pPackedOverlay, pPoseStack, consumer)
        }
    }
}
