package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.color.item.ItemColor
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemDisplayContext
import org.bread_experts_group.breadmod.client.render.buffer.render.RenderBuffer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.jetbrains.annotations.ApiStatus.Internal
import org.joml.Matrix4f
import org.joml.Vector2f
import java.awt.Color
import java.util.*
import kotlin.math.min

/**
 * Main minecraft instance
 */
@Internal
val localClient: Minecraft = Minecraft.getInstance()
internal var skyColorMixinActive: Boolean = false
internal var redness: Float = 1f

/**
 * Color getter for ItemStacks.
 */
val itemColor: ItemColor = ItemColor { stack: ItemStack, i: Int ->
	if (i > 0) -1 else DyedItemColor.getOrDefault(stack, Color.WHITE.rgb)
}

// todo it's only showing green in the render area
fun GuiGraphics.renderFluid(
	x: Float, y: Float, width: Int, height: Int,
	fluid: Fluid, flowing: Boolean, direction: Direction = Direction.NORTH,
) {
	val atlas = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
	val ext = IClientFluidTypeExtensions.of(fluid)
	val spriteDiff = if (flowing) {
		val stillWidth = atlas.apply(ext.stillTexture).contents().width().toFloat()
		atlas.apply(ext.flowingTexture).let {
			val flowingWidth =
				it.contents().width(); it to if (flowingWidth > stillWidth) (stillWidth / flowingWidth) else 1F
		}
	} else atlas.apply(ext.stillTexture) to 1F
	val sprite = spriteDiff.first
	val colors = FloatArray(4).also(Color(ext.tintColor)::getComponents)
	val matrix4f: Matrix4f = this.pose().last().pose()
	RenderSystem.setShaderTexture(0, sprite.atlasLocation())
	RenderSystem.setShader(GameRenderer::getRendertypeGuiShader)
	RenderSystem.enableBlend()
	val bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
	val pX2 = x + width
	var remainingFluid = height
	while (remainingFluid > 0) {
		// TODO: Make pY the TOP LEFT, instead of BOTTOM LEFT
		val lpY = (y - remainingFluid)
		val lpY2 = lpY + min(remainingFluid, width)
		// N  // E  // S  // W
		// AB // CA // DC // BD
		// CD // DB // BA // AC
		// (pX, lpY2), (pX2, lpY2)
		// (pX, lpY ), (pX2, lpY )
		val rotated = listOf(Vector2f(x, lpY), Vector2f(x, lpY2), Vector2f(pX2, lpY2), Vector2f(pX2, lpY)).also {
			Collections.rotate(
				it,
				when (direction) {
					Direction.EAST -> 1; Direction.SOUTH -> 2; Direction.WEST -> 3; else -> 0
				}
			)
		}
		val dv1 = (sprite.v1 - sprite.v0)
		val v1 =
			if (remainingFluid < width) (sprite.v0 + ((dv1 / width) * remainingFluid))
			else (sprite.v0 + (dv1 * spriteDiff.second))
		val u1 = sprite.u0 + ((sprite.u1 - sprite.u0) * spriteDiff.second)
		fun VertexConsumer.color() = this.setColor(colors[0], colors[1], colors[2], colors[3])
		rotated[0].let { bufferBuilder.addVertex(matrix4f, it.x, it.y, 0F).color().setUv(u1, v1) }
		rotated[1].let { bufferBuilder.addVertex(matrix4f, it.x, it.y, 0F).color().setUv(u1, sprite.v0) }
		rotated[2].let { bufferBuilder.addVertex(matrix4f, it.x, it.y, 0F).color().setUv(sprite.u0, sprite.v0) }
		rotated[3].let { bufferBuilder.addVertex(matrix4f, it.x, it.y, 0F).color().setUv(sprite.u0, v1) }

		remainingFluid -= width
	}

	BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
	RenderSystem.disableBlend()
}

/**
 * Fills in a square area with border.
 */
fun GuiGraphics.borderedFill(
	renderType: RenderType,
	minX: Int,
	minY: Int,
	maxX: Int,
	maxY: Int,
	borderColor: Int,
	innerColor: Int
) {
	this.fill(renderType, minX, minY, maxX, maxY, borderColor)
	this.fill(renderType, minX + 1, minY + 1, maxX - 1, maxY - 1, innerColor)
}

/**
 * Scales the [PoseStack] uniformly on the X, Y, and Z axis.
 */
fun PoseStack.scaleFlat(scale: Float): Unit = this.scale(scale, scale, scale)

/**
 * Translates the [PoseStack] of the added [RenderBuffer] to the player's camera.
 * Used for initial model positions in-world.
 *
 * @see offsetRenderToCameraPos
 */
fun PoseStack.initialTranslate(camera: Camera): Unit =
	this.translate(-camera.position.x, -camera.position.y, -camera.position.z)

/**
 * Alternative method for positioning the [PoseStack] of the added [RenderBuffer] to the camera pos.
 *
 * Offsets the current [PoseStack] to [pos] by subtracting [pos] from the [camera] position.
 * @see initialTranslate
 */
fun PoseStack.offsetRenderToCameraPos(pos: Vec3, camera: Camera) {
	val offset = pos.subtract(camera.position)
	// the -0.5 is a temp workaround for the render being positioned at the corner instead of centered
	this.translate(offset.x - 0.5, offset.y, offset.z - 0.5)
}

/**
 * Draws scaled [text] in a Screen or Overlay
 */
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
		localClient.font,
		text,
		x,
		y,
		color,
		dropShadow
	)
	poseStack.scaleFlat(1f)
}

/**
 * Renders a specified [BlockState] onto a [BlockEntityWithoutLevelRenderer] or [BlockEntityRenderer]
 */
fun ModelBlockRenderer.renderBlockModel(
	lastPose: PoseStack.Pose,
	buffer: MultiBufferSource,
	blockState: BlockState,
	packedLight: Int,
	packedOverlay: Int = NO_OVERLAY,
	renderType: RenderType = Sheets.solidBlockSheet(),
	red: Float = 1f,
	green: Float = 1f,
	blue: Float = 1f
) {
	val blockModel = localClient.modelManager.blockModelShaper.getBlockModel(blockState)
	this.renderModel(
		lastPose,
		buffer.getBuffer(renderType),
		blockState,
		blockModel,
		red,
		green,
		blue,
		packedLight,
		packedOverlay,
		ModelData.builder().with(ModelProperty(), ExtraFaceData(Color.WHITE.rgb, 0, 0, true)).build(),
		renderType
	)
}

fun ModelBlockRenderer.renderBlockModel(
	lasePose: PoseStack.Pose,
	buffer: MultiBufferSource,
	blockEntity: BlockEntity,
	model: BakedModel,
	packedLight: Int,
	packedOverlay: Int,
	renderType: RenderType = RenderType.solid(),
	red: Float = 1f,
	green: Float = 1f,
	blue: Float = 1f
): Unit = this.renderModel(
	lasePose,
	buffer.getBuffer(renderType),
	blockEntity.blockState,
	model,
	red,
	green,
	blue,
	packedLight,
	packedOverlay,
	ModelData.EMPTY,
	renderType
)

/**
 * Renders a provided [stack] onto a [BlockEntityRenderer]
 */
fun ItemRenderer.renderStaticItem(
	stack: ItemStack,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	blockEntity: BlockEntity,
	packedLight: Int
): Unit = this.renderStatic(
	stack,
	ItemDisplayContext.FIXED,
	packedLight,
	NO_OVERLAY,
	poseStack,
	buffer,
	blockEntity.level,
	1
)

/**
 * Renders a provided [model] (as an item model) onto this [BlockEntityWithoutLevelRenderer]
 */
fun ItemRenderer.renderItemModel(
	model: BakedModel,
	stack: ItemStack,
	displayContext: ItemDisplayContext,
	poseStack: PoseStack,
	bufferSource: MultiBufferSource,
	packedOverlay: Int,
	packedLight: Int,
	fabulous: Boolean = true
) {
	model.getRenderPasses(stack, fabulous).forEach { passes ->
		passes.getRenderTypes(stack, fabulous).forEach { renderType ->
			val buffer = if (fabulous) {
				ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, stack.hasFoil())
			} else
				ItemRenderer.getFoilBuffer(bufferSource, renderType, true, stack.hasFoil())
			passes.applyTransform(
				displayContext,
				poseStack,
				displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
			)
			this.renderModelLists(passes, stack, packedLight, packedOverlay, poseStack, buffer)
		}
	}
}

/**
 * [ModelResourceLocation] with [modLocation] present.
 */
fun modelLocation(location: String): ModelResourceLocation =
	ModelResourceLocation(modLocation(location), "standalone")

/**
 * Renders a given [Component] onto a [BlockEntityWithoutLevelRenderer] or [BlockEntityRenderer]
 *
 * @param component The text as a [Component.literal] or [Component.translatable]
 * to be rendered onto the target block or item.
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