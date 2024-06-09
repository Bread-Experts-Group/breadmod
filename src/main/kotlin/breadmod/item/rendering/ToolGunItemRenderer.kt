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

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
//        println("is working")
        val renderer = Minecraft.getInstance().itemRenderer
        val fontRenderer = Minecraft.getInstance().font
        val mainModel = Minecraft.getInstance().modelManager.getModel(mainModelLocation)
        val coilModel = Minecraft.getInstance().modelManager.getModel(coilModelLocation)

        pPoseStack.pushPose()
        renderModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        // todo decouple idle spinning from create's rotation logic
        // todo recoil and increased coil spin when using tool gun
        pPoseStack.mulPose(Axis.XN.rotationDegrees(ScrollValueHandler.getScroll(AnimationTickHolder.getPartialTicks())))
        renderModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        pPoseStack.popPose()
        pPoseStack.pushPose()
        // x, y, z after rotations
        // x: back and forward, y: up and down, z: left and right

        // big text
        pPoseStack.translate(0.923, 0.055, -0.031)
        pPoseStack.scale(0.005f, 0.005f, 0.005f)
        pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
        pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
        pPoseStack.mulPose(Axis.XP.rotationDegrees(-22.5f))
        fontRenderer.drawInBatch(
            "test",
            0f,
            0f,
            Color.WHITE.rgb,
            false,
            pPoseStack.last().pose(),
            pBuffer,
            Font.DisplayMode.NORMAL,
            Color(0,0,255,128).rgb,
            15728880
        )
        pPoseStack.popPose()

        // smol text
        pPoseStack.pushPose()
        pPoseStack.translate(0.925, 0.063, -0.035)
        pPoseStack.scale(0.0005f, 0.0005f, 0.0005f)
        pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
        pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
        pPoseStack.mulPose(Axis.XP.rotationDegrees(-22.5f))
        fontRenderer.drawInBatch(
            "funny text",
            0f,
            0f,
            Color.WHITE.rgb,
            false,
            pPoseStack.last().pose(),
            pBuffer,
            Font.DisplayMode.NORMAL,
            Color(0,0,255,128).rgb,
            15728880
        )
        pPoseStack.popPose()
    }

    private fun renderModel(
        pModel: BakedModel,
        pRenderer: ItemRenderer,
        pStack: ItemStack,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val glint = pStack.hasFoil()
        for(type in pModel.getRenderTypes(pStack, false)) {
            val helper: RenderType = RenderTypeHelper.getEntityRenderType(type, false)
            val consumer = ItemRenderer.getFoilBuffer(pBuffer, helper, true, glint)
            pRenderer.renderModelLists(pModel, pStack, pPackedLight, pPackedOverlay, pPoseStack, consumer)
        }
    }
}
