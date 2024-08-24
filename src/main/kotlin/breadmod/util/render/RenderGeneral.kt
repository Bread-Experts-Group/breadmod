package breadmod.util.render

import breadmod.ModMain.modLocation
import breadmod.util.translateDirection
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.client.RenderTypeHelper
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import org.jetbrains.annotations.ApiStatus.Internal
import org.joml.*
import java.awt.Color
import java.lang.Math
import java.util.*
import kotlin.math.atan
import kotlin.math.min

@Internal
val rgMinecraft: Minecraft = Minecraft.getInstance()
internal typealias RenderBuffer = MutableList<Pair<MutableList<Float>, (MutableList<Float>, RenderLevelStageEvent) -> Boolean>>

/**
 * A list of lambdas to call for rendering. If lambdas return true, they will be removed.
 * @see breadmod.ClientForgeEventBus.onLevelRender
 * @author Miko Elbrecht
 * @since 1.0.0
 */
internal val renderBuffer: RenderBuffer = mutableListOf()
private var angle = 0f

/**
 * Draws a line from between [start] and [end], translated according to the current [net.minecraft.client.player.LocalPlayer]'s position.
 * @see breadmod.network.clientbound.BeamPacket
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun addBeamTask(start: Vector3f, end: Vector3f, thickness: Float?) =
    renderBuffer.add(mutableListOf(1F) to { mutableList, levelStageEvent ->
        val currentOpacity = mutableList[0]
        val level = rgMinecraft.level
        val player = rgMinecraft.player
        val camera = levelStageEvent.camera
        if (level != null && currentOpacity > 0 && player != null) {
            levelStageEvent.poseStack.pushPose()
            levelStageEvent.poseStack.translate(-camera.position.x, -camera.position.y - 1f, -camera.position.z)
            val poseStack = levelStageEvent.poseStack
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

            levelStageEvent.poseStack.popPose()
            mutableList[0] = currentOpacity - 0.1f * rgMinecraft.partialTick
            false
        } else true
    })

fun GuiGraphics.renderFluid(
    pX: Float, pY: Float, pWidth: Int, pHeight: Int,
    pFluid: Fluid, pFlowing: Boolean, pDirection: Direction = Direction.NORTH,
) {
    val atlas = rgMinecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
    val ext = IClientFluidTypeExtensions.of(pFluid)
    val spriteDiff = if (pFlowing) {
        val stillWidth = atlas.apply(ext.stillTexture).contents().width().toFloat()
        atlas.apply(ext.flowingTexture).let {
            val flowingWidth = it.contents().width(); it to if (flowingWidth > stillWidth) (stillWidth / flowingWidth) else 1F
        }
    } else atlas.apply(ext.stillTexture) to 1F

    val sprite = spriteDiff.first
    val colors = FloatArray(4).also { Color(ext.tintColor).getComponents(it) }
    val matrix4f: Matrix4f = this.pose().last().pose()
    RenderSystem.setShaderTexture(0, sprite.atlasLocation())
    RenderSystem.setShader { GameRenderer.getPositionColorTexShader() }
    RenderSystem.enableBlend()
    val bufferBuilder = Tesselator.getInstance().builder
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX)

    val pX2 = pX + pWidth

    var remainingFluid = pHeight
    while (remainingFluid > 0) {
        // TODO: Make pY the TOP LEFT, instead of BOTTOM LEFT
        val lpY = (pY - remainingFluid)
        val lpY2 = lpY + min(remainingFluid, pWidth)

        // N  // E  // S  // W
        // AB // CA // DC // BD
        // CD // DB // BA // AC
        // (pX, lpY2), (pX2, lpY2)
        // (pX, lpY ), (pX2, lpY )
        val rotated = listOf(Vector2f(pX, lpY), Vector2f(pX, lpY2), Vector2f(pX2, lpY2), Vector2f(pX2, lpY)).also {
            Collections.rotate(
                it,
                when (pDirection) {
                    Direction.EAST -> 1; Direction.SOUTH -> 2; Direction.WEST -> 3; else -> 0
                }
            )
        }

        val dv1 = (sprite.v1 - sprite.v0)
        val v1 =
            if (remainingFluid < pWidth) (sprite.v0 + ((dv1 / pWidth) * remainingFluid)) else (sprite.v0 + (dv1 * spriteDiff.second))
        val u1 = sprite.u0 + ((sprite.u1 - sprite.u0) * spriteDiff.second)

        fun VertexConsumer.color() = this.color(colors[0], colors[1], colors[2], colors[3])
        rotated[0].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(u1, v1).endVertex() }
        rotated[1].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(u1, sprite.v0).endVertex() }
        rotated[2].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(sprite.u0, sprite.v0).endVertex() }
        rotated[3].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(sprite.u0, v1).endVertex() }

        remainingFluid -= pWidth
    }

    BufferUploader.drawWithShader(bufferBuilder.end())
    RenderSystem.disableBlend()
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
    pPackedLight: Int,
    pRenderType: RenderType = RenderType.glint()
) {
    val glint = pStack.hasFoil()
    for (type in pModel.getRenderTypes(pStack, false)) {
        val helper: RenderType =
            if (pRenderType != RenderType.glint()) pRenderType else RenderTypeHelper.getEntityRenderType(type, false)
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
    rgMinecraft.blockRenderer.modelRenderer.renderModel(
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
fun renderStaticItem(
    pStack: ItemStack,
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pBlockEntity: BlockEntity,
    pPackedLight: Int
) {
    val itemRenderer = rgMinecraft.itemRenderer
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

fun PoseStack.scaleFlat(scale: Float) = this.scale(scale, scale, scale)

fun vertexTest(
    pPoseStack: PoseStack,
    pBuffer: MultiBufferSource,
    pRenderType: RenderType,
    pColor: Vector4f,
    pX: Float,
    pY: Float,
    pZ: Float,
    pU: Float,
    pV: Float
) {
    val buffer = pBuffer.getBuffer(pRenderType)
    buffer.vertex(pPoseStack.last().pose(), pX, pY, pZ)
        .color(pColor.x, pColor.y, pColor.z, pColor.w)
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

// todo rename quad functions above to not have Test in their name and remove these functions below
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
    val sprite = rgMinecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pTextureLocation)
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
    if (pDirection != null) facing = translateDirection(facing, pDirection)

    pPoseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()))
    pPoseStack.translate(pPosX, pPosY, pPosZ)
    when (facing) {
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
    renderText(pComponent, pColor, pBackgroundColor, pFontRenderer, pPoseStack, pBuffer, pDropShadow, 15728880)
    pPoseStack.popPose()
}

fun renderEntityInInventoryFollowsMouse(
    pGuiGraphics: GuiGraphics,
    pX: Int,
    pY: Int,
    pScale: Double,
    pMouseX: Float,
    pMouseY: Float,
    pEntity: Entity
) {
    val f = atan((pMouseX / 40.0f).toDouble()).toFloat()
    val f1 = atan((pMouseY / 40.0f).toDouble()).toFloat()
    renderEntityInInventoryFollowsAngle(pGuiGraphics, pX, pY, pScale, f, f1, pEntity)
}

fun renderEntityInInventoryFollowsAngle(
    pGuiGraphics: GuiGraphics,
    pX: Int,
    pY: Int,
    pScale: Double,
    angleXComponent: Float,
    angleYComponent: Float,
    pEntity: Entity
) {
    val quaternionF = Quaternionf().rotateZ(Math.PI.toFloat())
    val quaternionF1 = Quaternionf().rotateX(angleYComponent * 20.0f * (Math.PI.toFloat() / 180f))
    quaternionF.mul(quaternionF1)
    val f2 = if (pEntity is LivingEntity) pEntity.yBodyRot else 0f
    val f3 = pEntity.yRot
    val f4 = pEntity.xRot
    val f5 = if (pEntity is LivingEntity) pEntity.yHeadRotO else 0f
    val f6 = pEntity.yHeadRot
    if (pEntity is LivingEntity) pEntity.yBodyRot = 180.0f + angleXComponent * 20.0f
    pEntity.yRot = 180.0f + angleXComponent * 40.0f
    pEntity.xRot = -angleYComponent * 20.0f
    pEntity.yHeadRot = pEntity.yRot
    if (pEntity is LivingEntity) pEntity.yHeadRotO = pEntity.yRot
    renderEntityInInventory(pGuiGraphics, pX, pY, pScale, quaternionF, quaternionF1, pEntity)
    pEntity.yRot = f3
    pEntity.xRot = f4
    pEntity.yHeadRot = f6
    if (pEntity is LivingEntity) {
        pEntity.yBodyRot = f2
        pEntity.yHeadRotO = f5
    }
}

fun renderEntityInInventory(
    pGuiGraphics: GuiGraphics,
    pX: Int,
    pY: Int,
    pScale: Double,
    pPose: Quaternionf,
    pCameraOrientation: Quaternionf,
    pEntity: Entity
) {
    pGuiGraphics.pose().pushPose()
    pGuiGraphics.pose().translate(pX.toDouble(), pY.toDouble(), 50.0)
    pGuiGraphics.pose().mulPoseMatrix(Matrix4f().scaling(pScale.toFloat(), pScale.toFloat(), (-pScale).toFloat()))
    pGuiGraphics.pose().mulPose(pPose)
    Lighting.setupForEntityInInventory()
    val entityRenderDispatcher = rgMinecraft.entityRenderDispatcher
    pCameraOrientation.conjugate()
    entityRenderDispatcher.overrideCameraOrientation(pCameraOrientation)

    entityRenderDispatcher.setRenderShadow(false)
    RenderSystem.runAsFancy {
        entityRenderDispatcher.render(
            pEntity,
            0.0,
            0.0,
            0.0,
            0.0f,
            1.0f,
            pGuiGraphics.pose(),
            pGuiGraphics.bufferSource(),
            15728880
        )
    }
    pGuiGraphics.flush()
    entityRenderDispatcher.setRenderShadow(true)
    pGuiGraphics.pose().popPose()
    Lighting.setupFor3DItems()
}