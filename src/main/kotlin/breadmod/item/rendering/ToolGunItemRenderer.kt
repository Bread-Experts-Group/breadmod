package breadmod.item.rendering

import breadmod.ModMain.modLocation
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.RenderTypeHelper
import net.minecraftforge.client.model.generators.ModelProvider

class ToolGunItemRenderer() : BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {
    private val mainModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/tool_gun_model")
    private val coilModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/tool_gun_coil_model")

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val renderer = Minecraft.getInstance().itemRenderer
        val modelManager = Minecraft.getInstance().modelManager
        val mainModel = modelManager.getModel(mainModelLocation)
        val coilModel = modelManager.getModel(coilModelLocation)

        pPoseStack.pushPose()
        mainModel.applyTransform(pDisplayContext, pPoseStack, isLeftHand(pDisplayContext))
        coilModel.applyTransform(pDisplayContext, pPoseStack, isLeftHand(pDisplayContext))

        renderModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        renderModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)

        // todo proper coil rotating, gun recoiling back after firing
        // refer to WrenchItemRenderer and WorldshaperItemRenderer for insight
        // ..also it's custom BEWLRs

        // These calls here actually have the coil rotate and render in place properly, but the entire model is offset in the wrong position
        /*
        renderer.render(pStack, pDisplayContext, isLeftHand(pDisplayContext), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, mainModel)
        pPoseStack.mulPose(Axis.XN.rotationDegrees(-45F))
        renderer.render(pStack, pDisplayContext, isLeftHand(pDisplayContext), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, coilModel)
        */

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
        for(type in pModel.getRenderTypes(pStack, true)) {
            val helper: RenderType = RenderTypeHelper.getEntityRenderType(type, true)
            val consumer = ItemRenderer.getFoilBuffer(pBuffer, helper, true, glint)
            pRenderer.renderModelLists(pModel, pStack, pPackedLight, pPackedOverlay, pPoseStack, consumer)
        }
    }

    private fun isLeftHand(type: ItemDisplayContext): Boolean {
        return type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
    }
}
