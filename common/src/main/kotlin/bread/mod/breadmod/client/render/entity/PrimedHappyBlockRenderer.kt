package bread.mod.breadmod.client.render.entity

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.entity.PrimedHappyBlock
import bread.mod.breadmod.registry.block.ModBlocks
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.TntMinecartRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class PrimedHappyBlockRenderer(
    context: EntityRendererProvider.Context
) : EntityRenderer<PrimedHappyBlock>(context) {
    private var blockRenderer: BlockRenderDispatcher? = null

    init {
        this.shadowRadius = 0.5f
        this.blockRenderer = context.blockRenderDispatcher
    }

    override fun getTextureLocation(entity: PrimedHappyBlock): ResourceLocation = texture

    override fun render(
        entity: PrimedHappyBlock,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0f, 0.5f, 0.0f)
        val i = entity.fuse
        if (i.toFloat() - partialTick + 1.0f < 10.0f) {
            var f = 1.0f - (i.toFloat() - partialTick + 1.0f) / 10.0f
            f = Mth.clamp(f, 0.0f, 1.0f)
            f *= f
            f *= f
            val f1 = 1.0f + f * 0.3f
            poseStack.scale(f1, f1, f1)
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f))
        poseStack.translate(-0.5f, -0.5f, 0.5f)
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f))
        this.blockRenderer?.let {
            TntMinecartRenderer.renderWhiteSolidBlock(
                it,
                ModBlocks.HAPPY_BLOCK.get().block.defaultBlockState(),
                poseStack,
                bufferSource,
                packedLight,
                i / 5 % 2 == 0
            )
        }
        poseStack.popPose()
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    companion object {
        private val texture = modLocation("textures", "block", "happy_block.png")
    }
}