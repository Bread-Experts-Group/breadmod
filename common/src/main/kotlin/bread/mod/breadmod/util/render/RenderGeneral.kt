package bread.mod.breadmod.util.render

import bread.mod.breadmod.ModMainCommon.modLocation
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import org.jetbrains.annotations.ApiStatus.Internal
import org.joml.Vector3f
import org.joml.Vector4f
import java.awt.Color
import java.lang.Math.clamp

@Internal
val rgMinecraft: Minecraft = Minecraft.getInstance()
internal typealias RenderBuffer = MutableList<Pair<MutableList<Float>, (MutableList<Float>, PoseStack, Camera, Float, LevelRenderer) -> Boolean>>

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

/* millis info
    val millis = Util.getMillis()

    // clamped decimal millis //
    println(clamp((millis.toFloat() / 300f % 2), 0f, 2f))

    // constant millis divided by a number //
    Mth.clamp(millis.toFloat(), 0f, 1f) / 1.5f
 */

/**
 * A list of lambdas to call for rendering. If lambdas return true, they will be removed.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
val renderBuffer: RenderBuffer = mutableListOf()

fun playerRenderTest(
    player: Player,
    x: Double,
    y: Double,
    z: Double,
    yRot: Float,
    limbSwing: Float,
//    headYaw: Float,
//    headPitch: Float
) = renderBuffer.add(
    mutableListOf(
        0.8F,
        0F,
        1F
    ) to { mutableList, poseStack, camera, partialTick, levelRenderer ->
        val currentOpacity = mutableList[0]
        val redValue = mutableList[1]
        val greenValue = mutableList[2]
        val connection = rgMinecraft.connection!!
        val playerInfo = connection.getPlayerInfo(player.uuid)!!
        val playerSkin = playerInfo.skin
        val texture = playerSkin.texture
        val modelType = playerSkin.model
        val playerModel = PlayerModel<Player>(
            rgMinecraft.entityModels.bakeLayer(
                if (modelType == PlayerSkin.Model.SLIM) ModelLayers.PLAYER_SLIM else ModelLayers.PLAYER
            ),
            modelType == PlayerSkin.Model.SLIM
        )
        val consumer = rgMinecraft.renderBuffers().bufferSource().getBuffer(RenderType.entityTranslucent(texture))
        val packedOverlay = LivingEntityRenderer.getOverlayCoords(player, 0f)

        if (currentOpacity > 0) {
            poseStack.pushPose()
            poseStack.mulPose(Axis.YN.rotationDegrees(-yRot))
            poseStack.translate(0.0, 0.0, -0.3)
            poseStack.mulPose(Axis.YN.rotationDegrees(yRot))
            poseStack.translate(-camera.position.x + x, -camera.position.y + y, -camera.position.z + z)
            poseStack.translate(0.0, 1.4, 0.0)
            poseStack.mulPose(Axis.XN.rotationDegrees(180f))
            poseStack.mulPose(Axis.YN.rotationDegrees(yRot))
            poseStack.scaleFlat(0.9375f)
            playerModel.setupAnim(
                player,
                limbSwing,
                0.55f,
                -1f, 0f, 0f
//        (headYaw - headYaw) + player.getViewYRot(partialTick),
//        (headPitch - headPitch) + player.getViewXRot(partialTick)
            )

            // clamp((millis.toFloat() / 300f) % 2f, 0f, 2f)

            mutableList[1] = clamp(redValue + 0.05f, 0f, 1f)
            mutableList[2] = clamp(greenValue - 0.05f, 0f, 1f)

            playerModel.young = false
            playerModel.renderToBuffer(
                poseStack,
                consumer,
                15728880,
                packedOverlay,
                Color(
                    redValue,
                    greenValue,
                    0.1f,
                    clamp(currentOpacity, 0f, 1f)
                ).rgb
            )
            poseStack.popPose()
            mutableList[0] = currentOpacity - 0.1f * rgMinecraft.timer.realtimeDeltaTicks
            false
        } else true
    })

/**
 * Draws a line from between [start] and [end], translated according to the current [LocalPlayer]'s position.
// * @see breadmod.network.clientbound.BeamPacket
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun addBeamTask(start: Vector3f, end: Vector3f, thickness: Float?) =
    renderBuffer.add(mutableListOf(1F) to { mutableList, poseStack, camera, partialTick, levelRenderer ->
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
                drawTexturedQuad(
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
                drawTexturedQuad(
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
                drawTexturedQuad(
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
                drawTexturedQuad(
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
                drawTexturedQuad(
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
                drawTexturedQuad(
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
            mutableList[0] = currentOpacity - 0.1f * rgMinecraft.timer.realtimeDeltaTicks
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
    text: Component,
    poseStack: PoseStack,
    guiGraphics: GuiGraphics,
    x: Int,
    y: Int,
    color: Int,
    scale: Float,
    dropShadow: Boolean
) {
    poseStack.scaleFlat(scale)
    guiGraphics.drawString(
        rgMinecraft.font,
        text,
        x,
        y,
        color,
        dropShadow
    )
    poseStack.scaleFlat(1f)
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

fun drawVertex(
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

fun drawQuad(
    poseStack: PoseStack,
    buffer: MultiBufferSource,
    renderType: RenderType,
    color: Vector4f,
    vertex0: Vector3f,
    vertex1: Vector3f,
    vertex2: Vector3f,
    vertex3: Vector3f,
    u0: Float, v0: Float,
    u1: Float, v1: Float
) {
    drawVertex(poseStack, buffer, renderType, color, vertex0.x, vertex0.y, vertex0.z, u0, v0)
    drawVertex(poseStack, buffer, renderType, color, vertex1.x, vertex1.y, vertex1.z, u0, v1)
    drawVertex(poseStack, buffer, renderType, color, vertex2.x, vertex2.y, vertex2.z, u1, v1)
    drawVertex(poseStack, buffer, renderType, color, vertex3.x, vertex3.y, vertex3.z, u1, v0)
}

fun drawTexturedQuad(
    textureLocation: ResourceLocation,
    renderType: RenderType,
    poseStack: PoseStack,
    buffer: MultiBufferSource,
    color: Vector4f,
    vertex0: Vector3f = Vector3f(0f, 0f, 0f),
    vertex1: Vector3f = Vector3f(0f, 0f, 1f),
    vertex2: Vector3f = Vector3f(1f, 0f, 1f),
    vertex3: Vector3f = Vector3f(1f, 0f, 0f)
) {
    val sprite = rgMinecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLocation)
    drawQuad(
        poseStack, buffer, renderType, color,
        vertex0,
        vertex1,
        vertex2,
        vertex3,
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