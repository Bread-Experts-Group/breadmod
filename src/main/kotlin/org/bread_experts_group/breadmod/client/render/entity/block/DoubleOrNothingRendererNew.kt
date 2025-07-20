package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.solidColorTexture
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntityNew
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.Color
import org.joml.Vector3f
import org.lwjgl.system.MemoryUtil

class DoubleOrNothingRendererNew(private val context: Context) : BlockEntityRenderer<DoubleOrNothingBlockEntityNew> {
	// Background vertex positions
	private val vertexes: Array<Vector3f> = arrayOf(
		Vector3f(0f, 0f, 0f), // top left
		Vector3f(1f, 0f, 0f), // top right
		Vector3f(0f, -1f, 0f), // bottom left
		Vector3f(1f, -1f, 0f) // bottom right
	)

	// Colors
	private val colors: IntArray = intArrayOf(
		Color.color(255, 57, 0),
		Color.color(255, 79, 59),
		Color.color(255, 59, 106),
		Color.color(255, 59, 135),
		Color.color(255, 59, 153),
		Color.color(232, 70, 170),
		Color.color(198, 53, 173),
		Color.color(198, 53, 201),
		Color.color(164, 73, 227),
		Color.BLACK
	)
	private val nothingColor: Int = Color.color(128)
	private val good1Color: Int = Color.color(102, 204, 102)
	private val good2Color: Int = Color.color(52, 240)
	private val jackpotColor: Int = Color.color(255, 214, 38)
	private val jackpotBGColor: Int = Color.color(255, 207)

	// Textures
	private val backgroundTexture: ResourceLocation = modLocation("textures/block/double_or_nothing/background.png")
	private val blockhead: ResourceLocation = modLocation("textures/tool_gun/gui/blockhead.png")
	private val colorableTexture: ResourceLocation = solidColorTexture(Color.WHITE, "double_or_nothing", 14, 28)
	private val blueScreenTexture: ResourceLocation =
		modLocation("textures/block/double_or_nothing/background_bluescreen.png")

	override fun render(
		blockEntity: DoubleOrNothingBlockEntityNew,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val half = blockEntity.blockState.getValue(ModBlockStateProperties.TRIPLE_BLOCK_HALF)
		if (half != ModBlockStateProperties.TripleBlockHalf.LOWER) return
		poseStack.pushPose()
		poseStack.translateOnBlockSide(blockEntity.blockState)
		drawQuad(
			poseStack,
			bufferSource,
			ModRenderType.rainbow(),
			Color.WHITE,
			this.vertexes[0],
			this.vertexes[1],
			this.vertexes[2],
			this.vertexes[3]
		) { consumer -> consumer.setSpeed(1000f).setDirection(Vec2(1f, -1f)) }
		poseStack.popPose()
	}

	private fun VertexConsumer.setSpeed(speed: Float): VertexConsumer {
		val builder = this as? BufferBuilder ?: throw AssertionError("current consumer is not BufferBuilder!")
		val i = builder.beginElement(ModRenderType.SPEED_VERTEX_ELEMENT)
		if (i != -1L) MemoryUtil.memPutFloat(i, speed)
		return this
	}

	private fun VertexConsumer.setDirection(direction: Vec2): VertexConsumer {
		val builder = this as? BufferBuilder ?: throw AssertionError("current consumer is not BufferBuilder!")
		val i = builder.beginElement(ModRenderType.DIRECTION_VERTEX_ELEMENT)
		if (i != -1L) {
			MemoryUtil.memPutFloat(i, direction.x)
			MemoryUtil.memPutFloat(i + 4L, direction.y)
		}
		return this
	}
}