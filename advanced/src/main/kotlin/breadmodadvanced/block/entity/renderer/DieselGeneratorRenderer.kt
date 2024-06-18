package breadmodadvanced.block.entity.renderer

import breadmodadvanced.ModMainAdv
import breadmodadvanced.block.entity.DieselGeneratorBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraftforge.client.model.generators.ModelProvider

class DieselGeneratorRenderer: BlockEntityRenderer<DieselGeneratorBlockEntity> {
    private val doorModelLocation = ModMainAdv.modLocation("${ModelProvider.BLOCK_FOLDER}/diesel_generator/diesel_generator_door")

    override fun render(
        pBlockEntity: DieselGeneratorBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val instance = Minecraft.getInstance()
        val renderer = instance.blockRenderer.modelRenderer
        val doorModel = instance.modelManager.getModel(doorModelLocation)

        pPoseStack.pushPose()
        pPoseStack.translate(0.625, 0.063, 0.0)
        pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
        renderer.renderModel(
            pPoseStack.last(),
            pBuffer.getBuffer(RenderType.solid()),
            pBlockEntity.blockState,
            doorModel,
            1f,
            1f,
            1f,
            pPackedLight,
            pPackedOverlay
        )
        pPoseStack.popPose()
    }
}