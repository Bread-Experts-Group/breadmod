package breadmodadvanced.item.render

import breadmod.util.render.renderItemModel
import breadmod.util.render.rgMinecraft
import breadmodadvanced.ModMainAdv
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider

class DieselGeneratorItemRenderer: BlockEntityWithoutLevelRenderer(
    rgMinecraft.blockEntityRenderDispatcher,
    rgMinecraft.entityModels
) {
    private val mainModelLocation = ModMainAdv.modLocation("${ModelProvider.BLOCK_FOLDER}/diesel_generator")
    private val doorModelLocation = ModMainAdv.modLocation("${ModelProvider.BLOCK_FOLDER}/diesel_generator/diesel_generator_door")

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val modelManager = rgMinecraft.modelManager
        val renderer = rgMinecraft.itemRenderer

        val mainModel = modelManager.getModel(mainModelLocation)
        val doorModel = modelManager.getModel(doorModelLocation)

        renderItemModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        pPoseStack.translate(0.625, 0.063, 0.0)
        renderItemModel(doorModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
    }
}