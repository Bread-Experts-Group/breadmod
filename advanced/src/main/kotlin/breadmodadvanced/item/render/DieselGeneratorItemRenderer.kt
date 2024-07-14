package breadmodadvanced.item.render

import breadmod.util.render.renderItemModel
import breadmodadvanced.ModMainAdv
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider

class DieselGeneratorItemRenderer: BlockEntityWithoutLevelRenderer(
    minecraft.blockEntityRenderDispatcher,
    minecraft.entityModels
) {
    private companion object {
        val minecraft: Minecraft = Minecraft.getInstance()
    }

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
        val instance = Minecraft.getInstance()
        val modelManager = instance.modelManager
        val renderer = instance.itemRenderer

        val mainModel = modelManager.getModel(mainModelLocation)
        val doorModel = modelManager.getModel(doorModelLocation)

        renderItemModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        pPoseStack.translate(0.625, 0.063, 0.0)
        renderItemModel(doorModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
    }
}