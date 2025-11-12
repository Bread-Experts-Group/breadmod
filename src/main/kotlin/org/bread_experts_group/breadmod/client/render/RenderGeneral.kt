package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.color.item.ItemColor
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.Font.DisplayMode
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelManager
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.RandomSource
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.model.ExtraFaceData
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.shader.ModPostChains
import org.bread_experts_group.breadmod.registry.shader.ModVertexFormats
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.translateDirection
import org.jetbrains.annotations.ApiStatus.Internal
import org.joml.Matrix4f
import org.lwjgl.system.MemoryUtil
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
val floorTexture: ResourceLocation = checkerboardTexture(Color.WHITE, Color.BLACK, "white_black")
val arrowTexture: ResourceLocation = modLocation("textures/arrow.png")

fun Minecraft.gamePaused(): Boolean = (this.isPaused && this.isLocalServer)

/**
 * Color getter for ItemStacks.
 */
val itemColor: ItemColor = ItemColor { stack: ItemStack, tintIndex: Int ->
	if (tintIndex > 0) -1 else DyedItemColor.getOrDefault(stack, Color.WHITE)
}

fun getFluidSpriteAndTint(fluid: Fluid, flowing: Boolean): Pair<TextureAtlasSprite?, Int> {
	val handler = IClientFluidTypeExtensions.of(fluid)
	val fluidSprite = if (flowing) handler.flowingTexture else handler.stillTexture
	val fluidSpriteApplied = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidSprite)
	return fluidSpriteApplied to handler.tintColor
}

fun LocalPlayer.copy(): LocalPlayer {
	val player = object : LocalPlayer(
		localClient,
		this.level() as ClientLevel,
		this.connection,
		this.stats,
		this.recipeBook,
		this.isShiftKeyDown,
		this.isSprinting
	) {
		override fun shouldShowName(): Boolean = false
		override fun isCustomNameVisible(): Boolean = false
	}
	player.inventory.armor.forEachIndexed { index, _ ->
		player.inventory.armor[index] = this.inventory.armor[index]
	}
	player.attackAnim = this.attackAnim
	player.speed = this.speed
	player.yBodyRotO = this.yBodyRotO
	player.yBodyRot = this.yBodyRot
	player.yHeadRotO = this.yHeadRotO
	player.yHeadRot = this.yHeadRot
	player.swingTime = this.swingTime
	player.xRotO = this.xRotO
	player.xRot = this.xRot
	return player
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
	tank: ExtendedFluidHandler.Tank,
	flowing: Boolean
) {
	if (tank.fluid.fluidType.isAir
		|| width <= 0
		|| height <= 0
		|| tank.amount == BigDecimal.ZERO
	) return
	val scaledAmount = min(tank.amount.divide(tank.capacity).toFloat() * height, height.toFloat())
	val (sprite, tint) = getFluidSpriteAndTint(tank.fluid, flowing)
	var color = tint
	if (sprite == null) {
		val maxY: Float = y + height
		if (color == -1) color = -0x55555556

		this.fill(x, maxY - scaledAmount, x + width, maxY, color)
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
//}
/**
 * Float variant of GuiGraphics#fill.
 */
fun GuiGraphics.fill(
	minX: Float,
	minY: Float,
	maxX: Float,
	maxY: Float,
	color: Int,
	renderType: RenderType = RenderType.gui()
) {
	var newMinX = minX
	var newMaxX = maxX
	var newMinY = minY
	var newMaxY = maxY
	val pose = this.pose().last().pose()
	if (minX < maxX) {
		val i = newMinX
		newMinX = maxX
		newMaxX = i
	}
	if (minY < maxY) {
		val j = newMinY
		newMinY = newMaxY
		newMaxY = j
	}
	val consumer = this.bufferSource().getBuffer(renderType)
	consumer.addVertex(pose, newMinX, newMinY, 0f).setColor(color)
	consumer.addVertex(pose, newMinX, newMaxY, 0f).setColor(color)
	consumer.addVertex(pose, newMaxX, newMaxY, 0f).setColor(color)
	consumer.addVertex(pose, newMaxX, newMinY, 0f).setColor(color)
	this.flush()
}

/**
 * Fills in a square area with a border.
 */
fun GuiGraphics.borderedFill(
	minX: Int,
	minY: Int,
	maxX: Int,
	maxY: Int,
	borderColor: Int,
	innerColor: Int,
	renderType: RenderType = RenderType.gui()
) {
	this.fill(renderType, minX, minY, maxX, maxY, borderColor)
	this.fill(renderType, minX + 1, minY + 1, maxX - 1, maxY - 1, innerColor)
}

fun GuiGraphics.borderedFillPositioned(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	borderColor: Int,
	innerColor: Int,
	renderType: RenderType = RenderType.gui()
) {
	this.fill(renderType, x, y, x + width, y + height, borderColor)
	this.fill(renderType, x + 1, y + 1, x + width - 1, y + height - 1, innerColor)
}

fun GuiGraphics.borderedFillPositioned(
	x: Float,
	y: Float,
	width: Float,
	height: Int,
	borderColor: Int,
	innerColor: Int,
	borderThickness: Float = 1f,
	renderType: RenderType = RenderType.gui()
) {
	this.fill(x, y, x + width, y + height, borderColor, renderType)
	this.fill(
		x + borderThickness,
		y + borderThickness,
		x + width - borderThickness,
		y + height - borderThickness,
		innerColor,
		renderType
	)
}

fun GuiGraphics.fillPositioned(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	color: Int,
	renderType: RenderType = RenderType.gui()
): Unit = this.fill(renderType, x, y, x + width, y + height, color)

fun GuiGraphics.fillPositioned(
	x: Float,
	y: Float,
	width: Float,
	height: Float,
	color: Int,
	renderType: RenderType = RenderType.gui()
): Unit = this.fill(x, y, width, height, color, renderType)

fun GuiGraphics.enablePositionedScissor(
	x: Int,
	y: Int,
	width: Int,
	height: Int
): Unit = this.enableScissor(x, y, x + width, y + height)

fun GuiGraphics.flushAndFinishScissor() {
	this.flush()
	this.disableScissor()
}

/**
 * If your screen implements [ContainerWidget], use this method to redirect focus from the container to its children.
 */
fun Screen.redirectFocusFromContainerWidgets(mouseX: Double, mouseY: Double, button: Int): Boolean {
	this.children().any { child ->
		if (child.mouseClicked(mouseX, mouseY, button)) {
			if (child !is ContainerWidget<*>) this.focused = child
			if (button == 0) this.isDragging = true
			return true
		} else false
	}

	return false
}

fun VertexConsumer.getBufferBuilder(): BufferBuilder =
	this as? BufferBuilder ?: throw ClassCastException("this vertex consumer is not BufferBuilder!")

fun VertexConsumer.setDirection(direction: Vec2): VertexConsumer {
	val builder = this.getBufferBuilder()
	val i = builder.beginElement(ModVertexFormats.DIRECTION_VERTEX_ELEMENT)
	if (i != -1L) {
		MemoryUtil.memPutFloat(i, direction.x)
		MemoryUtil.memPutFloat(i + 4L, direction.y)
	}
	return this
}

fun VertexConsumer.setSpeed(speed: Float): VertexConsumer {
	val builder = this.getBufferBuilder()
	val i = builder.beginElement(ModVertexFormats.SPEED_VERTEX_ELEMENT)
	if (i != -1L) MemoryUtil.memPutFloat(i, speed)
	return this
}

/**
 * Scales the [PoseStack] uniformly on the X, Y, and Z axis.
 */
fun PoseStack.scaleFlat(scale: Float): Unit = this.scale(scale, scale, scale)
fun PoseStack.translate(x: Int, y: Int, z: Int): Unit = this.translate(x.toFloat(), y.toFloat(), z.toFloat())
fun PoseStack.translate(vec3: Vec3): Unit = this.translate(vec3.x, vec3.y, vec3.z)
fun PoseStack.translate(vec3i: Vec3i): Unit = this.translate(vec3i.x, vec3i.y, vec3i.z)

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
fun PoseStack.offsetRenderToCameraPos(pos: Vec3, camera: Camera, workaround: Boolean = true) {
	val offset = pos.subtract(camera.position)
	val fix = if (workaround) 0.5 else 0.0
	// the -0.5 is a temp workaround for the render being positioned in the corner instead of centered
	this.translate(offset.x - fix, offset.y, offset.z - fix)
}

fun PoseStack.rotate(axis: Axis, degrees: Float): Unit = this.mulPose(axis.rotationDegrees(degrees))

/**
 * Translates this [PoseStack] and divides it by 16. Used for positioning models onto blocks.
 */
fun PoseStack.translateDiv16(x: Double, y: Double, z: Double): Unit =
	this.translate(x / 16, y / 16, z / 16)

fun PoseStack.translateDiv16(x: Float, y: Float, z: Float): Unit =
	this.translateDiv16(x.toDouble(), y.toDouble(), z.toDouble())

fun PoseStack.translateDiv16(vec: Vec3): Unit =
	this.translateDiv16(vec.x, vec.y, vec.z)

fun Block.textureLocation(): ResourceLocation = BuiltInRegistries.BLOCK.getKey(this).withPrefix("block/")
fun Item.textureLocation(): ResourceLocation = BuiltInRegistries.ITEM.getKey(this).withPrefix("item/")

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
		ModelData.builder()
			.with(ModelProperty(), ExtraFaceData(Color.WHITE, LightTexture.block(packedLight), 0, true)).build(),
		renderType
	)
}

fun ModelBlockRenderer.tessellateModel(
	blockEntity: BlockEntity,
	model: BakedModel,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	randomSource: RandomSource,
	packedOverlay: Int,
	modelData: ModelData
) {
	model.getRenderTypes(blockEntity.blockState, randomSource, modelData).forEach { renderType ->
		this.tesselateWithAO(
			blockEntity.level ?: return,
			model,
			blockEntity.blockState,
			blockEntity.blockPos,
			poseStack,
			buffer.getBuffer(renderType),
			true,
			randomSource,
			64,
			packedOverlay,
			modelData,
			renderType
		)
	}
}

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

fun ParticleOptions.toParticle(): Particle? =
	localClient.particleEngine.createParticle(this, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

/**
 * Convenience function for getting a model with just a string.
 */
fun ModelManager.getModel(location: String): BakedModel =
	this.getModel(ModelResourceLocation(modLocation(location), "standalone"))

fun Minecraft.getModel(location: String): BakedModel = this.modelManager.getModel(location)

private fun Font.drawAdjustableShadowText(
	text: FormattedCharSequence,
	x: Float,
	y: Float,
	color: Int,
	dropShadow: Boolean,
	matrix: Matrix4f,
	buffer: MultiBufferSource,
	displayMode: DisplayMode,
	backgroundColor: Int,
	packedLightCoords: Int,
	dropShadowOffset: Float = 0.03f
) {
	val adjustedColor = if ((color and -67108864) == 0) color or -0x1000000 else color
	val matrix4f = Matrix4f(matrix)
	if (dropShadow) {
		this.renderText(
			text,
			x,
			y,
			adjustedColor,
			true,
			matrix,
			buffer,
			displayMode,
			backgroundColor,
			packedLightCoords
		)
		matrix4f.translate(0f, 0f, dropShadowOffset)
	}

	this.renderText(text, x, y, adjustedColor, false, matrix4f, buffer, displayMode, backgroundColor, packedLightCoords)
}

/**
 * Renders a given [Component] onto a [BlockEntityWithoutLevelRenderer] or [BlockEntityRenderer]
 *
 * @param component The text as a [Component.literal] or [Component.translatable]
 * to be rendered onto the target block or item.
 * @param color The primary text color as an integer.
 * @param backgroundColor Secondary text color as an integer, applies to the background
 * @param poseStack Positions the text onto the target block or item
 * @param dropShadow draws a drop shadow behind the text
 *
 * @see Font.drawInBatch
 * @since 0.0.1
 */
fun Font.renderText(
	component: FormattedCharSequence,
	color: Int,
	backgroundColor: Int,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	dropShadow: Boolean,
	packedLight: Int,
	dropShadowOffset: Float = 0.03f
): Unit = this.drawAdjustableShadowText(
	component,
	0f,
	0f,
	color,
	dropShadow,
	poseStack.last().pose(),
	buffer,
	DisplayMode.NORMAL,
	backgroundColor,
	packedLight,
	dropShadowOffset
)

fun Font.renderText(
	component: FormattedCharSequence,
	color: Int,
	backgroundColor: Int,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	dropShadow: Boolean,
	packedLight: Int,
	x: Float,
	y: Float,
	dropShadowOffset: Float = 0.03f
): Unit = this.drawAdjustableShadowText(
	component,
	x,
	y,
	color,
	dropShadow,
	poseStack.last().pose(),
	buffer,
	DisplayMode.NORMAL,
	backgroundColor,
	packedLight,
	dropShadowOffset
)

fun Font.renderTextNoBg(
	component: FormattedCharSequence,
	color: Int,
	poseStack: PoseStack,
	buffer: MultiBufferSource,
	dropShadow: Boolean,
	packedLight: Int,
	dropShadowOffset: Float
): Unit =
	this.renderText(component, color, Color.color(a = 0), poseStack, buffer, dropShadow, packedLight, dropShadowOffset)

private const val TRANSLATE_OFFSET: Double = 0.0001

/**
 * [posX], [posY], [posZ] translates the [PoseStack] on the facing side of the block. *(not required)*
 * ### translated [PoseStack] starts at the top left of the facing side
 *
 * @see translateDirection
 */
// todo check to make sure the rotated PoseStack aligns and rotates properly. horizontal directions are fine but UP and DOWN is iffy...
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
		Direction.UP -> {
			this.translate(-1.0, 1 + TRANSLATE_OFFSET, 0.0)
			this.mulPose(Axis.XN.rotationDegrees(90F))
		}
		Direction.DOWN -> {
			this.translate(-1.0, 0 - TRANSLATE_OFFSET, 0.0)
			this.mulPose(Axis.XP.rotationDegrees(90f))
		}
	}
}

// todo proper text rotation on up and down axis.
fun PoseStack.drawTextOnBlockSide(
	fontRenderer: Font,
	component: Component,
	posX: Double,
	posY: Double,
	posZ: Double = 0.0,
	bufferSource: MultiBufferSource,
	blockState: BlockState,
	color: Int = Color.WHITE,
	backgroundColor: Int = 0,
	dropShadow: Boolean = false,
	direction: Direction? = null,
	scale: Float = 1f,
	dropShadowOffset: Float = 0.03f,
	packedLight: Int = FULL_BRIGHT
) {
	this.pushPose()
	this.translateOnBlockSide(blockState, direction, posX, posY, posZ)
	this.mulPose(Axis.XN.rotationDegrees(180f))
	this.scaleFlat(scale)
	fontRenderer.renderText(
		component.visualOrderText,
		color,
		backgroundColor,
		this,
		bufferSource,
		dropShadow,
		packedLight,
		dropShadowOffset
	)
	this.popPose()
}

fun solidColorTexture(color: Int, id: String, width: Int = 16, height: Int = 16): ResourceLocation {
	val native = NativeImage(width, height, false)
	native.fillRect(0, 0, width, height, color)
	return localClient.textureManager.register("bm_color_tex_${color}_$id", DynamicTexture(native))
}

fun solidColorTexture(r: Int, g: Int, b: Int, id: String, width: Int = 16, height: Int = 16): ResourceLocation =
	solidColorTexture(Color.color(r, g, b), id, width, height)

fun checkerboardTexture(
	firstColor: Int,
	secondColor: Int,
	id: String,
	width: Int = 16,
	height: Int = 16
): ResourceLocation {
	val native = NativeImage(width, height, false)
	native.fillRect(0, 0, width / 2, height / 2, firstColor)
	native.fillRect(width / 2, 0, width / 2, height / 2, secondColor)
	native.fillRect(0, height / 2, width / 2, height / 2, secondColor)
	native.fillRect(width / 2, height / 2, width / 2, height / 2, firstColor)
	return localClient.textureManager.register("bm_color_tex_${firstColor}_${secondColor}_$id", DynamicTexture(native))
}

fun renderBloom(deltaTracker: DeltaTracker) {
	if (ModPostChains.ready) {
		ModPostChains.bloom.process(deltaTracker.gameTimeDeltaTicks)
		ModPostChains.bloomEmissiveTarget.clear(Minecraft.ON_OSX)
		localClient.mainRenderTarget.bindWrite(false)
		RenderSystem.clear(256, Minecraft.ON_OSX)
	}
}