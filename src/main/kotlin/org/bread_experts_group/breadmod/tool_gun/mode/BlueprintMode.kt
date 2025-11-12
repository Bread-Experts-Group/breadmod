package org.bread_experts_group.breadmod.tool_gun.mode

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.isZero
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3

// todo work on and add screen, structure previewing
@ToolGunMode
class BlueprintMode : IToolGunMode {
	val pos1: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()
	val pos2: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()
	val builders: MutableList<StructureBuilder> = mutableListOf()

	override fun actionPre(level: Level, player: Player, stack: ItemStack, usedHand: InteractionHand): Boolean {
		if (level.isClientSide) return false
		val (x, y, z) = player.position()
		val blockCast = player.rayCast(50.0, blocks()) ?: return false
		if (this.pos1.isZero()) {
			level.playSound(null, x, y, z, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.AMBIENT, 1f, 0.8f)
			this.pos1.set(blockCast.blockPosition)
			return false
		} else if (this.pos2.isZero()) {
			level.playSound(null, x, y, z, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.AMBIENT, 1f, 1f)
			this.pos2.set(blockCast.blockPosition)
			return false
		}
		return true
	}

	override fun action(
		level: Level,
		player: Player,
		stack: ItemStack,
		usedHand: InteractionHand
	) {
		val (x, y, z) = player.position()
		val blockCast = player.rayCast(50.0, blocks()) ?: return

		this.builders.add(
			StructureBuilder(
				buildList {
					BlockPos.betweenClosed(this@BlueprintMode.pos1, this@BlueprintMode.pos2).forEach { pos ->
						val state = level.getBlockState(pos)
						if (state.isAir) return@forEach
						val (x, y, z) = this@BlueprintMode.pos1
						val offset = BlockPos(pos.x - x, pos.y - y, pos.z - z)
						this.add(offset to BLOCK_STATE_REGISTRY.getId(state))
					}
				},
				blockCast.blockPosition.relative(blockCast.hitSide)
			)
		)

		this.pos1.set(0, 0, 0)
		this.pos2.set(0, 0, 0)
		level.playSound(null, x, y, z, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.AMBIENT, 1f, 0.5f)
	}

	override fun tick(level: Level, player: Player, stack: ItemStack, data: ToolGunData) {
		if (level.isClientSide) return
		this.builders.removeIf {
			it.tick(level)
			it.finished
		}
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("blueprint_mode")

	override fun defineCustomRenderer(): IToolGunModeRenderer = Renderer(this)

	class StructureBuilder(
		val blocks: List<Pair<BlockPos, Int>>,
		val targetPos: BlockPos
	) {
		private val ticker: LerpTicker<Int> = LerpTicker(0 to LerpTicker.LerpParams(clampMin = 0f, clampMax = 1f))
		var finished: Boolean = false
		private var index: Int = 0

		fun tick(level: Level) {
			val pair = this.blocks[this.index++]

			if (this.ticker.getRawValue(0) == 1f && this.index < this.blocks.size) {
				val state = BLOCK_STATE_REGISTRY.byId(pair.second) ?: return
				val (x, y, z) = this.targetPos.offset(pair.first)
				val sound = state.getSoundType(level, pair.first, null).placeSound
				level.setBlockAndUpdate(this.targetPos.offset(pair.first), state)
				logDebugInfo("${pair.first}, ${pair.second}")
				level.playSound(null, x.toDouble(), y.toDouble(), z.toDouble(), sound, SoundSource.BLOCKS, 1f, 1f)
				this.ticker.setParamPosition(0, 0f)
			} else if (this.index >= this.blocks.size) this.finished = true
			this.ticker.tickAllPositions()
		}
	}

	class Renderer(private val mode: BlueprintMode) : IToolGunModeRenderer {
		override fun getMode(): IToolGunMode = this.mode

		override fun renderOverlayAdditions(
			guiGraphics: GuiGraphics,
			originX: Int,
			originY: Int,
			deltaTracker: DeltaTracker,
			stack: ItemStack,
			data: ToolGunData
		) {
			val blockCast = (localClient.player ?: return).rayCast(50.0, blocks()) ?: return
			guiGraphics.drawString(this.font, "${this.mode.pos1.toVec3()}", originX, originY + 40, Color.WHITE)
			guiGraphics.drawString(this.font, "${blockCast.blockPosition}", originX, originY + 50, Color.WHITE)
		}

		override fun renderLevelStageEvent(
			event: RenderLevelStageEvent,
			bufferSource: MultiBufferSource,
			player: LocalPlayer,
			data: ToolGunData
		) {
			val isPos1Zero = this.mode.pos1.isZero()
			val isPos2Zero = this.mode.pos2.isZero()
			val poseStack = event.poseStack
			val bufferSource = localClient.renderBuffers().bufferSource()
			val camera = event.camera

			if (event.stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
				val blockCast = player.rayCast(50.0, blocks())
				val aabb = blockCast?.let { result ->
					val pos1 = if (isPos1Zero) result.blockPosition else this.mode.pos1
					val pos2 = if (isPos2Zero) result.blockPosition else this.mode.pos2
					AABB.of(BoundingBox.fromCorners(pos1, pos2))
				} ?: AABB.of(BoundingBox.fromCorners(this.mode.pos1, this.mode.pos2))

				poseStack.pushPose()
				poseStack.initialTranslate(camera)
				LevelRenderer.renderLineBox(
					poseStack,
					bufferSource.getBuffer(RenderType.lines()),
					aabb,
					0.4f,
					1f,
					0.4f,
					1f
				)
				blockCast?.let { result ->
					if (isPos1Zero || isPos2Zero) return@let
					LevelRenderer.renderLineBox(
						poseStack,
						bufferSource.getBuffer(RenderType.lines()),
						aabb.move(result.blockPosition.relative(result.hitSide).toVec3() - aabb.minPosition),
						1f,
						1f,
						1f,
						1f
					)
				}
				poseStack.popPose()
			}
		}
	}
}