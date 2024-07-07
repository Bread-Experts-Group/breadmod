package breadmod.block.machine.entity.renderer

import breadmod.ModMain
import breadmod.block.machine.entity.ToasterBlockEntity
import breadmod.util.render.renderBlockModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.model.generators.ModelProvider

class ToasterRenderer: BlockEntityRenderer<ToasterBlockEntity> {
    private val handleModelLocation = ModMain.modLocation("${ModelProvider.BLOCK_FOLDER}/toaster/handle")
    private var triggeredOffset = 0.0

    override fun render(
        pBlockEntity: ToasterBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val instance = Minecraft.getInstance()
        val itemRenderer = instance.itemRenderer
        val handleModel = instance.modelManager.getModel(handleModelLocation)
        val blockRotation = pBlockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val triggered = pBlockEntity.blockState.getValue(BlockStateProperties.TRIGGERED)

        triggeredOffset = if(triggered) -0.13 else 0.0

        pPoseStack.pushPose()
        when(blockRotation) {
            Direction.SOUTH -> {
                pPoseStack.mulPose(Axis.YN.rotationDegrees(180f))
                pPoseStack.translate(-1.0, triggeredOffset, -1.0)
            }
            Direction.WEST -> {
                pPoseStack.translate(0.0, triggeredOffset, 1.0)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
            }
            Direction.EAST -> {
                pPoseStack.translate(1.0, triggeredOffset, 0.0)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
            }
            Direction.NORTH -> {
                pPoseStack.translate(0.0, triggeredOffset, 0.0)
            }
            else -> {}
        }

        renderBlockModel(pPoseStack, pBuffer, pBlockEntity, handleModel, pPackedLight, pPackedOverlay)
        pPoseStack.popPose()

        // todo set up 2 items in the up and down position depending on triggered state
        pPoseStack.pushPose()
        itemRenderer.renderStatic(
            pBlockEntity.getRenderStack(),
            ItemDisplayContext.FIXED,
            pPackedLight,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.level,
            1
        )
        pPoseStack.popPose()
    }
}