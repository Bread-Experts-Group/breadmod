package org.bread_experts_group.breadmod.tool_gun.mode

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.KeyData
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.KeyMappings
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.createEntity
import org.bread_experts_group.breadmod.util.getBlockState
import org.bread_experts_group.breadmod.util.getValue
import org.bread_experts_group.breadmod.util.putBlockState
import org.bread_experts_group.breadmod.util.putEntity
import org.bread_experts_group.breadmod.util.putValue
import org.bread_experts_group.breadmod.util.rayCast

@ToolGunMode
@Suppress("unused")
class CreatorMode : IToolGunMode {
	private val logger: Logger = LogManager.getLogger("Creator Mode")

	companion object {
		@DataGenerateLanguage(name = "Create/Edit blocks and entities.")
		val description: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "description")

		@DataGenerateLanguage(name = "Creator")
		val displayName: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "display_name")

		@DataGenerateLanguage(name = "Creator Mode")
		val name: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "name")

		@DataGenerateLanguage(name = "create blocks and entities")
		val tooltip: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "tooltip")
	}

	private var preparedBlock: BlockState = Blocks.AIR.defaultBlockState()
	private var preparedEntityTag: CompoundTag = CompoundTag()
	private var placingEntity: Boolean = true

	override fun action(level: Level, player: Player, stack: ItemStack, usedHand: InteractionHand, data: ToolGunData) {
//		this.logger.info("block: ${this.preparedBlock}")
//		this.logger.info("entity: ${this.preparedEntityTag}")
		val block = player.rayCast(50.0, blocks()) ?: return
		if (this.placingEntity) {
			val entity = this.preparedEntityTag.createEntity(level)
			entity.setPos(block.hitPosition.relative(block.hitSide, 1.0))
			level.addFreshEntity(entity)
		} else {
			level.setBlockAndUpdate(block.blockPosition.relative(block.hitSide), this.preparedBlock)
		}
	}

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip
	override fun defineCustomRenderer(): IToolGunModeRenderer = CreatorRenderer(this)
	override fun getUid(): ResourceLocation = this.toolGunLocation("creator_mode")

	override fun registerKeys(into: MutableMap<Int, KeyData>) {
		into[InputConstants.KEY_F] = KeyData(Component.literal("open screen")) { _, _, player, data ->
			if (localClient.screen == null) localClient.setScreen(CreatorScreen(player.level(), data))
		}
		into[KeyMappings.toolGunAltOne.key.value] =
			KeyData(Component.literal("change place mode")) { event, _, _, data ->
				if (event.action == InputConstants.PRESS) data.setValue("placing_entity", !this.placingEntity)
			}
	}

	override fun saveExtraData(tag: CompoundTag, level: Level) {
		tag.putBlockState("block", this.preparedBlock)
		tag.putEntity("entity", this.preparedEntityTag.createEntity(level))
		tag.putValue("placing_entity", this.placingEntity)
	}

	override fun loadExtraData(tag: CompoundTag, level: Level) {
		this.preparedBlock = tag.getBlockState("block")
		this.preparedEntityTag = tag.getCompound("entity")
		this.placingEntity = tag.getValue("placing_entity")
	}

	// todo render an outline of the targeted block when looking at blocks in the world
	//  DebugRenderer.renderFilledBox using the AABB of the targeted block
	class CreatorRenderer(private val mode: CreatorMode) : IToolGunModeRenderer {
		override fun buildModeWidget(): Builder =
			Builder()
				.name(Companion.name)
				.icon(Items.CRAFTING_TABLE)
				.description(Companion.description)

		override fun renderScreenStage(
			stack: ItemStack,
			displayContext: ItemDisplayContext,
			poseStack: PoseStack,
			buffer: MultiBufferSource,
			packedLight: Int,
			packedOverlay: Int
		) {
			this.drawTextOnScreen(
				Component.literal("placing: ${if (this.mode.placingEntity) "Entity" else "Block"}"),
				Color.WHITE,
				Color.BLACK,
				false,
				poseStack,
				buffer,
				0.008,
				0.015
			)
		}

		private val random: RandomSource = RandomSource.create()
		private var rotation: Float = 0f
		override fun renderOverlayAdditions(
			guiGraphics: GuiGraphics,
			originX: Int,
			originY: Int,
			deltaTracker: DeltaTracker,
			stack: ItemStack,
			data: ToolGunData
		) {
			this.rotation += 1f * deltaTracker.gameTimeDeltaTicks
			CreatorScreen.renderBlockPreview(
				this.mode.preparedBlock,
				null,
				guiGraphics,
				this.random,
				guiGraphics.guiWidth() - 104,
				originY,
				this.rotation,
				deltaTracker.gameTimeDeltaTicks
			)
		}

		override fun getMode(): IToolGunMode = this.mode
	}
}