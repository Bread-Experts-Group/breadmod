package org.bread_experts_group.breadmod

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.commands.Commands
import net.minecraft.util.Mth.clamp
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.event.level.ChunkEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod.Companion.loadToolGunModes
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.WarOverlay
import org.bread_experts_group.breadmod.client.render.buffer.chunk.ChunkBuffer
import org.bread_experts_group.breadmod.client.render.buffer.render.MachTrailBufferTask.machTrailMap
import org.bread_experts_group.breadmod.client.render.buffer.render.RenderBuffer
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.redness
import org.bread_experts_group.breadmod.client.render.skyColorMixinActive
import org.bread_experts_group.breadmod.command.client.InternetRelayChatCommand
import org.bread_experts_group.breadmod.command.client.PingCommand
import org.bread_experts_group.breadmod.network.serverbound.PlaceItemInWorldPacket
import org.bread_experts_group.breadmod.registry.KeyMappings
import org.bread_experts_group.breadmod.registry.item.IKeyboardItem
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Suppress("unused")
@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
internal object ClientNeoForgeEventBus {
	@SubscribeEvent
	fun onChunkLoad(event: ChunkEvent.Load) {
		ChunkBuffer.handleLoad(event)
	}

	@SubscribeEvent
	fun onChunkUnload(event: ChunkEvent.Unload) {
		ChunkBuffer.handleUnload(event)
	}

	@SubscribeEvent
	fun onScreenRender(event: ScreenEvent.Render.Post) {
		ScreenBleedOverlay.renderBleed(event.guiGraphics)
	}

	@SubscribeEvent
	fun registerStageRender(event: RenderLevelStageEvent) {
		if (event.stage == RenderLevelStageEvent.Stage.AFTER_SKY && WarOverlay.timerActive) {
			val poseStack = event.poseStack
			val bufferBuilder =
				Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR)
			val millis = Util.getMillis()

			RenderSystem.setShader(GameRenderer::getPositionColorShader)
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
			RenderSystem.enableBlend()
			poseStack.pushPose()
			poseStack.mulPose(Axis.XP.rotationDegrees(-17f))
			val matrix = poseStack.last().pose()
			bufferBuilder.addVertex(matrix, 0f, 100f, 0f).setColor(0.9f, 0f, 0.1f, clamp(redness - 0.2f, 0f, 1f))

			for (j: Int in 0 .. 16) {
				val f1 = j * (Math.PI.toFloat() * 2f) / 16f
				val f2: Float = sin(f1)
				val f3: Float = cos(f1)
				bufferBuilder.addVertex(matrix, f2, -1f, -f3).setColor(0.9f, 0f, 0.1f, clamp(redness - 0.2f, 0f, 1f))
			}
			val shaderFogColor = RenderSystem.getShaderFogColor()
			RenderSystem.setShaderFogColor(
				shaderFogColor[0] + redness,
				shaderFogColor[1] - redness,
				shaderFogColor[2] - redness,
				1f
			)
			FogRenderer.setupFog(
				event.camera,
				FogRenderer.FogMode.FOG_SKY,
				256f,
				true,
				event.partialTick.realtimeDeltaTicks
			)
			FogRenderer.setupFog(
				event.camera,
				FogRenderer.FogMode.FOG_TERRAIN,
				max(256f, 32f),
				true,
				event.partialTick.realtimeDeltaTicks
			)

			redness = clamp((sin(millis.toFloat() / 1800) + 1) / 2, 0f, 1f)
			skyColorMixinActive = true

			BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
			RenderSystem.disableBlend()
			poseStack.popPose()
		} else if (!WarOverlay.timerActive) {
			redness = 0.0f
			skyColorMixinActive = false
		}

		RenderBuffer.handle(event)
	}

	@SubscribeEvent
	fun onMouseScroll(event: MouseScrollingEvent) {
		val player = localClient.player ?: return
		val stack = player.getItemInHand(player.usedItemHand)
		val item = stack.item
		if (item is IMouseItem) item.onMouseScroll(event, stack, player)
	}

	@SubscribeEvent
	fun onKeyboardPress(event: InputEvent.Key) {
		val player = localClient.player ?: return
		val level = localClient.level ?: return
		val stack = player.getItemInHand(player.usedItemHand)
		val item = stack.item
		if (item is IKeyboardItem) item.onKeyboardPress(event, stack, player)
		if (event.action == InputConstants.PRESS) {
			if (event.key == KeyMappings.placeItemKey.key.value) {
				val hitResult = localClient.hitResult as? BlockHitResult ?: return
				if (level.getBlockState(hitResult.blockPos).isAir) return
				PacketDistributor.sendToServer(PlaceItemInWorldPacket(hitResult.blockPos, hitResult.direction))
			}
		}
	}

	@SubscribeEvent
	fun onMouseInputPre(event: InputEvent.MouseButton.Pre) {
		val player = localClient.player ?: return
		val stack = getStackInPlayerHand(player)
		val item = stack.item
		if (item is IMouseItem) item.onMouseInputPre(event, stack, player)
	}

	@SubscribeEvent
	fun onMouseInputPost(event: InputEvent.MouseButton.Post) {
		val player = localClient.player ?: return
		val stack = getStackInPlayerHand(player)
		val item = stack.item
		if (item is IMouseItem) item.onMouseInputPost(event, stack, player)
	}

	@SubscribeEvent
	fun login(event: ClientPlayerNetworkEvent.LoggingIn) {
		loadToolGunModes()
	}

	@SubscribeEvent
	fun clientTick(event: ClientTickEvent.Pre) {
		if (machTrailMap.isNotEmpty()) {
			machTrailMap.forEach { (_, machTrailData) ->
				machTrailData.tick()
				if (!machTrailData.player.isSprinting || machTrailData.player.attackAnim > 0f) {
					machTrailData.machFourSound.kill = true
					localClient.soundManager.stop(machTrailData.machFourSound)
					machTrailMap.remove(machTrailData.playerProfile)
					return
				}
			}
		}
	}

	@SubscribeEvent
	fun registerClientCommands(event: RegisterClientCommandsEvent) {
		event.dispatcher.register(
			Commands.literal(BreadMod.ID)
				.then(InternetRelayChatCommand.register())
				.then(PingCommand.register())
		)
	}
}