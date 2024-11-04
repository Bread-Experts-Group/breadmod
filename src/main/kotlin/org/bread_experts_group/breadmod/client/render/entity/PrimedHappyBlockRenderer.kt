package org.bread_experts_group.breadmod.client.render.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.TntMinecartRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.entity.PrimedHappyBlock
import org.bread_experts_group.breadmod.registry.block.ModBlocks

class PrimedHappyBlockRenderer(
    private val context: EntityRendererProvider.Context
) : EntityRenderer<PrimedHappyBlock>(context) {
    init {
        shadowRadius = 0.5f
    }

    override fun render(
        primedHappyBlock: PrimedHappyBlock,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0f, 0.5f, 0.0f)
        val fuse = primedHappyBlock.fuse
        if (fuse.toFloat() - partialTick + 1.0f < 10.0f) {
            var f = 1.0f - (fuse.toFloat() - partialTick + 1.0f) / 10.0f
            f = Mth.clamp(f, 0.0f, 1.0f)
            f *= f
            f *= f
            val f1 = 1.0f + f * 0.3f
            poseStack.scale(f1, f1, f1)
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f))
        poseStack.translate(-0.5f, -0.5f, 0.5f)
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f))
        TntMinecartRenderer.renderWhiteSolidBlock(
            context.blockRenderDispatcher,
            ModBlocks.HAPPY_BLOCK.get().block.defaultBlockState(),
            poseStack,
            bufferSource,
            packedLight,
            fuse / 5 % 2 == 0
        )
        poseStack.popPose()
        super.render(primedHappyBlock, entityYaw, partialTick, poseStack, bufferSource, packedLight)
    }

    override fun getTextureLocation(entity: PrimedHappyBlock): ResourceLocation =
        modLocation("textures", "block", "happy_block.png")
}