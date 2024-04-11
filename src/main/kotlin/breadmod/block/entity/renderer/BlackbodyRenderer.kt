package breadmod.block.entity.renderer

import breadmod.block.HeatingElementBlock
import breadmod.block.entity.HeatingElementBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexMultiConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.level.block.state.BlockState

class BlackbodyRenderer: BlockEntityRenderer<HeatingElementBlockEntity> {
    override fun render(
        pBlockEntity: HeatingElementBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int,
    ) {
        val vertexConsumer = VertexMultiConsumer.create(
            pBuffer.getBuffer(RenderType.glintDirect()), pBuffer.getBuffer(
                RenderType.solid()
            )
        )

        val blockState: BlockState = pBlockEntity.blockState
        val level = pBlockEntity.level!!

//        val model: BakedModel = this.blockRenderer.getBlockModel(blockState)
//        ModelBlockRenderer() .modelRenderer.tesselateBlock(
//            level,
//            model,
//            blockState,
//            pBlockEntity.blockPos,
//            pPoseStack,
//            vertexConsumer,
//            false,
//            level.getRandom(),
//            0,
//            pPackedOverlay
//        )
        println("vomit")
    }
}