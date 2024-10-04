package bread.mod.breadmod.client.render.block

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.block.entity.ToasterBlockEntity
import bread.mod.breadmod.util.render.renderBlockModel
import bread.mod.breadmod.util.render.renderStaticItem
import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.architectury.platform.Platform
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class ToasterRenderer(
    val context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<ToasterBlockEntity> {
    private val platformId = if (Platform.isFabric()) "fabric_resource" else "standalone"

    private val mainModelLocation = ModelResourceLocation(modLocation("block/toaster/handle"), platformId)
    private var triggeredOffset = 0.0

    override fun render(
        blockEntity: ToasterBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val handleModel = rgMinecraft.modelManager.getModel(mainModelLocation)
        val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val triggered = blockEntity.blockState.getValue(BlockStateProperties.TRIGGERED)

        triggeredOffset = if (triggered) -0.13 else 0.0

        poseStack.pushPose()
        when (blockRotation) {
            Direction.SOUTH -> {
                poseStack.mulPose(Axis.YN.rotationDegrees(180f))
                poseStack.translate(-1.0, triggeredOffset, -1.0)
            }

            Direction.WEST -> {
                poseStack.translate(0.0, triggeredOffset, 1.0)
                poseStack.mulPose(Axis.YN.rotationDegrees(-90f))
            }

            Direction.EAST -> {
                poseStack.translate(1.0, triggeredOffset, 0.0)
                poseStack.mulPose(Axis.YN.rotationDegrees(90f))
            }

            Direction.NORTH -> {
                poseStack.translate(0.0, triggeredOffset, 0.0)
            }

            else -> {}
        }

        renderBlockModel(poseStack, bufferSource, blockEntity, handleModel, packedLight, packedOverlay)
        poseStack.popPose()
        val itemStack = blockEntity.getRenderStack()

        poseStack.pushPose()
        poseStack.translate(0.5, 0.3, 0.61)
        poseStack.scale(0.6f, 0.6f, 0.6f)
        if (blockRotation == Direction.SOUTH || blockRotation == Direction.NORTH) {
            poseStack.mulPose(Axis.YN.rotationDegrees(90f))
            poseStack.translate(-0.185, 0.0, 0.185)
        }
        if (!triggered) {
            if (itemStack.count == 2) {
                renderStaticItem(itemStack, poseStack, bufferSource, blockEntity, packedLight)
                poseStack.translate(0.0, 0.0, -0.37)
                renderStaticItem(itemStack, poseStack, bufferSource, blockEntity, packedLight)
            } else {
                renderStaticItem(itemStack, poseStack, bufferSource, blockEntity, packedLight)
            }
        }
        poseStack.popPose()
    }
}