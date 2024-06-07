package breadmod.item.rendering

import breadmod.ModMain.modLocation
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler
import com.simibubi.create.foundation.utility.AnimationTickHolder
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
//        println("is working")
        val renderer = Minecraft.getInstance().itemRenderer
        val mainModel = Minecraft.getInstance().modelManager.getModel(mainModelLocation)
        val coilModel = Minecraft.getInstance().modelManager.getModel(coilModelLocation)
        pPoseStack.pushPose()
        renderModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        // todo decouple idle spinning from create's rotation logic
        // todo recoil and increased coil spin when using tool gun
        pPoseStack.mulPose(Axis.XN.rotationDegrees(ScrollValueHandler.getScroll(AnimationTickHolder.getPartialTicks())))

        renderModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
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
