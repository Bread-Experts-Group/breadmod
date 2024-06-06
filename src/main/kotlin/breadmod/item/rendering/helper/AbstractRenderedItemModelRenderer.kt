package breadmod.item.rendering.helper

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.BakedModelWrapper

abstract class AbstractRenderedItemModelRenderer : BlockEntityWithoutLevelRenderer(null, null) {

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val mainModel: CustomRenderedItemModel = Minecraft.getInstance().itemRenderer.getModel(pStack, null, null, 0) as CustomRenderedItemModel
        val renderer: PartialItemModelRenderer =
            PartialItemModelRenderer.of(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedOverlay)

        pPoseStack.pushPose()
        pPoseStack.translate(0.5F, 0.5F, 0.5F)
        render(pStack, mainModel, renderer, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        pPoseStack.popPose()
    }

    abstract fun render(
        pStack: ItemStack,
        pModel: CustomRenderedItemModel,
        pRenderer: PartialItemModelRenderer,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    )

    class CustomRenderedItemModel(originalModel: BakedModel): BakedModelWrapper<BakedModel>(originalModel) {
        override fun isCustomRenderer(): Boolean = true
        override fun applyTransform(
            cameraTransformType: ItemDisplayContext,
            poseStack: PoseStack,
            applyLeftHandTransform: Boolean
        ): BakedModel {
            super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform)
            return this
        }
        fun getOriginalModel(): BakedModel = originalModel
    }
}