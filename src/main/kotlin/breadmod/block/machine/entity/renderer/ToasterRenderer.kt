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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
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
        val itemStack = pBlockEntity.getRenderStack()

        pPoseStack.pushPose()
        pPoseStack.translate(0.5, 0.3, 0.61)
        pPoseStack.scale(0.6f, 0.6f, 0.6f)
        if(blockRotation == Direction.SOUTH || blockRotation == Direction.NORTH) {
            pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
            pPoseStack.translate(-0.185, 0.0, 0.185)
        }
        if(!triggered) {
            if(itemStack.count == 2) {
                renderStaticItem(itemStack, pPoseStack, pBuffer, pBlockEntity, pPackedLight)
                pPoseStack.translate(0.0, 0.0, -0.37)
                renderStaticItem(itemStack, pPoseStack, pBuffer, pBlockEntity, pPackedLight)
            } else {
                renderStaticItem(itemStack, pPoseStack, pBuffer, pBlockEntity, pPackedLight)
            }
        }
        pPoseStack.popPose()
    }

    private fun renderStaticItem(pStack: ItemStack, pPoseStack: PoseStack, pBuffer: MultiBufferSource, pBlockEntity: BlockEntity, pPackedLight: Int) {
        val itemRenderer = Minecraft.getInstance().itemRenderer
        itemRenderer.renderStatic(
            pStack,
            ItemDisplayContext.FIXED,
            pPackedLight,
            OverlayTexture.NO_OVERLAY,
            pPoseStack,
            pBuffer,
            pBlockEntity.level,
            1
        )
    }
}