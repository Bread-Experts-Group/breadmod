package breadmod.block.machine.entity.renderer

import breadmod.ModMain
import breadmod.block.machine.entity.CreativeGeneratorBlockEntity
import breadmod.util.render.renderBlockModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraftforge.client.model.generators.ModelProvider

class CreativeGeneratorRenderer: BlockEntityRenderer<CreativeGeneratorBlockEntity> {
    private val starModelLocation = ModMain.modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star")
//    val angle = 0f

    override fun render(
        pBlockEntity: CreativeGeneratorBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val instance = Minecraft.getInstance()
        val starModel = instance.modelManager.getModel(starModelLocation)
        val level = pBlockEntity.level ?: return

        pPoseStack.translate(0.5, 0.5, 0.5)
        pPoseStack.mulPose(Axis.YN.rotationDegrees((Math.floorMod(level.gameTime, 360).toFloat() + pPartialTick)))
        pPoseStack.mulPose(Axis.XN.rotationDegrees((Math.floorMod(level.gameTime, 360).toFloat() + pPartialTick)))
        pPoseStack.scale(0.95f, 0.95f, 0.95f)
        renderBlockModel(pPoseStack, pBuffer, pBlockEntity, starModel, pPackedLight, pPackedOverlay)
    }
}