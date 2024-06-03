package breadmod.item.rendering

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraftforge.client.model.BakedModelWrapper

/*
* Code credit goes to the Create team
* https://github.com/Creators-of-Create/Create
*/

class CustomRenderedItemModel(originalModel: BakedModel) : BakedModelWrapper<BakedModel>(originalModel) {
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