package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.buffer.render.RenderBuffer
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler
import org.bread_experts_group.breadmod.util.translateDirection
import org.jetbrains.annotations.ApiStatus.Internal
import org.joml.Matrix4f
import snownee.jade.overlay.DisplayHelper
import java.awt.Color
import java.math.BigDecimal
import java.util.function.Supplier
import kotlin.jvm.optionals.getOrNull
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

fun getFluidSpriteAndTint(fluid: Fluid, flowing: Boolean): Pair<TextureAtlasSprite?, Int> {
	val handler = IClientFluidTypeExtensions.of(fluid)
	val fluidSprite = if (flowing) handler.flowingTexture else handler.stillTexture
	val fluidSpriteApplied = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidSprite)
	return fluidSpriteApplied to handler.tintColor
}

private fun drawTextureWithMasking(
	matrix: Matrix4f,
	xCoord: Float,
	yCoord: Float,
	textureSprite: TextureAtlasSprite,
	maskTop: Float,
	maskRight: Float
) {
	val uMin = textureSprite.u0
	var uMax = textureSprite.u1
	val vMin = textureSprite.v0
	var vMax = textureSprite.v1
	uMax -= maskRight / 16.0f * (uMax - uMin)
	vMax -= maskTop / 16.0f * (vMax - vMin)
	val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
	buffer.addVertex(matrix, xCoord, yCoord + 16.0f, 0f).setUv(uMin, vMax)
	buffer.addVertex(matrix, xCoord + 16.0f - maskRight, yCoord + 16.0f, 0f).setUv(uMax, vMax)
	buffer.addVertex(matrix, xCoord + 16.0f - maskRight, yCoord + maskTop, 0f).setUv(uMax, vMin)
	buffer.addVertex(matrix, xCoord, yCoord + maskTop, 0f).setUv(uMin, vMin)
	BufferUploader.drawWithShader(buffer.buildOrThrow())
}

private fun setGLColorFromInt(color: Int) {
	val red = (color shr 16 and 255).toFloat() / 255.0f
	val green = (color shr 8 and 255).toFloat() / 255.0f
	val blue = (color and 255).toFloat() / 255.0f
	val alpha = (color shr 24 and 255).toFloat() / 255.0f
	RenderSystem.setShaderColor(red, green, blue, alpha)
}

fun GuiGraphics.drawTiledSprite(
	xPosition: Float,
	yPosition: Float,
	tiledWidth: Float,
	tiledHeight: Float,
	color: Int,
	scaledAmount: Float,
	sprite: TextureAtlasSprite
) {
	if (tiledWidth == 0.0f || tiledHeight == 0.0f || scaledAmount == 0.0f) return
	RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS)
	RenderSystem.setShader(Supplier(GameRenderer::getPositionTexShader))
	val matrix = this.pose().last().pose()
	setGLColorFromInt(color)
	RenderSystem.enableBlend()
	val xTileCount = (tiledWidth / 16.0f).toInt()
	val xRemainder = tiledWidth - (xTileCount * 16).toFloat()
	val yTileCount = (scaledAmount / 16.0f).toInt()
	val yRemainder = scaledAmount - (yTileCount * 16).toFloat()
	val yStart = yPosition + tiledHeight

	for (xTile in 0 .. xTileCount) {
		for (yTile in 0 .. yTileCount) {
			val width = if (xTile == xTileCount) xRemainder else 16.0f
			val height = if (yTile == yTileCount) yRemainder else 16.0f
			val x = xPosition + (xTile * 16).toFloat()
			val y = yStart - ((yTile + 1) * 16).toFloat()
			if (width > 0.0f && height > 0.0f) {
				val maskTop = 16.0f - height
				val maskRight = 16.0f - width
				drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight)
			}
		}
	}

	RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
	RenderSystem.disableBlend()
}

fun GuiGraphics.drawCenteredWordWrap(font: Font, text: FormattedText, x: Int, y: Int, lineWidth: Int, color: Int) {
	var yOffset = y
	for (charSequence: FormattedCharSequence in font.split(text, lineWidth)) {
		this.drawCenteredString(font, charSequence, x - font.width(text) / 2, yOffset, color)
		yOffset += 9
	}
}

fun GuiGraphics.renderFluid(
	x: Float, y: Float, width: Int, height: Int,
	tank: ExpansibleFluidHandler.ExpansibleTank,
	flowing: Boolean
) {
	if (tank.fluid.fluidType.isAir
		|| width <= 0
		|| height <= 0
		|| tank.capacity == null
		|| tank.amount == BigDecimal.ZERO
	) return
	val scaledAmount = min(tank.amount.divide(tank.capacity).toFloat() * height, height.toFloat())
	val (sprite, tint) = getFluidSpriteAndTint(tank.fluid, flowing)
	var color = tint
	if (sprite == null) {
		val maxY: Float = y + height
		if (color == -1) color = -0x55555556

		DisplayHelper.fill(
			this,
			x,
			maxY - scaledAmount,
			x + width,
			maxY,
			color
		)
	} else {
		this.drawTiledSprite(
			x,
			y,
			width.toFloat(),
			height.toFloat(),
			color,
			scaledAmount,
			sprite
		)
	}
}

/**
 * Fills in a square area with a border.
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

fun GuiGraphics.borderedFillPositioned(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	borderColor: Color,
	innerColor: Color
) {
	this.fill(RenderType.gui(), x, y, x + width, y + height, borderColor.rgb)
	this.fill(RenderType.gui(), x + 1, y + 1, x + width - 1, y + height - 1, innerColor.rgb)
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
	// the -0.5 is a temp workaround for the render being positioned in the corner instead of centered
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

internal fun renderTypeDebugLineStrip(): RenderType = RenderType.debugLineStrip(1.0)

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
	fabulous: Boolean = true,
	overrideRenderType: Boolean = false,
	renderTypeOverride: RenderType = RenderType.solid()
) {
	model.getRenderPasses(stack, fabulous).forEach { passes ->
		passes.getRenderTypes(stack, fabulous).forEach { renderType ->
			val buffer = if (fabulous) {
				ItemRenderer.getFoilBufferDirect(
					bufferSource,
					if (overrideRenderType) renderTypeOverride else renderType,
					true,
					stack.hasFoil()
				)
			} else
				ItemRenderer.getFoilBuffer(
					bufferSource,
					if (overrideRenderType) renderTypeOverride else renderType,
					true,
					stack.hasFoil()
				)
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

private const val TRANSLATE_OFFSET = 0.0001

/**
 * [posX], [posY], [posZ] translates the [PoseStack] on the facing side of the block. *(not required)*
 * ### translated [PoseStack] starts at the top left of the facing side
 *
 * @see translateDirection
 */
fun PoseStack.translateOnBlockSide(
	blockState: BlockState,
	direction: Direction? = null,
	posX: Double = 0.0,
	posY: Double = 0.0,
	posZ: Double = 0.0
) {
	var facing =
		blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).getOrNull() ?: blockState.getOptionalValue(
			BlockStateProperties.FACING
		).getOrNull()
		?: return
	if (direction != null) facing = translateDirection(facing, direction)

	this.mulPose(Axis.YN.rotationDegrees(facing.toYRot()))
	this.translate(posX, posY, posZ)
	when (facing) {
		Direction.NORTH -> this.translate(-1.0, 1.0, TRANSLATE_OFFSET)
		Direction.EAST -> this.translate(-1.0, 1.0, 1 + TRANSLATE_OFFSET)
		Direction.WEST -> this.translate(0.0, 1.0, TRANSLATE_OFFSET)
		Direction.SOUTH -> this.translate(0.0, 1.0, 1 + TRANSLATE_OFFSET)
		Direction.UP, Direction.DOWN -> {
			this.translate(-1.0, 1 + TRANSLATE_OFFSET, 0.0)
			this.mulPose(Axis.XN.rotationDegrees(90F))
		}
	}
}

val TRANSPARENT: Int = Color(0f, 0f, 0f, 0f).rgb

fun PoseStack.drawTextOnSide(
	fontRenderer: Font,
	component: Component,
	posX: Double,
	posY: Double,
	posZ: Double = 0.0,
	bufferSource: MultiBufferSource,
	blockState: BlockState,
	color: Int = Color.WHITE.rgb,
	backgroundColor: Int = TRANSPARENT,
	dropShadow: Boolean = false,
	direction: Direction? = null,
	scale: Float = 1f
) {
	this.pushPose()
	this.translateOnBlockSide(blockState, direction, posX, posY, posZ)
	this.mulPose(Axis.XN.rotationDegrees(180f))
	this.scaleFlat(scale)
	renderText(
		component.visualOrderText,
		color,
		backgroundColor,
		fontRenderer,
		this,
		bufferSource,
		dropShadow,
		15728880
	)
	this.popPose()
}

fun PoseStack.drawCenteredTextOnSide(
	fontRenderer: Font,
	component: Component,
	posX: Double,
	posY: Double,
	posZ: Double = 0.0,
	bufferSource: MultiBufferSource,
	blockState: BlockState,
	color: Int = Color.WHITE.rgb,
	backgroundColor: Int = TRANSPARENT,
	dropShadow: Boolean = false,
	direction: Direction? = null,
	scale: Float = 1f
) {
	this.pushPose()
	this.translateOnBlockSide(
		blockState, direction,
		posX - fontRenderer.width(component.visualOrderText) / 2,
		posY, posZ
	)
	this.mulPose(Axis.XN.rotationDegrees(180f))
	this.scaleFlat(scale)
	renderText(
		component.visualOrderText, color, backgroundColor, fontRenderer,
		this, bufferSource, dropShadow, 15728880
	)
	this.popPose()
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