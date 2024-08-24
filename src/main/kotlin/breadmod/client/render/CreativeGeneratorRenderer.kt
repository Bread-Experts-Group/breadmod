package breadmod.client.render

import breadmod.ModMain.modLocation
import breadmod.block.entity.machine.CreativeGeneratorBlockEntity
import breadmod.util.render.renderBlockModel
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraftforge.client.model.generators.ModelProvider

class CreativeGeneratorRenderer: BlockEntityRenderer<CreativeGeneratorBlockEntity> {
    private val starModelLocation = modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star")
    private val aLocation = modLocation("${ModelProvider.BLOCK_FOLDER}/sphere")

    override fun render(
        pBlockEntity: CreativeGeneratorBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val starModel = rgMinecraft.modelManager.getModel(starModelLocation)
        val level = pBlockEntity.level ?: return

//        val builder = pBuffer.getBuffer(ClientModEventBus.ModRenderTypes.BLOOM.apply(modLocation("shaders/white.png")))
        pPoseStack.pushPose()
        pPoseStack.translate(0.5f, 2f, 0.5f)
//        pPoseStack.translate(0.5f, 0f, 0.5f)
        pPoseStack.mulPose(Axis.YN.rotationDegrees((Math.floorMod(level.gameTime, 360).toFloat() + pPartialTick)))
//        pPoseStack.translate(-0.5f, 0f, -0.5f)
//        pPoseStack.mulPose(Axis.XN.rotationDegrees((Math.floorMod(level.gameTime, 360).toFloat() + pPartialTick)))
//        pPoseStack.translate(0.5f, 0f, 0.5f)

//        drawQuad(builder, pPoseStack, 0f, 1f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, pPackedLight, pPackedOverlay)
//        drawQuad(builder, pPoseStack, 0f, 0f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, pPackedLight, pPackedOverlay)
//        drawQuad(builder, pPoseStack, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, pPackedLight, pPackedOverlay)
//        drawQuad(builder, pPoseStack, 1f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, pPackedLight, pPackedOverlay)
//        pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
//        pPoseStack.translate(0f, 0f, -1f)
//        drawQuad(builder, pPoseStack, 1f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, pPackedLight, pPackedOverlay)
//        pPoseStack.mulPose(Axis.YN.rotationDegrees(180f))
//        pPoseStack.translate(-1f, 0f, -1f)
//        drawQuad(builder, pPoseStack, 1f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, pPackedLight, pPackedOverlay)
        renderBlockModel(
            pPoseStack, pBuffer, pBlockEntity, rgMinecraft.modelManager.getModel(aLocation), pPackedLight, pPackedOverlay,
           /* ClientModEventBus.ModRenderTypes.BLOOM.apply(modLocation("shaders/white.png"))*/
        )
        pPoseStack.popPose()

        pPoseStack.translate(0.5, 0.5, 0.5)
        pPoseStack.mulPose(Axis.YN.rotationDegrees((Math.floorMod(level.gameTime, 360).toFloat() + pPartialTick)))
        pPoseStack.mulPose(Axis.XN.rotationDegrees((Math.floorMod(level.gameTime, 360).toFloat() + pPartialTick)))
        pPoseStack.scale(0.95f, 0.95f, 0.95f)

//        renderBlockModel(
//            pPoseStack, pBuffer, pBlockEntity,
//            starModel, pPackedLight, pPackedOverlay,
//            ClientModEventBus.ModRenderTypes.BLOOM.apply(modLocation("shaders/white.png"))
//        )
    }
}