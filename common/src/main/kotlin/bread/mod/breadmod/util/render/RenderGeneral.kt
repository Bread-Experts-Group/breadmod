package bread.mod.breadmod.util.render

import bread.mod.breadmod.ModMainCommon.modLocation
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import org.jetbrains.annotations.ApiStatus.Internal
import org.joml.Vector3f
import org.joml.Vector4f

@Internal
val rgMinecraft: Minecraft = Minecraft.getInstance()
internal typealias RenderBuffer = MutableList<Pair<MutableList<Float>, (MutableList<Float>, PoseStack, Camera) -> Boolean>>

var skyColorMixinActive: Boolean = false
var redness: Float = 1f

// --Commented out by Inspection START (9/10/2024 03:54):
//val mouseGuiX: Double
//    get() = rgMinecraft.mouseHandler.xpos() * rgMinecraft.window.guiScaledWidth.toDouble() /
//            rgMinecraft.window.screenWidth.toDouble()
// --Commented out by Inspection STOP (9/10/2024 03:54)
// --Commented out by Inspection START (9/10/2024 03:54):
//val mouseGuiY: Double
//    get() = rgMinecraft.mouseHandler.ypos() * rgMinecraft.window.guiScaledHeight.toDouble() /
//            rgMinecraft.window.screenHeight.toDouble()
// --Commented out by Inspection STOP (9/10/2024 03:54)

/**
 * A list of lambdas to call for rendering. If lambdas return true, they will be removed.
 * TODO update javadoc
 * @author Miko Elbrecht
 * @since 1.0.0
 */
val renderBuffer: RenderBuffer = mutableListOf()

/**
 * Draws a line from between [start] and [end], translated according to the current [LocalPlayer]'s position.
// * @see breadmod.network.clientbound.BeamPacket
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun addBeamTask(start: Vector3f, end: Vector3f, thickness: Float?) =
    renderBuffer.add(mutableListOf(1F) to { mutableList, poseStack, camera ->
        val currentOpacity = mutableList[0]
        val level = rgMinecraft.level
        val player = rgMinecraft.player
        if (level != null && currentOpacity > 0 && player != null) {
            poseStack.pushPose()
            poseStack.translate(-camera.position.x, -camera.position.y - 1f, -camera.position.z)
            val bufferSource = rgMinecraft.renderBuffers().bufferSource()

//            poseStack.mulPose(Axis.YN.rotationDegrees(Math.floorMod(level.gameTime, 360).toFloat() + levelStageEvent.partialTick))

            if (thickness != null) {
                // South
                texturedQuadTest(
                    modLocation("block", "bread_block"),
                    RenderType.translucent(),
                    poseStack,
                    bufferSource,
                    Vector4f(1f, 1f, 1f, currentOpacity),
                    Vector3f(start.x + 1f, start.y, start.z + 1f),
                    Vector3f(start.x - 1f, start.y, start.z + 1f),
                    Vector3f(end.x - 1f, end.y, end.z + 1f),
                    Vector3f(end.x + 1f, end.y, end.z + 1f)
                )
//            poseStack.translate(2f, 0f, 0f)
                // East
                texturedQuadTest(
                    modLocation("block", "bread_block"),
                    RenderType.translucent(),
                    poseStack,
                    bufferSource,
                    Vector4f(1f, 1f, 1f, currentOpacity),
                    Vector3f(start.x + 1f, start.y, start.z - 1f),
                    Vector3f(start.x + 1f, start.y, start.z + 1f),
                    Vector3f(end.x + 1f, end.y, end.z + 1f),
                    Vector3f(end.x + 1f, end.y, end.z - 1f)
                )
                // West
                texturedQuadTest(
                    modLocation("block", "bread_block"),
                    RenderType.translucent(),
                    poseStack,
                    bufferSource,
                    Vector4f(1f, 1f, 1f, currentOpacity),
                    Vector3f(start.x - 1f, start.y, start.z + 1f),
                    Vector3f(start.x - 1f, start.y, start.z - 1f),
                    Vector3f(end.x - 1f, end.y, end.z - 1f),
                    Vector3f(end.x - 1f, end.y, end.z + 1f)
                )
                // North
                texturedQuadTest(
                    modLocation("block", "bread_block"),
                    RenderType.translucent(),
                    poseStack,
                    bufferSource,
                    Vector4f(1f, 1f, 1f, currentOpacity),
                    Vector3f(start.x - 1f, start.y, start.z - 1f),
                    Vector3f(start.x + 1f, start.y, start.z - 1f),
                    Vector3f(end.x + 1f, end.y, end.z - 1f),
                    Vector3f(end.x - 1f, end.y, end.z - 1f)
                )

                // Start
                texturedQuadTest(
                    modLocation("block", "bread_block"),
                    RenderType.translucent(),
                    poseStack,
                    bufferSource,
                    Vector4f(1f, 1f, 1f, currentOpacity),
                    Vector3f(start.x - 1f, start.y, start.z - 1f),
                    Vector3f(start.x - 1f, start.y, start.z + 1f),
                    Vector3f(start.x + 1f, start.y, start.z + 1f),
                    Vector3f(start.x + 1f, start.y, start.z - 1f)
                )
                // End
                texturedQuadTest(
                    modLocation("block", "bread_block"),
                    RenderType.translucent(),
                    poseStack,
                    bufferSource,
                    Vector4f(1f, 1f, 1f, currentOpacity),
                    Vector3f(end.x - 1f, end.y, end.z - 1f),
                    Vector3f(end.x + 1f, end.y, end.z - 1f),
                    Vector3f(end.x + 1f, end.y, end.z + 1f),
                    Vector3f(end.x - 1f, end.y, end.z + 1f)
                )
            }

            poseStack.popPose()
//            mutableList[0] = currentOpacity - 0.1f * rgMinecraft.timer.realtimeDeltaTicks
            false
        } else true
    })

fun PoseStack.scaleFlat(scale: Float): Unit = this.scale(scale, scale, scale)

fun setupOverlayRenderState(useBlend: Boolean, useDepthTest: Boolean) {
    if (useBlend) {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
    } else RenderSystem.disableBlend()
    if (useDepthTest) RenderSystem.enableDepthTest() else RenderSystem.disableDepthTest()
}

fun drawScaledText(
    pText: Component,
    pPose: PoseStack,
    pGuiGraphics: GuiGraphics,
    pX: Int,
    pY: Int,
    pColor: Int,
    pScale: Float,
    pDropShadow: Boolean
) {
    pPose.scaleFlat(pScale)
    pGuiGraphics.drawString(
        rgMinecraft.font,
        pText,
        pX,
        pY,
        pColor,
        pDropShadow
    )
    pPose.scaleFlat(1f)
}

/**
 * Renders a provided [model] (as an item model) onto a [BlockEntityWithoutLevelRenderer]
 */
fun ItemRenderer.renderItemModel(
    model: BakedModel,
    stack: ItemStack,
    displayContext: ItemDisplayContext,
    leftHand: Boolean,
    poseStack: PoseStack,
    buffer: MultiBufferSource,
    packedOverlay: Int,
    packedLight: Int,
//    renderType: RenderType = RenderType.glint()
) {
//    val renderType = if (renderType != RenderType.glint()) ItemBlockRenderTypes.getRenderType(stack, false) else renderType
    val renderType = ItemBlockRenderTypes.getRenderType(stack, false)
    val vertexConsumer = getFoilBufferDirect(buffer, renderType, true, stack.hasFoil())
    model.transforms.getTransform(displayContext).apply(leftHand, poseStack)
    renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexConsumer)
}

// todo test to see if this works
// todo it's busted, needs fixing
/**
 * Renders a provided [model] (as a block model) onto a [BlockEntityRenderer]
 *
 * *Note that the [model] does not rotate with the block*
 */
fun renderBlockModel(
    poseStack: PoseStack,
    bufferSource: MultiBufferSource,
    blockEntity: BlockEntity,
    model: BakedModel,
    packedLight: Int,
    packedOverlay: Int,
    renderType: RenderType = RenderType.solid()
) {
    rgMinecraft.blockRenderer.modelRenderer.renderModel(
        poseStack.last(),
        bufferSource.getBuffer(renderType),
        blockEntity.blockState,
        model,
        1f,
        1f,
        1f,
        packedLight,
        packedOverlay
    )
}

/**
 * Renders a provided [stack] onto a [BlockEntityRenderer]
 */
fun renderStaticItem(
    stack: ItemStack,
    poseStack: PoseStack,
    buffer: MultiBufferSource,
    blockEntity: BlockEntity,
    packedLight: Int
) {
    val itemRenderer = rgMinecraft.itemRenderer
    itemRenderer.renderStatic(
        stack,
        ItemDisplayContext.FIXED,
        packedLight,
        NO_OVERLAY,
        poseStack,
        buffer,
        blockEntity.level,
        1
    )
}

//// todo vertexes are inverted (probably only on tool gun beam, look into)
fun vertexTest(
    poseStack: PoseStack,
    pBuffer: MultiBufferSource,
    renderType: RenderType,
    color: Vector4f,
    x: Float,
    y: Float,
    z: Float,
    u: Float,
    v: Float
) {
    val buffer = pBuffer.getBuffer(renderType)
    buffer.addVertex(poseStack.last().pose(), x, y, z)
        .setColor(color.x, color.y, color.z, color.w)
        .setUv(u, v)
        .setOverlay(NO_OVERLAY)
        .setLight(0xFFFFFF)
        .setNormal(0f, 1f, 0f)
}

fun quadTest(
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pRenderType: RenderType,
    pColor: Vector4f,
    pVertex0: Vector3f,
    pVertex1: Vector3f,
    pVertex2: Vector3f,
    pVertex3: Vector3f,
    pU0: Float, pV0: Float,
    pU1: Float, pV1: Float
) {
    vertexTest(pPoseStack, pBuffer, pRenderType, pColor, pVertex0.x, pVertex0.y, pVertex0.z, pU0, pV0)
    vertexTest(pPoseStack, pBuffer, pRenderType, pColor, pVertex1.x, pVertex1.y, pVertex1.z, pU0, pV1)
    vertexTest(pPoseStack, pBuffer, pRenderType, pColor, pVertex2.x, pVertex2.y, pVertex2.z, pU1, pV1)
    vertexTest(pPoseStack, pBuffer, pRenderType, pColor, pVertex3.x, pVertex3.y, pVertex3.z, pU1, pV0)
}

fun texturedQuadTest(
    pSprite: ResourceLocation,
    pRenderType: RenderType,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pColor: Vector4f,
    pVertex0: Vector3f = Vector3f(0f, 0f, 0f),
    pVertex1: Vector3f = Vector3f(0f, 0f, 1f),
    pVertex2: Vector3f = Vector3f(1f, 0f, 1f),
    pVertex3: Vector3f = Vector3f(1f, 0f, 0f)
) {
    val sprite = rgMinecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pSprite)
    quadTest(
        pPoseStack, pBuffer, pRenderType, pColor,
        pVertex0,
        pVertex1,
        pVertex2,
        pVertex3,
        sprite.u0, sprite.v0,
        sprite.u1, sprite.v1
    )
}


//fun drawVertex(
//    pBuilder: VertexConsumer,
//    pPoseStack: PoseStack,
//    pX: Float,
//    pY: Float,
//    pZ: Float,
//    pU: Float,
//    pV: Float,
//    pPackedLight: Int,
//    pPackedOverlay: Int,
//    pColor: Int
//) {
//    pBuilder.vertex(pPoseStack.last().pose(), pX, pY, pZ)
//        .color(pColor)
//        .uv(pU, pV)
//        .overlayCoords(pPackedOverlay)
//        .uv2(pPackedLight)
//        .normal(0f, 1f, 0f)
//        .endVertex()
//}
//
//fun drawQuad(
//    pBuilder: VertexConsumer,
//    pPoseStack: PoseStack,
//    pColor: Int,
//    pX0: Float, pY0: Float, pZ0: Float,
//    pX1: Float, pY1: Float, pZ1: Float,
//    pU0: Float, pV0: Float,
//    pU1: Float, pV1: Float,
//    pPackedLight: Int,
//    pPackedOverlay: Int
//) {
//    drawVertex(pBuilder, pPoseStack, pX0, pY0, pZ0, pU0, pV0, pPackedLight, pColor, pPackedOverlay)
//    drawVertex(pBuilder, pPoseStack, pX0, pY1, pZ1, pU0, pV1, pPackedLight, pColor, pPackedOverlay)
//    drawVertex(pBuilder, pPoseStack, pX1, pY1, pZ1, pU1, pV1, pPackedLight, pColor, pPackedOverlay)
//    drawVertex(pBuilder, pPoseStack, pX1, pY0, pZ0, pU1, pV0, pPackedLight, pColor, pPackedOverlay)
//}
//
//fun drawTexturedQuad(
//    pTextureLocation: ResourceLocation,
//    pRenderType: RenderType,
//    pPoseStack: PoseStack,
//    pBuffer: MultiBufferSource,
//    pPackedLight: Int,
//    pPackedOverlay: Int,
//    pX0: Float = 0f, pY0: Float = 0f, pZ0: Float = 0f,
//    pX1: Float = 1f, pY1: Float = 0f, pZ1: Float = 1f
//) {
//    val sprite = rgMinecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pTextureLocation)
//    val spriteBuilder = pBuffer.getBuffer(pRenderType)
//    drawQuad(
//        spriteBuilder, pPoseStack, Color.WHITE.rgb,
//        pX0, pY0, pZ0,
//        pX1, pY1, pZ1,
//        sprite.u0, sprite.v0,
//        sprite.u1, sprite.v1,
//        pPackedLight,
//        pPackedOverlay
//    )
//}

/**
 * Renders a given [Component] onto a [BlockEntityWithoutLevelRenderer] or [BlockEntityRenderer]
 *
 * @param component The text as a [Component.literal] or [Component.translatable] to be rendered onto the target block or item.
 * @param color The primary text color as an integer.
 * @param backgroundColor Secondary text color as an integer, applies to the background
 * @param fontRenderer Draws the text onto the target block or item
 * @param postStack Positions the text onto the target block or item
 * @param dropShadow draws a drop shadow behind the text
 *
 * @see Font.drawInBatch
 * @since 0.0.1
 */
fun renderText(
    component: FormattedCharSequence,
    color: Int,
    backgroundColor: Int,
    fontRenderer: Font,
    postStack: PoseStack,
    buffer: MultiBufferSource,
    dropShadow: Boolean,
    packedLight: Int
) {
    fontRenderer.drawInBatch(
        component,
        0f,
        0f,
        color,
        dropShadow,
        postStack.last().pose(),
        buffer,
        Font.DisplayMode.NORMAL,
        backgroundColor,
        packedLight
    )
}

//private const val TRANSLATE_OFFSET = 0.0001

///**
// * [posX], [posY], [posZ] translates the [poseStack] on the facing side of the block. *(not required)*
// * ### translated [poseStack] starts at the top left of the facing side
// *
// * @see translateDirection
// */
//fun translateOnBlockSide(
//    blockState: BlockState, direction: Direction? = null,
//    poseStack: PoseStack, posX: Double = 0.0, posY: Double = 0.0, posZ: Double = 0.0
//) {
//    var facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
//        ?: throw IllegalArgumentException("Provided block state must have a HORIZONTAL_FACING property")
//    if (direction != null) facing = translateDirection(facing, direction)
//
//    poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()))
//    poseStack.translate(posX, posY, posZ)
//    when (facing) {
//        Direction.NORTH -> poseStack.translate(-1.0, 1.0, TRANSLATE_OFFSET)
//        Direction.EAST -> poseStack.translate(-1.0, 1.0, 1 + TRANSLATE_OFFSET)
//        Direction.WEST -> poseStack.translate(0.0, 1.0, TRANSLATE_OFFSET)
//        Direction.SOUTH -> poseStack.translate(0.0, 1.0, 1 + TRANSLATE_OFFSET)
//        Direction.UP, Direction.DOWN -> {
//            poseStack.translate(-1.0, 1 + TRANSLATE_OFFSET, 0.0)
//            poseStack.mulPose(Axis.XN.rotationDegrees(90F))
//        }
//    }
//}

//val TRANSPARENT: Int = Color(0f, 0f, 0f, 0f).rgb

// --Commented out by Inspection START (9/10/2024 03:54):
//fun drawTextOnSide(
//    fontRenderer: Font,
//    component: Component,
//
//    posX: Double,
//    posY: Double,
//    posZ: Double = 0.0,
//
//    poseStack: PoseStack,
//    buffer: MultiBufferSource,
//    blockState: BlockState,
//
//    color: Int = Color.WHITE.rgb,
//    backgroundColor: Int = TRANSPARENT,
//    dropShadow: Boolean = false,
//    direction: Direction? = null,
//    scale: Float = 1f
//) {
//    poseStack.pushPose()
//    translateOnBlockSide(blockState, direction, poseStack, posX, posY, posZ)
//    poseStack.mulPose(Axis.XN.rotationDegrees(180f))
//    poseStack.scaleFlat(scale)
//    renderText(component.visualOrderText, color, backgroundColor, fontRenderer, poseStack, buffer, dropShadow, 15728880)
//    poseStack.popPose()
//}
// --Commented out by Inspection STOP (9/10/2024 03:54)

// --Commented out by Inspection START (9/10/2024 03:54):
//fun drawCenteredTextOnSide(
//    fontRenderer: Font,
//    component: Component,
//
//    posX: Double,
//    posY: Double,
//    posZ: Double = 0.0,
//
//    poseStack: PoseStack,
//    buffer: MultiBufferSource,
//    blockState: BlockState,
//
//    color: Int = Color.WHITE.rgb,
//    backgroundColor: Int = TRANSPARENT,
//    dropShadow: Boolean = false,
//    direction: Direction? = null,
//    scale: Float = 1f
//) {
//    poseStack.pushPose()
//    translateOnBlockSide(
//        blockState, direction, poseStack,
//        posX - rgMinecraft.font.width(component.visualOrderText) / 2,
//        posY, posZ
//    )
//    poseStack.mulPose(Axis.XN.rotationDegrees(180f))
//    poseStack.scaleFlat(scale)
//    renderText(
//        component.visualOrderText, color, backgroundColor, fontRenderer,
//        poseStack, buffer, dropShadow, 15728880
//    )
//    poseStack.popPose()
//}
// --Commented out by Inspection STOP (9/10/2024 03:54)

//fun renderEntityInInventoryFollowsMouse(
//    pGuiGraphics: GuiGraphics,
//    pX: Int,
//    pY: Int,
//    pScale: Double,
//    pMouseX: Float,
//    pMouseY: Float,
//    pEntity: Entity
//) {
//    val f = atan((pMouseX / 40.0f).toDouble()).toFloat()
//    val f1 = atan((pMouseY / 40.0f).toDouble()).toFloat()
//    renderEntityInInventoryFollowsAngle(pGuiGraphics, pX, pY, pScale, f, f1, pEntity)
//}
//
//fun renderEntityInInventoryFollowsAngle(
//    pGuiGraphics: GuiGraphics,
//    pX: Int,
//    pY: Int,
//    pScale: Double,
//    angleXComponent: Float,
//    angleYComponent: Float,
//    pEntity: Entity
//) {
//    val quaternionF = Quaternionf().rotateZ(Math.PI.toFloat())
//    val quaternionF1 = Quaternionf().rotateX(angleYComponent * 20.0f * (Math.PI.toFloat() / 180f))
//    quaternionF.mul(quaternionF1)
//    val f2 = if (pEntity is LivingEntity) pEntity.yBodyRot else 0f
//    val f3 = pEntity.yRot
//    val f4 = pEntity.xRot
//    val f5 = if (pEntity is LivingEntity) pEntity.yHeadRotO else 0f
//    val f6 = pEntity.yHeadRot
//    if (pEntity is LivingEntity) pEntity.yBodyRot = 180.0f + angleXComponent * 20.0f
//    pEntity.yRot = 180.0f + angleXComponent * 40.0f
//    pEntity.xRot = -angleYComponent * 20.0f
//    pEntity.yHeadRot = pEntity.yRot
//    if (pEntity is LivingEntity) pEntity.yHeadRotO = pEntity.yRot
//    renderEntityInInventory(pGuiGraphics, pX, pY, pScale, quaternionF, quaternionF1, pEntity)
//    pEntity.yRot = f3
//    pEntity.xRot = f4
//    pEntity.yHeadRot = f6
//    if (pEntity is LivingEntity) {
//        pEntity.yBodyRot = f2
//        pEntity.yHeadRotO = f5
//    }
//}
//
//fun renderEntityInInventory(
//    pGuiGraphics: GuiGraphics,
//    pX: Int,
//    pY: Int,
//    pScale: Double,
//    pPose: Quaternionf,
//    pCameraOrientation: Quaternionf,
//    pEntity: Entity
//) {
//    pGuiGraphics.pose().pushPose()
//    pGuiGraphics.pose().translate(pX.toDouble(), pY.toDouble(), 50.0)
//    pGuiGraphics.pose().mulPoseMatrix(Matrix4f().scaling(pScale.toFloat(), pScale.toFloat(), (-pScale).toFloat()))
//    pGuiGraphics.pose().mulPose(pPose)
//    Lighting.setupForEntityInInventory()
//    val entityRenderDispatcher = rgMinecraft.entityRenderDispatcher
//    pCameraOrientation.conjugate()
//    entityRenderDispatcher.overrideCameraOrientation(pCameraOrientation)
//
//    entityRenderDispatcher.setRenderShadow(false)
//    RenderSystem.runAsFancy {
//        entityRenderDispatcher.render(
//            pEntity,
//            0.0,
//            0.0,
//            0.0,
//            0.0f,
//            1.0f,
//            pGuiGraphics.pose(),
//            pGuiGraphics.bufferSource(),
//            15728880
//        )
//    }
//    pGuiGraphics.flush()
//    entityRenderDispatcher.setRenderShadow(true)
//    pGuiGraphics.pose().popPose()
//    Lighting.setupFor3DItems()
//}