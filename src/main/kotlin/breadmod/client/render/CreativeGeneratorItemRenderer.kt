package breadmod.client.render

import breadmod.ModMain.modLocation
import breadmod.util.render.renderItemModel
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider

class CreativeGeneratorItemRenderer : BlockEntityWithoutLevelRenderer(
    rgMinecraft.blockEntityRenderDispatcher,
    rgMinecraft.entityModels
) {
    private val mainModelLocation = modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator")
    private val starModelLocation =
        modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star")

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val level = rgMinecraft.level ?: return
        val renderer = rgMinecraft.itemRenderer
        val modelManager = rgMinecraft.modelManager

        val mainModel = modelManager.getModel(mainModelLocation)
        val starModel = modelManager.getModel(starModelLocation)

        renderItemModel(
            mainModel,
            renderer,
            pStack,
            pPoseStack,
            pBuffer,
            pPackedOverlay,
            pPackedLight,
            RenderType.cutout()
        )
        pPoseStack.translate(0.5, 0.5, 0.5)
        pPoseStack.mulPose(
            Axis.YN.rotationDegrees(
                Math.floorMod(level.gameTime, 360).toFloat() + rgMinecraft.partialTick
            )
        )
        pPoseStack.mulPose(
            Axis.XN.rotationDegrees(
                Math.floorMod(level.gameTime, 360).toFloat() + rgMinecraft.partialTick
            )
        )
        pPoseStack.scale(0.95f, 0.95f, 0.95f)
        renderItemModel(starModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
    }
}