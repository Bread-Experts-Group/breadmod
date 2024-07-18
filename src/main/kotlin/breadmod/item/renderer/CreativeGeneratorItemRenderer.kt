package breadmod.item.renderer

import breadmod.ModMain
import breadmod.util.render.renderItemModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider

class CreativeGeneratorItemRenderer : BlockEntityWithoutLevelRenderer(
    minecraft.blockEntityRenderDispatcher,
    minecraft.entityModels
) {
    private companion object {
        val minecraft: Minecraft = Minecraft.getInstance()
    }

    private val mainModelLocation = ModMain.modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator")
    private val starModelLocation = ModMain.modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star")

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val instance = Minecraft.getInstance()
        val renderer = instance.itemRenderer
        val modelManager = instance.modelManager
        val level = instance.level ?: return

        val mainModel = modelManager.getModel(mainModelLocation)
        val starModel = modelManager.getModel(starModelLocation)

        renderItemModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        pPoseStack.translate(0.5, 0.5, 0.5)
        pPoseStack.mulPose(Axis.YN.rotationDegrees(Math.floorMod(level.gameTime, 360).toFloat() + instance.partialTick))
        pPoseStack.mulPose(Axis.XN.rotationDegrees(Math.floorMod(level.gameTime, 360).toFloat() + instance.partialTick))
        pPoseStack.scale(0.95f, 0.95f, 0.95f)
        renderItemModel(starModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
    }
}