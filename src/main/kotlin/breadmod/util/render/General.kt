package breadmod.util.render

import breadmod.ModMain
import breadmod.util.translateDirection
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.RenderTypeHelper
import net.minecraftforge.client.event.RenderLevelStageEvent
import org.joml.Vector3f
import java.awt.Color

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

//    RenderSystem.enableBlend()
//    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader { GameRenderer.getPositionColorTexShader() }
    RenderSystem.setShaderTexture(0, ModMain.modLocation("textures", "block", "bread_block.png"))


    val tessellator = Tesselator.getInstance()
    it.poseStack.pushPose()
    it.poseStack.translate(-playerEyePos.x, -playerEyePos.y, -playerEyePos.z)
    val poseStack = it.poseStack.last().pose()

    val instance = Minecraft.getInstance()
    val sprite = instance.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation("breadmod", "block/bread_block.png"))
//    println(sprite.atlasLocation())

//    RenderSystem.setShaderTexture(0, ResourceLocation("minecraft", "block/grass_block"))
//    RenderSystem.bindTexture(0)

    val builder = tessellator.builder
    builder.begin(/*if(thickness != null) VertexFormat.Mode.QUADS else VertexFormat.Mode.DEBUG_LINES*/VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR)
    // Q1
    if(thickness != null) {
        builder.vertex(poseStack, start.x, start.y, start.z)
            .color(1f,1f,1f,1f)
            .uv(sprite.u0, sprite.v0)
            .overlayCoords(NO_OVERLAY)
            .uv2(0xFFFFFF)
            .normal(0f, 1f, 0f)
            .endVertex()
        builder.vertex(poseStack, start.x, start.y, start.z + thickness)
            .color(1f,1f,1f,1f)
            .uv(sprite.u0, sprite.v0)
            .overlayCoords(NO_OVERLAY)
            .uv2(0xFFFFFF)
            .normal(0f, 1f, 0f)
            .endVertex()
        builder.vertex(poseStack, end.x, end.y, end.z)
            .color(1f,1f,1f,1f)
            .uv(sprite.u0, sprite.v0)
            .overlayCoords(NO_OVERLAY)
            .uv2(0xFFFFFF)
            .normal(0f, 1f, 0f)
            .endVertex()

        builder.vertex(poseStack, start.x, start.y, start.z)
            .color(1f,1f,1f,1f)
            .uv(sprite.u1, sprite.v1)
            .overlayCoords(NO_OVERLAY)
            .uv2(0xFFFFFF)
            .normal(0f, 1f, 0f)
            .endVertex()
        builder.vertex(poseStack, end.x, end.y, end.z)
            .color(1f,1f,1f,1f)
            .uv(sprite.u1, sprite.v1)
            .overlayCoords(NO_OVERLAY)
            .uv2(0xFFFFFF)
            .normal(0f, 1f, 0f)
            .endVertex()
        builder.vertex(poseStack, end.x, end.y, end.z - thickness)
            .color(1f,1f,1f,1f)
            .uv(sprite.u1, sprite.v1)
            .overlayCoords(NO_OVERLAY)
            .uv2(0xFFFFFF)
            .normal(0f, 1f, 0f)
            .endVertex()
    }

//    builder.vertex(poseStack, start.x, start.y, start.z).color(1f,1f,1f,1f).endVertex()
//    builder.vertex(poseStack, end.x - 1f, end.y - 1f, end.z - 1f).color(1f,1f,1f,1f).endVertex()
//    builder.vertex(poseStack, start.x, start.y, start.z).color(1f,1f,1f,1f).endVertex()
//    builder.end()
//    builder.vertex(poseStack, end.x + 1.0f, end.y, end.z).color(1f,1f,1f,1f).uv(sprite.u1, sprite.v0).endVertex()

//    if(thickness != null) {
//        println(thickness)
//        builder.vertex(poseStack, start.x + thickness, start.y, start.z).color(1f,1f,1f,1f).uv(sprite.u1, sprite.v1).endVertex()
//        builder.vertex(poseStack, end.x + thickness, end.y, end.z).color(1f,1f,1f,1f).uv(sprite.u1, sprite.v0).endVertex()
//        // Q2
//        builder.vertex(poseStack, start.x, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
//        builder.vertex(poseStack, end.x, end.y + thickness, end.z).color(0f,1f,1f,1f) .endVertex()
//
//        builder.vertex(poseStack, start.x + thickness, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
//        builder.vertex(poseStack, end.x + thickness, end.y + thickness, end.z).color(0f,1f,1f,1f).endVertex()
//        // Q3
//        builder.vertex(poseStack, start.x + thickness, start.y, start.z).color(0f,1f,1f,1f).endVertex()
//        builder.vertex(poseStack, end.x + thickness, end.y, end.z).color(0f,1f,1f,1f) .endVertex()
//
//        builder.vertex(poseStack, start.x + thickness, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
//        builder.vertex(poseStack, end.x + thickness, end.y + thickness, end.z).color(0f,1f,1f,1f).endVertex()
//        // Q4
//        builder.vertex(poseStack, start.x, start.y, start.z).color(0f,1f,1f,1f).endVertex()
//        builder.vertex(poseStack, end.x, end.y, end.z).color(0f,1f,1f,1f) .endVertex()
//
//        builder.vertex(poseStack, start.x, start.y + thickness, start.z).color(0f,1f,1f,1f).endVertex()
//        builder.vertex(poseStack, end.x, end.y + thickness, end.z).color(0f,1f,1f,1f).endVertex()
//    }

    tessellator.end()
//    RenderSystem.disableBlend()
    it.poseStack.popPose()

    return@add false
}

//fun addBeamTask(start: Vector3f, end: Vector3f, thickness: Float?) {
//    val instance = Minecraft.getInstance()
//    val playerEyePos = (Minecraft.getInstance().player ?: return).getEyePosition(instance.partialTick)
//
//    val bufferSource: MultiBufferSource = instance.renderBuffers().bufferSource()
//    val poseStack = PoseStack()
//
//
//    poseStack.pushPose()
//    println("drawing quad")
//    texturedQuadTest(
//        ResourceLocation("breadmod", "block/bread_block"),
//        RenderType.solid(),
//        poseStack,
//        bufferSource
//    )
//    poseStack.popPose()
//}

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
    pPackedLight: Int,
    pRenderType: RenderType = RenderType.glint()
) {
    val glint = pStack.hasFoil()
    for(type in pModel.getRenderTypes(pStack, false)) {
        val helper: RenderType = if(pRenderType != RenderType.glint()) pRenderType else RenderTypeHelper.getEntityRenderType(type, false)
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
        NO_OVERLAY,
        pPoseStack,
        pBuffer,
        pBlockEntity.level,
        1
    )
}

fun vertexTest(
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pRenderType: RenderType,
    pX: Float,
    pY: Float,
    pZ: Float,
    pU: Float,
    pV: Float
) {
    val buffer = pBuffer.getBuffer(pRenderType)
    buffer.vertex(pPoseStack.last().pose(), pX, pY, pZ)
        .color(1f, 1f, 1f, 1f)
        .uv(pU, pV)
        .overlayCoords(NO_OVERLAY)
        .uv2(0xFFFFFF)
        .normal(0f, 1f, 0f)
        .endVertex()
}

fun quadTest(
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pRenderType: RenderType,
    pX0: Float, pY0: Float, pZ0: Float,
    pX1: Float, pY1: Float, pZ1: Float,
    pU0: Float, pV0: Float,
    pU1: Float, pV1: Float
) {
    vertexTest(pPoseStack, pBuffer, pRenderType, pX0, pY0, pZ0, pU0, pV0)
    vertexTest(pPoseStack, pBuffer, pRenderType, pX0, pY1, pZ1, pU0, pV1)
    vertexTest(pPoseStack, pBuffer, pRenderType, pX1, pY1, pZ1, pU1, pV1)
    vertexTest(pPoseStack, pBuffer, pRenderType, pX1, pY0, pZ0, pU1, pV0)
}

fun texturedQuadTest(
    pSprite: ResourceLocation,
    pRenderType: RenderType,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pX0: Float = 0f, pY0: Float = 0f, pZ0: Float = 0f,
    pX1: Float = 1f, pY1: Float = 0f, pZ1: Float = 1f
) {
    val instance = Minecraft.getInstance()
    val sprite = instance.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pSprite)
    quadTest(
        pPoseStack, pBuffer, pRenderType,
        pX0, pY0, pZ0,
        pX1, pY1, pZ1,
        sprite.u0, sprite.v0,
        sprite.u1, sprite.v1
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
    pPackedOverlay: Int,
    pColor: Int
) {
    pBuilder.vertex(pPoseStack.last().pose(), pX, pY, pZ)
        .color(pColor)
        .uv(pU, pV)
        .overlayCoords(pPackedOverlay)
        .uv2(pPackedLight)
        .normal(0f, 1f, 0f)
        .endVertex()
}

fun drawQuad(
    pBuilder: VertexConsumer,
    pPoseStack: PoseStack,
    pColor: Int,
    pX0: Float, pY0: Float, pZ0: Float,
    pX1: Float, pY1: Float, pZ1: Float,
    pU0: Float, pV0: Float,
    pU1: Float, pV1: Float,
    pPackedLight: Int,
    pPackedOverlay: Int
) {
    drawVertex(pBuilder, pPoseStack, pX0, pY0, pZ0, pU0, pV0, pPackedLight, pColor, pPackedOverlay)
    drawVertex(pBuilder, pPoseStack, pX0, pY1, pZ1, pU0, pV1, pPackedLight, pColor, pPackedOverlay)
    drawVertex(pBuilder, pPoseStack, pX1, pY1, pZ1, pU1, pV1, pPackedLight, pColor, pPackedOverlay)
    drawVertex(pBuilder, pPoseStack, pX1, pY0, pZ0, pU1, pV0, pPackedLight, pColor, pPackedOverlay)
}

fun drawTexturedQuad(
    pTextureLocation: ResourceLocation,
    pRenderType: RenderType,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pPackedLight: Int,
    pPackedOverlay: Int,
    pX0: Float = 0f, pY0: Float = 0f, pZ0: Float = 0f,
    pX1: Float = 1f, pY1: Float = 0f, pZ1: Float = 1f
) {
    val instance = Minecraft.getInstance()
    val sprite = instance.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pTextureLocation)
    val spriteBuilder = pBuffer.getBuffer(pRenderType)
    drawQuad(
        spriteBuilder, pPoseStack, Color.WHITE.rgb,
        pX0, pY0, pZ0,
        pX1, pY1, pZ1,
        sprite.u0, sprite.v0,
        sprite.u1, sprite.v1,
        pPackedLight,
        pPackedOverlay
    )
}

/**
 * Renders a given [Component] onto a [BlockEntityWithoutLevelRenderer] or [BlockEntityRenderer]
 *
 * @param pComponent The text as a [Component.literal] or [Component.translatable] to be rendered onto the target block or item.
 * @param pColor The primary text color as an integer.
 * @param pBackgroundColor Secondary text color as an integer, applies to the background
 * @param pFontRenderer Draws the text onto the target block or item
 * @param pPoseStack Positions the text onto the target block or item
 * @param pDropShadow draws a drop shadow behind the text
 *
 * @see Font.drawInBatch
 * @author Logan McLean
 * @since 0.0.1
 */
fun renderText(
    pComponent: Component,
    pColor: Int,
    pBackgroundColor: Int,
    pFontRenderer: Font,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pDropShadow: Boolean,
    pPackedLight: Int
) {
    pFontRenderer.drawInBatch(
        pComponent,
        0f,
        0f,
        pColor,
        pDropShadow,
        pPoseStack.last().pose(),
        pBuffer,
        Font.DisplayMode.NORMAL,
        pBackgroundColor,
        pPackedLight
    )
}

private const val TRANSLATE_OFFSET = 0.0001

/**
 * [pPosX], [pPosY], [pPosZ] translates the [pPoseStack] on the facing side of the block. *(not required)*
 * ### translated [pPoseStack] starts at the top left of the facing side
 *
 * @see translateDirection
 */
fun translateOnBlockSide(
    pBlockState: BlockState, pDirection: Direction? = null,
    pPoseStack: PoseStack, pPosX: Double = 0.0, pPosY: Double = 0.0, pPosZ: Double = 0.0
) {
    var facing = pBlockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        ?: throw IllegalArgumentException("Provided block state must have a HORIZONTAL_FACING property")
    if(pDirection != null) facing = translateDirection(facing, pDirection)

    pPoseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()))
    pPoseStack.translate(pPosX, pPosY, pPosZ)
    when(facing) {
        Direction.NORTH -> pPoseStack.translate(-1.0, 1.0, TRANSLATE_OFFSET)
        Direction.EAST -> pPoseStack.translate(-1.0, 1.0, 1 + TRANSLATE_OFFSET)
        Direction.WEST -> pPoseStack.translate(0.0, 1.0, TRANSLATE_OFFSET)
        Direction.SOUTH -> pPoseStack.translate(0.0, 1.0, 1 + TRANSLATE_OFFSET)
        Direction.UP, Direction.DOWN -> {
            pPoseStack.translate(-1.0, 1 + TRANSLATE_OFFSET, 0.0)
            pPoseStack.mulPose(Axis.XN.rotationDegrees(90F))
        }
    }
}

fun drawTextOnSide(
    pFontRenderer: Font,
    pComponent: Component,
    pColor: Int,
    pBackgroundColor: Int,
    pDropShadow: Boolean,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pBlockState: BlockState,
    pDirection: Direction? = null,
    pScale: Float,
    pPosX: Double,
    pPosY: Double,
    pPosZ: Double = 0.0
) {
    pPoseStack.pushPose()
    translateOnBlockSide(pBlockState, pDirection, pPoseStack, pPosX, pPosY, pPosZ)
    pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
    pPoseStack.scale(pScale, pScale, pScale)
    renderText(pComponent, pColor, pBackgroundColor, pFontRenderer, pPoseStack, pBuffer, pDropShadow, Color(255, 255, 255, 0).rgb)
    pPoseStack.popPose()
}