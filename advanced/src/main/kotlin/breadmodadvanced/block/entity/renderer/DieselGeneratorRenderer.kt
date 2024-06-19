package breadmodadvanced.block.entity.renderer

import breadmodadvanced.ModMainAdv
import breadmodadvanced.block.entity.DieselGeneratorBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
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
        val renderer = instance.blockRenderer
        val doorModel = instance.modelManager.getModel(doorModelLocation)
        val blockRotation = pBlockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        pPoseStack.pushPose()
        when(blockRotation) { // TODO Set up rotations and translations for door, upgrade cards, and fluid, set up functions for transforming separate models
            Direction.NORTH -> {
                println("facing north")
                pPoseStack.translate(0.625, 0.063, 0.0)
                // todo set up toggle property in block to.. toggle the door being open or closed
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            Direction.SOUTH -> {
                println("facing south")
                pPoseStack.translate(0.375, 0.0625, 1.0)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(180f))
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            Direction.WEST -> {
                println("facing west")
                pPoseStack.translate(0.0, 0.0628, 0.375)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            Direction.EAST -> {
                println("facing east")
                pPoseStack.translate(1.0, 0.0625, 0.625)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            else -> println("facing.. nowhere??")
        }
//        pPoseStack.rotateAround(blockRotation, 0.0f, 0.0f, 0.0f)

        renderModel(pPoseStack, pBuffer, pBlockEntity, doorModel, pPackedLight, pPackedOverlay)
        pPoseStack.popPose()

    }

    private fun renderModel(
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pBlockEntity: BlockEntity,
        pModel: BakedModel,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        Minecraft.getInstance().blockRenderer.modelRenderer.renderModel(
            pPoseStack.last(),
            pBuffer.getBuffer(RenderType.solid()),
            pBlockEntity.blockState,
            pModel,
            1f,
            1f,
            1f,
            pPackedLight,
            pPackedOverlay,
            pBlockEntity.modelData,
            RenderType.solid()
        )
    }
}