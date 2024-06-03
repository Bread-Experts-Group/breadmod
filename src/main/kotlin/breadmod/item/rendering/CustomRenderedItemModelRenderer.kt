package breadmod.item.rendering

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

/*
* Code credit goes to the Create team
* https://github.com/Creators-of-Create/Create
*/

class CustomRenderedItemModelRenderer(
    pBlockEntityRenderDispatcher: BlockEntityRenderDispatcher,
    pEntityModelSet: EntityModelSet
) : BlockEntityWithoutLevelRenderer(pBlockEntityRenderDispatcher, pEntityModelSet) {
    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        super.renderByItem(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
    }
}