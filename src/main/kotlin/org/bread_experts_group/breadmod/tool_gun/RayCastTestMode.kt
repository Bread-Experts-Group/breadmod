package org.bread_experts_group.breadmod.tool_gun

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.debug.DebugRenderer
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.data_holders.common.KeyData
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.entities
import org.bread_experts_group.breadmod.util.getValue
import org.bread_experts_group.breadmod.util.putValue
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3

@ToolGunMode
@Suppress("unused")
class RayCastTestMode : IToolGunMode {
	var hitPos: Vec3 = Vec3.ZERO
	var blockHitPos: MutableBlockPos = MutableBlockPos()
	var direction: Direction = Direction.NORTH
	var showDebugData: Boolean = true

	override fun action(level: Level, player: Player, stack: ItemStack) {
		player.rayCast(100.0, blocks())?.let {
			this.hitPos = it.hitPosition
			this.blockHitPos.set(it.blockPosition)
			this.direction = it.hitSide
		}
	}

	override fun saveExtraData(tag: CompoundTag, level: Level) {
		tag.putValue("showDebug", this.showDebugData)
	}

	override fun loadExtraData(tag: CompoundTag, level: Level) {
		this.showDebugData = tag.getValue("showDebug")
	}

	override fun registerKeys(into: MutableMap<Int, KeyData>) {
		into[InputConstants.KEY_G] = KeyData(Component.literal("show debug")) { event, _, _, data ->
			if (this.isKeyboardPress(event) && localClient.screen == null) data.setValue(
				"showDebug",
				!this.showDebugData
			)
		}
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("raycast_test")

	override fun defineCustomRenderer(): IToolGunModeRenderer = Renderer(this)

	class Renderer(private val mode: RayCastTestMode) : IToolGunModeRenderer {
		override fun getMode(): IToolGunMode = this.mode

		private fun GuiGraphics.drawText(text: String, x: Int, y: Int) {
			this.drawString(this@Renderer.font, text, x, y, Color.WHITE, true)
		}

		override fun renderOverlayAdditions(
			guiGraphics: GuiGraphics,
			originX: Int,
			originY: Int,
			deltaTracker: DeltaTracker,
			stack: ItemStack,
			data: ToolGunData
		) {
			if (this.mode.showDebugData) {
				guiGraphics.pose().translate(0.0, 5.0, 0.0)
				guiGraphics.pose().scaleFlat(0.6f)
				guiGraphics.drawText("hit pos: ${this.mode.hitPos}", originX + 2, originY + 80)
				guiGraphics.drawText("hit blockpos: ${this.mode.blockHitPos}", originX + 2, originY + 90)
				guiGraphics.drawText(
					"local blockpos: ${(localClient.player ?: return).blockPosition()}",
					originX + 2,
					originY + 100
				)
				guiGraphics.drawText("hit direction: ${this.mode.direction}", originX + 2, originY + 110)
				guiGraphics.drawText(
					"local direction: ${(localClient.player ?: return).nearestViewDirection}",
					originX + 2,
					originY + 120
				)
			}
		}

		override fun renderLevelStageEvent(
			event: RenderLevelStageEvent,
			bufferSource: MultiBufferSource,
			player: LocalPlayer,
			data: ToolGunData
		) {
			if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) return
			val poseStack = event.poseStack
			val level = player.level()

			player.rayCast(50.0, blocks())?.let { blockRaycast ->
				blockRaycast.hit.getShape(level, blockRaycast.blockPosition).toAabbs().forEach {
					poseStack.pushPose()
					poseStack.offsetRenderToCameraPos(blockRaycast.blockPosition.toVec3(), event.camera, false)
					DebugRenderer.renderFilledBox(
						poseStack,
						bufferSource,
						it,
						0.7f,
						0.7f,
						1f,
						0.6f
					)
					poseStack.popPose()
				}
			}

			player.rayCast(50.0, entities())?.let {
				poseStack.pushPose()
				poseStack.offsetRenderToCameraPos(it.blockPosition.toVec3(), event.camera, false)
				DebugRenderer.renderFilledBox(
					poseStack,
					bufferSource,
					it.hit.boundingBox,
					0.7f,
					0.7f,
					1f,
					0.6f
				)
				poseStack.popPose()
			}
			data.getMode()
		}
	}
}