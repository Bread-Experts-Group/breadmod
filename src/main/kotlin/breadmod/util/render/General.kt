package breadmod.util.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.client.RenderTypeHelper
import net.minecraftforge.client.event.RenderLevelStageEvent
import org.joml.Vector3f

/**
 * A list of lambdas to call for rendering. If lambdas return true, they will be removed.
 * @see breadmod.ClientForgeEventBus.onLevelRender
 * @author Miko Elbrecht
 * @since 1.0.0
 */
val renderBuffer = mutableListOf<(RenderLevelStageEvent) -> Boolean>()

/**
 * Draws a line from between [start] and [end], translated according to the current [net.minecraft.client.player.LocalPlayer]'s position.
 * BUG: Only works on X axis
 * @see breadmod.network.BeamPacket
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun addBeamTask(start: Vector3f, end: Vector3f, thickness: Float?) = renderBuffer.add {
    val playerEyePos = (Minecraft.getInstance().player ?: return@add true).getEyePosition(it.partialTick)

    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader { GameRenderer.getPositionColorShader() }

    val tessellator = Tesselator.getInstance()
    it.poseStack.pushPose()
    it.poseStack.translate(-playerEyePos.x, -playerEyePos.y, -playerEyePos.z)
    val stack = it.poseStack.last().pose()
    
    val builder = tessellator.builder
    builder.begin(if(thickness != null) VertexFormat.Mode.QUADS else VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
    // Q1
    builder.vertex(stack, start.x, start.y, start.z).color(0f,1f,1f,1f).endVertex()
    builder.vertex(stack, end.x, end.y, end.z).color(0f,1f,1f,1f).endVertex()
    if(thickness != null) {
        builder.vertex(stack, start.x + thickness, start.y, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + thickness, end.y, end.z).color(0f,1f,1f,1f).endVertex()
        // Q2
        builder.vertex(stack, start.x, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x, end.y + thickness, end.z).color(0f,1f,1f,1f) .endVertex()

        builder.vertex(stack, start.x + thickness, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + thickness, end.y + thickness, end.z).color(0f,1f,1f,1f).endVertex()
        // Q3
        builder.vertex(stack, start.x + thickness, start.y, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + thickness, end.y, end.z).color(0f,1f,1f,1f) .endVertex()

        builder.vertex(stack, start.x + thickness, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + thickness, end.y + thickness, end.z).color(0f,1f,1f,1f).endVertex()
        // Q4
        builder.vertex(stack, start.x, start.y, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x, end.y, end.z).color(0f,1f,1f,1f) .endVertex()

        builder.vertex(stack, start.x, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x, end.y + thickness, end.z).color(0f,1f,1f,1f).endVertex()
    }

    tessellator.end()
    RenderSystem.disableBlend()
    it.poseStack.popPose()

    return@add false
}

/**
 * Renders a provided [pModel] (as an item model) onto a [BlockEntityWithoutLevelRenderer]
 */
fun renderItemModel(
    pModel: BakedModel,
    pRenderer: ItemRenderer,
    pStack: ItemStack,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pPackedOverlay: Int,
    pPackedLight: Int
) {
    val glint = pStack.hasFoil()
    for(type in pModel.getRenderTypes(pStack, false)) {
        val helper: RenderType = RenderTypeHelper.getEntityRenderType(type, false)
        val consumer = ItemRenderer.getFoilBuffer(pBuffer, helper, true, glint)
        pRenderer.renderModelLists(pModel, pStack, pPackedLight, pPackedOverlay, pPoseStack, consumer)
    }
}

/**
 * Renders a provided [pModel] (as a block model) onto a [BlockEntityRenderer]
 *
 * *Note that the [pModel] does not rotate with the block*
 */
fun renderBlockModel(
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pBlockEntity: BlockEntity,
    pModel: BakedModel,
    pPackedLight: Int,
    pPackedOverlay: Int,
    renderType: RenderType = RenderType.solid()
) {
    Minecraft.getInstance().blockRenderer.modelRenderer.renderModel(
        pPoseStack.last(),
        pBuffer.getBuffer(renderType),
        pBlockEntity.blockState,
        pModel,
        1f,
        1f,
        1f,
        pPackedLight,
        pPackedOverlay,
        pBlockEntity.modelData,
        renderType
    )
}

/**
 * Renders a provided [pStack] onto a [BlockEntityRenderer]
 */
fun renderStaticItem(pStack: ItemStack, pPoseStack: PoseStack, pBuffer: MultiBufferSource, pBlockEntity: BlockEntity, pPackedLight: Int) {
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

fun drawVertex(
    pBuilder: VertexConsumer,
    pPoseStack: PoseStack,
    pX: Float,
    pY: Float,
    pZ: Float,
    pU: Float,
    pV: Float,
    pPackedLight: Int,
    pColor: Int
) {
    pBuilder.vertex(pPoseStack.last().pose(), pX, pY, pZ)
        .color(pColor)
        .uv(pU, pV)
        .uv2(pPackedLight)
        .normal(1f, 0f, 0f)
        .endVertex()
}

fun drawQuad(
    pBuilder: VertexConsumer,
    pPoseStack: PoseStack,
    pX0: Float, pY0: Float, pZ0: Float,
    pX1: Float, pY1: Float, pZ1: Float,
    pU0: Float, pV0: Float,
    pU1: Float, pV1: Float,
    pPackedLight: Int,
    pColor: Int
) {
    drawVertex(pBuilder, pPoseStack, pX0, pY0, pZ0, pU0, pV0, pPackedLight, pColor)
    drawVertex(pBuilder, pPoseStack, pX0, pY1, pZ1, pU0, pV1, pPackedLight, pColor)
    drawVertex(pBuilder, pPoseStack, pX1, pY1, pZ1, pU1, pV1, pPackedLight, pColor)
    drawVertex(pBuilder, pPoseStack, pX1, pY0, pZ0, pU1, pV0, pPackedLight, pColor)
}