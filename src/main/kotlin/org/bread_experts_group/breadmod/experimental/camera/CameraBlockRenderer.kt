package org.bread_experts_group.breadmod.experimental.camera

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.entity.block.BreadModBER
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.translateOnBlockSide
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.toVec3
import java.util.UUID

class CameraBlockRenderer(context: BlockEntityRendererProvider.Context) : BreadModBER(context) {
	val camera = DummyCamera()
	companion object {
		val textures: MutableMap<UUID, CameraTexture> = mutableMapOf()
		var textureCounter: Int = 0
	}

	override fun render(
		blockEntity: BreadModBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		this.runTheTestThatMakesTheGameCameraSnapToTheCamera(blockEntity, poseStack, bufferSource)
	}

	fun runTheTestThatMakesTheGameCameraSnapToTheCamera(
		blockEntity: BreadModBlockEntity,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource
	) {
		CameraStuff.CAMERA_TARGET.clear(Minecraft.ON_OSX)
		CameraStuff.CAMERA_TARGET.bindWrite(true)
		val level = blockEntity.level ?: return
		val (x, y, z) = blockEntity.blockPos.above().toVec3()
		if (this.camera.entity == null) this.camera.entity = Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level)
		this.camera.entity.setPos(x, y, z)
		this.camera.setPosition(x, y, z)

		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_LEVEL, { _, _ ->
			CameraStuff.renderLevel(localClient, CameraStuff.CAMERA_TARGET, this.camera)
			true
		})

		poseStack.translateOnBlockSide(blockEntity.blockState)
		drawQuad(poseStack, bufferSource, CameraStuff.CAMERA_RENDER_TYPE.apply(CameraStuff.CAMERA_TARGET))
		localClient.mainRenderTarget.bindWrite(true)
	}

	fun renderCameraTexture(blockEntity: BreadModBlockEntity, poseStack: PoseStack, bufferSource: MultiBufferSource) {
		val handler = blockEntity.getCapability(CameraStateHandler.BLOCK_VOID)
		val texture = Companion.textures.getOrPut(handler.id) {
			CameraTexture(
				blockEntity,
				modLocation("camera_texture_${Companion.textureCounter++}")
			)
		}

		poseStack.translateOnBlockSide(blockEntity.blockState)
		drawQuad(poseStack, bufferSource, RenderType.text(texture.location))
	}
}