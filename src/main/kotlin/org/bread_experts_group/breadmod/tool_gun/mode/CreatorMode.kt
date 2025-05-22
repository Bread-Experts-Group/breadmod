package org.bread_experts_group.breadmod.tool_gun.mode

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.KeyData
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.createEntity
import org.bread_experts_group.breadmod.util.getBlockState
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.putBlockState
import org.bread_experts_group.breadmod.util.putEntity
import org.bread_experts_group.breadmod.util.rayCast

@ToolGunMode
@Suppress("unused")
class CreatorMode : AbstractToolGunMode() {
	private val logger: Logger = LogManager.getLogger("Creator Mode")

	companion object {
		@DataGenerateLanguage("en_us", "Create/Edit blocks and entities.")
		val description: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "description")

		@DataGenerateLanguage("en_us", "Creator")
		val displayName: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "display_name")

		@DataGenerateLanguage("en_us", "Creator Mode")
		val name: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "name")

		@DataGenerateLanguage("en_us", "create blocks and entities")
		val tooltip: MutableComponent = modTranslatable("tool_gun", "creator", "mode", "tooltip")
	}

	private var preparedBlock: BlockState = Blocks.AIR.defaultBlockState()
	private var preparedEntityTag: CompoundTag = CompoundTag()

	override fun action(level: Level, player: Player, stack: ItemStack) {
		this.logger.info("block: ${this.preparedBlock}")
		this.logger.info("entity: ${this.preparedEntityTag}")
		val block = player.rayCast(500.0, blocks()) ?: return
		val entity = this.preparedEntityTag.createEntity(level) ?: return
		entity.setPos(block.position.plus(0.0, 1.0, 0.0))
		level.addFreshEntity(entity)
	}

	override fun getDisplayName(): Component = displayName
	override fun getTooltip(): Component = tooltip
	override fun getCustomRenderer(): IToolGunModeRenderer = CreatorRenderer(this.getUid())
	override fun getUid(): ResourceLocation = this.toolGunLocation("creator_mode")

	override fun registerKeys(into: MutableMap<Int, KeyData>) {
		into[InputConstants.KEY_F] = KeyData(Component.literal("open screen")) { _, _, player, data ->
			if (localClient.screen == null) localClient.setScreen(CreatorScreen(player.level(), data))
		}
	}

	override fun saveExtraData(tag: CompoundTag, level: Level) {
		tag.putBlockState("block", this.preparedBlock)
		tag.putEntity("entity", this.preparedEntityTag.createEntity(level))
	}

	override fun loadExtraData(tag: CompoundTag, level: Level) {
		this.preparedBlock = tag.getBlockState("block")
		this.preparedEntityTag = tag.getCompound("entity")
	}

	class CreatorRenderer(id: ResourceLocation) : AbstractToolGunModeRenderer(id) {
		override fun buildModeWidget(): Builder =
			Builder()
				.name(name)
				.icon(Items.CRAFTING_TABLE)
				.description(description)
	}
}