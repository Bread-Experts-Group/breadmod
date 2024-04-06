package breadmod.entity.renderer

import breadmod.BreadMod
import breadmod.block.registry.ModBlocks.HAPPY_BLOCK
import breadmod.entity.PrimedHappyBlock
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.TntMinecartRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class PrimedHappyBlockRenderer(pContext: EntityRendererProvider.Context) :
    EntityRenderer<PrimedHappyBlock>(pContext) {
    private val blockRenderer: BlockRenderDispatcher

    init {
        this.shadowRadius = 0.5f
        this.blockRenderer = pContext.blockRenderDispatcher
    }

    override fun render(
        pEntity: PrimedHappyBlock,
        pEntityYaw: Float,
        pPartialTicks: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int
    ) {
        pPoseStack.pushPose()
        pPoseStack.translate(0.0f, 0.5f, 0.0f)
        val i = pEntity.fuse
        if (i.toFloat() - pPartialTicks + 1.0f < 10.0f) {
            var f = 1.0f - (i.toFloat() - pPartialTicks + 1.0f) / 10.0f
            f = Mth.clamp(f, 0.0f, 1.0f)
            f *= f
            f *= f
            val f1 = 1.0f + f * 0.3f
            pPoseStack.scale(f1, f1, f1)
        }

        pPoseStack.mulPose(Axis.YP.rotationDegrees(-90.0f))
        pPoseStack.translate(-0.5f, -0.5f, 0.5f)
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90.0f))
        TntMinecartRenderer.renderWhiteSolidBlock(
            this.blockRenderer,
            HAPPY_BLOCK.get().block.defaultBlockState(),
            pPoseStack,
            pBuffer,
            pPackedLight,
            i / 5 % 2 == 0
        )
        pPoseStack.popPose()
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight)
    }

    override fun getTextureLocation(pEntity: PrimedHappyBlock): ResourceLocation {
        return texture
    }

    companion object {
        private val texture = ResourceLocation(BreadMod.ID, "textures/block/happy_block.png")
    }
}
