package org.bread_experts_group.breadmod.tool_gun.mode

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.KeyData
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget
import org.bread_experts_group.breadmod.tool_gun.gui.screen.BlueprintLoadScreen
import org.bread_experts_group.breadmod.tool_gun.gui.screen.BlueprintSaveScreen
import org.bread_experts_group.breadmod.tool_gun.mode.blueprint.StructureBuilder
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.getValue
import org.bread_experts_group.breadmod.util.isNotZero
import org.bread_experts_group.breadmod.util.isZero
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.putValue
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3
import java.nio.file.Path
import kotlin.io.path.Path

// todo work on and add screen, structure previewing, proper positioning
@ToolGunMode
class BlueprintMode : IToolGunMode {
	companion object {
		val BLUEPRINT_PATH: Path = Path("tool_gun/blueprints")
		val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
	}

	val pos1: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()
	val pos2: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()
	val builders: MutableList<StructureBuilder> = mutableListOf()
	val capturedBlocks: MutableList<Pair<BlockPos, BlockState>> = mutableListOf()
	var isPlacing: Boolean = false

	fun getBounding(): BoundingBox = BoundingBox.fromCorners(BlockPos.ZERO, this.pos2 - this.pos1)

	override fun actionPre(
		level: Level,
		player: Player,
		stack: ItemStack,
		usedHand: InteractionHand,
		data: ToolGunData
	): Boolean {
		if (this.isPlacing) return true
		if (!level.isClientSide) return false
		val (x, y, z) = player.position()
		val blockCast = player.rayCast(50.0, blocks()) ?: return false
		return if (this.pos1.isZero()) {
			level.playSound(player, x, y, z, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.AMBIENT, 1f, 0.8f)
			this.pos1.set(blockCast.blockPosition)
			false
		} else if (this.pos2.isZero()) {
			level.playSound(player, x, y, z, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.AMBIENT, 1f, 0.9f)
			this.pos2.set(blockCast.blockPosition)
			false
		} else if (this.pos1.isNotZero() && this.pos2.isNotZero()) {
			level.playSound(player, x, y, z, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.AMBIENT, 1f, 1f)
			this.capturedBlocks.addAll(this.gatherBlocks(level))
			localClient.setScreen(BlueprintSaveScreen(this.getBounding(), this.capturedBlocks, this))
			false
		} else true
	}

	override fun saveExtraData(tag: CompoundTag, level: Level) {
		tag.putValue("isPlacing", this.isPlacing)
	}

	override fun loadExtraData(tag: CompoundTag, level: Level) {
		this.isPlacing = tag.getValue("isPlacing")
	}

	override fun action(
		level: Level,
		player: Player,
		stack: ItemStack,
		usedHand: InteractionHand,
		data: ToolGunData
	) {
//		this.deserializeBlocks("test").forEach { logDebugInfo(it) }
		/*if (level.isClientSide) return
		val (x, y, z) = player.position()
		val blockCast = player.rayCast(50.0, blocks()) ?: return
		// todo block entity data
		this.builders.add(
			StructureBuilder(
				level,
				this.gatherBlocks(level).iterator(),
				blockCast.blockPosition.relative(blockCast.hitSide)
			)
		)

		this.pos1.set(0, 0, 0)
		this.pos2.set(0, 0, 0)
		level.playSound(null, x, y, z, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.AMBIENT, 1f, 1.2f)*/
	}

	fun gatherBlocks(level: Level): List<Pair<BlockPos, BlockState>> =
		buildList {
			BlockPos.betweenClosed(this@BlueprintMode.pos1, this@BlueprintMode.pos2).forEach { pos ->
				val state = level.getBlockState(pos)
				if (state.isAir) return@forEach
				val (x, y, z) = this@BlueprintMode.pos1
				// todo use BlockPos#offset?
				val offset = BlockPos(pos.x - x, pos.y - y, pos.z - z)
				this.add(offset to state)
			}
		}/*.sortedBy { it.first.y }*/

	override fun registerKeys(into: MutableMap<Int, KeyData>) {
		into[InputConstants.KEY_F] = KeyData(Component.literal("screen")) { _, _, _, _ ->
			if (localClient.screen == null) localClient.setScreen(BlueprintLoadScreen())
		}
		into[InputConstants.KEY_G] = KeyData(Component.literal("isPlacing")) { event, _, _, data ->
			if (event.action == InputConstants.PRESS) data.setValue("isPlacing", !this.isPlacing)
		}
	}

	override fun tick(level: Level, player: Player, stack: ItemStack, data: ToolGunData) {
		if (level.isClientSide) return
		this.builders.removeIf { !it.blocks.hasNext() }
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("blueprint_mode")

	override fun defineCustomRenderer(): IToolGunModeRenderer = BlueprintRenderer(this)

	class BlueprintRenderer(private val mode: BlueprintMode) : IToolGunModeRenderer {
		override fun getMode(): IToolGunMode = this.mode
		override fun buildModeWidget(): ModeWidget.Builder = super.buildModeWidget().icon(Items.PAPER)

		override fun renderScreenStage(
			stack: ItemStack,
			displayContext: ItemDisplayContext,
			poseStack: PoseStack,
			buffer: MultiBufferSource,
			packedLight: Int,
			packedOverlay: Int
		) {
			val mode = if (this.mode.isPlacing) "Placing" else "Copying"
			this.drawTextOnScreen(
				Component.literal("Mode: $mode"),
				Color.WHITE,
				poseStack,
				buffer,
				0.008,
				0.015
			)
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
				} ?: AABB.of(this.mode.getBounding())

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