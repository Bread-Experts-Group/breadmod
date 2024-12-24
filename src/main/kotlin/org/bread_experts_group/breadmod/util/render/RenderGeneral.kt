package org.bread_experts_group.breadmod.util.render

import com.mojang.authlib.GameProfile
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.PoseStack.Pose
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.color.item.ItemColor
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.block.model.BakedQuad
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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.XoroshiroRandomSource
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.model.MachTrailModel
import org.bread_experts_group.breadmod.registry.MachTrailData
import org.bread_experts_group.breadmod.registry.item.actual.armor.ChefHatItem
import org.jetbrains.annotations.ApiStatus.Internal
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import java.awt.Color
import java.lang.Math.clamp
import java.util.*
import kotlin.math.min

/**
 * Main minecraft instance
 */
@Internal
val localClient : Minecraft = Minecraft.getInstance()
internal typealias RenderBuffer =
		MutableList<Pair<MutableList<Float>, (MutableList<Float>, RenderLevelStageEvent) -> Boolean>>

internal var skyColorMixinActive : Boolean = false
internal var redness : Float = 1f
/**
 * Color getter for ItemStacks.
 */
val itemColor : ItemColor = ItemColor { stack : ItemStack, i : Int ->
	if (i > 0) -1 else DyedItemColor.getOrDefault(stack, Color.WHITE.rgb)
}
/**
 * A list of lambdas to call for rendering. If lambdas return true, they will be removed.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
val renderBuffer : RenderBuffer = mutableListOf()
/**
 * A map holding mach trail data for each player currently running with the chef hat.
 */
val machTrailMap : MutableMap<GameProfile, MachTrailData> = mutableMapOf()
// todo head rotations
/**
 * Renders a single instance of the mach trail behind the player.
 *
 * @author Logan McLean
 * @see MachTrailData
 * @see ChefHatItem
 */
fun renderMachTrail(playerProfile : GameProfile) {
	val playerId = playerProfile.id
	val level = localClient.level ?: return
	val player = level.getPlayerByUUID(playerId) ?: return
	val x = player.x
	val y = player.y
	val z = player.z
	val yRot = -player.rotationVector.y
	val machTrailModel = MachTrailModel(playerProfile, 0)

	renderBuffer.add(
		mutableListOf(
			0.8F,
			0F,
			1F
		) to { mutableList, renderStageEvent ->
			val currentOpacity = mutableList[0]
			val redValue = mutableList[1]
			val greenValue = mutableList[2]
			val poseStack = renderStageEvent.poseStack
			val camera = renderStageEvent.camera
			val partialTick = renderStageEvent.partialTick.realtimeDeltaTicks
			val currentColor = Color(
				redValue,
				greenValue,
				0.1f,
				clamp(currentOpacity, 0f, 1f)
			).rgb
			machTrailModel.currentColor = currentColor

			if (currentOpacity > 0) {
				poseStack.pushPose()
				poseStack.mulPose(Axis.YN.rotationDegrees(-yRot))
				poseStack.translate(0.0, 0.0, -0.3)
				poseStack.mulPose(Axis.YN.rotationDegrees(yRot))
				poseStack.initialTranslate(camera)
				poseStack.translate(x, y, z)
				poseStack.translate(0.0, 1.4, 0.0)
				poseStack.mulPose(Axis.XN.rotationDegrees(180f))
				poseStack.mulPose(Axis.YN.rotationDegrees(yRot))

				machTrailModel.render(poseStack)

				poseStack.popPose()

				mutableList[1] = clamp(redValue + 0.05f, 0f, 1f)
				mutableList[2] = clamp(greenValue - 0.05f, 0f, 1f)
				mutableList[0] = currentOpacity - 0.1f * partialTick
				false
			} else true
		})
}
/**
 * Draws a line from between [start] and [end], translated according to the current [LocalPlayer]'s position.
// * @see breadmod.network.clientbound.BeamPacket
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun addBeamTask(start : Vector3f, end : Vector3f, thickness : Float?) {
	val level = localClient.level
	val player = localClient.player
	val bufferSource = localClient.renderBuffers().bufferSource()

	renderBuffer.add(mutableListOf(1F) to { mutableList, renderStageEvent ->
		val currentOpacity = mutableList[0]
		val poseStack = renderStageEvent.poseStack
		val camera = renderStageEvent.camera
		val partialTick = renderStageEvent.partialTick.realtimeDeltaTicks

		if (level != null && currentOpacity > 0 && player != null) {
			poseStack.pushPose()
			poseStack.initialTranslate(camera)
			poseStack.translate(0.0, -1.0, 0.0)

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
			mutableList[0] = currentOpacity - 0.1f * partialTick
			false
		} else true
	})
}
// todo it's only showing green in the render area
fun GuiGraphics.renderFluid(
	x : Float, y : Float, width : Int, height : Int,
	fluid : Fluid, flowing : Boolean, direction : Direction = Direction.NORTH,
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
	val matrix4f : Matrix4f = this.pose().last().pose()
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
 * Scales the [PoseStack] uniformly on the X, Y, and Z axis.
 */
fun PoseStack.scaleFlat(scale : Float) : Unit = this.scale(scale, scale, scale)
/**
 * Translates the [PoseStack] of the added [renderBuffer] to the player's camera.
 * Used for initial model positions in-world.
 */
fun PoseStack.initialTranslate(camera : Camera) : Unit =
	this.translate(-camera.position.x, -camera.position.y, -camera.position.z)
/**
 * Draws scaled [text] in a Screen or Overlay
 */
fun drawScaledText(
	text : Component,
	poseStack : PoseStack,
	guiGraphics : GuiGraphics,
	x : Int,
	y : Int,
	color : Int,
	scale : Float,
	dropShadow : Boolean
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
	lastPose : PoseStack.Pose,
	buffer : MultiBufferSource,
	blockState : BlockState,
	packedLight : Int,
	packedOverlay : Int = NO_OVERLAY,
	renderType : RenderType = RenderType.solid(),
	red : Float = 1f,
	green : Float = 1f,
	blue : Float = 1f
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
		packedOverlay
	)
}

fun ModelBlockRenderer.renderBlockModel(
	lasePose : PoseStack.Pose,
	buffer : MultiBufferSource,
	blockEntity : BlockEntity,
	model : BakedModel,
	packedLight : Int,
	packedOverlay : Int,
	renderType : RenderType = RenderType.solid(),
	red : Float = 1f,
	green : Float = 1f,
	blue : Float = 1f
) : Unit = this.renderModel(
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

private val randomSource = XoroshiroRandomSource(42)
/**
 * Renders a [BakedModel] with color
 */
fun renderModel(
	pose : Pose,
	consumer : VertexConsumer,
	state : BlockState,
	model : BakedModel,
	packedLight : Int,
	packedOverlay : Int,
	red : Float = 1f,
	green : Float = 1f,
	blue : Float = 1f
) {
	Direction.entries.forEach {
		renderQuadList(
			pose, consumer,
			red, green, blue,
			model.getQuads(state, it, randomSource),
			packedLight, packedOverlay
		)
	}

	renderQuadList(
		pose, consumer,
		red, green, blue,
		model.getQuads(state, null, randomSource),
		packedLight, packedOverlay
	)
}

private fun renderQuadList(
	pose : Pose, consumer : VertexConsumer,
	red : Float, green : Float, blue : Float,
	quads : List<BakedQuad>,
	packedLight : Int, packedOverlay : Int
) {
	quads.forEach {
		consumer.putBulkData(
			pose, it,
			red, green, blue, 1.0f,
			packedLight, packedOverlay
		)
	}
}
/**
 * Renders a provided [stack] onto a [BlockEntityRenderer]
 */
fun ItemRenderer.renderStaticItem(
	stack : ItemStack,
	poseStack : PoseStack,
	buffer : MultiBufferSource,
	blockEntity : BlockEntity,
	packedLight : Int
) : Unit = this.renderStatic(
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
	model : BakedModel,
	stack : ItemStack,
	displayContext : ItemDisplayContext,
	leftHand : Boolean,
	poseStack : PoseStack,
	buffer : MultiBufferSource,
	packedOverlay : Int,
	packedLight : Int,
) {
	val renderType = ItemBlockRenderTypes.getRenderType(stack, false)
	val vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, renderType, true, stack.hasFoil())
	model.applyTransform(displayContext, poseStack, leftHand)
	this.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexConsumer)
}
/**
 * [ModelResourceLocation] with [modLocation] present.
 */
fun modelLocation(location : String) : ModelResourceLocation =
	ModelResourceLocation(modLocation(location), "standalone")
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
	component : FormattedCharSequence,
	color : Int,
	backgroundColor : Int,
	fontRenderer : Font,
	postStack : PoseStack,
	buffer : MultiBufferSource,
	dropShadow : Boolean,
	packedLight : Int
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