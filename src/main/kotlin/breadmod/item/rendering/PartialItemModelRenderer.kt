package breadmod.item.rendering

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

/*
* Code credit goes to the Create team
* https://github.com/Creators-of-Create/Create
*/

class PartialItemModelRenderer(
    private val pStack: ItemStack,
    private val pTransformType: ItemDisplayContext,
    private val pPoseStack: PoseStack,
    private val pBufferSource: MultiBufferSource,
    private val pOverlay: Int
) {
    private val random = RandomSource.create()

    fun render(pModel: BakedModel, pLight: Int) { render(pModel, RenderTypes)}
}