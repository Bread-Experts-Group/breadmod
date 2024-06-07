package breadmod.item.rendering

import breadmod.ModMain.modLocation
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.RenderTypeHelper
import net.minecraftforge.client.model.BakedModelWrapper
import net.minecraftforge.client.model.generators.ModelProvider

@OnlyIn(Dist.CLIENT)
class ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
    Minecraft.getInstance().blockEntityRenderDispatcher,
    Minecraft.getInstance().entityModels
) {
    private val mainModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/tool_gun/item")
    private val coilModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/tool_gun/coil")

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        println("it worked")
        val renderer = Minecraft.getInstance().itemRenderer
        val modelManager = Minecraft.getInstance().modelManager
        val mainModel = modelManager.getModel(mainModelLocation)
        val coilModel = modelManager.getModel(coilModelLocation)
        pPoseStack.pushPose()

        renderModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        pPoseStack.translate(0.5, 0.0, 0.0)
        renderModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)

//        renderer.render(pStack, pDisplayContext, isLeftHand(pDisplayContext), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, mainModel)
//        pPoseStack.mulPose(Axis.XN.rotationDegrees(-45F))
//        renderer.render(pStack, pDisplayContext, isLeftHand(pDisplayContext), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, coilModel)

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

    private fun isLeftHand(type: ItemDisplayContext): Boolean {
        return type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
    }
}
