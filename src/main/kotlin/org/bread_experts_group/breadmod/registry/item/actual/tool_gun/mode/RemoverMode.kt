package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.client.event.InputEvent.Key
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.KeyMappings
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.entities
import org.bread_experts_group.breadmod.util.rayCast
import java.awt.Color

@ToolGunMode
@Suppress("unused")
class RemoverMode : AbstractToolGunMode() {
	companion object {
		@DataGenerateLanguage("en_us", "Remover Mode")
		val name: MutableComponent = modTranslatable("tool_gun", "remover", "mode", "name")

		@DataGenerateLanguage("en_us", "Mode for removing entities and blocks from the world... humanely of course.")
		val description: MutableComponent = modTranslatable("tool_gun", "remover", "mode", "description")

		@DataGenerateLanguage("en_us", "Remover")
		val displayName: MutableComponent = modTranslatable("tool_gun", "remover", "mode", "display_name")

		@DataGenerateLanguage("en_us", "Remove Entities and Blocks.")
		val tooltip: MutableComponent = modTranslatable("tool_gun", "remover", "mode", "tooltip")

		@DataGenerateLanguage("en_us", "BreadMod: Disconnect: Client 0 overflowed reliable channel.")
		val playerDisconnectMessage: MutableComponent =
			modTranslatable("item", TOOL_GUN_DEF, "remover", "player_left_game")
	}

	var targetEntities: Boolean = false
	override fun action(level: Level, player: Player, stack: ItemStack) {
		if (!player.isShiftKeyDown) {
			if (this.targetEntities) {
				val entity = player.rayCast(500.0, entities(EntityType.PLAYER))
				entity?.let {
					if (level is ServerLevel) {
						level.sendParticles(
							ParticleTypes.END_ROD,
							it.position.x,
							it.position.y,
							it.position.z,
							40,
							this.rand(player),
							player.random.nextDouble(),
							this.rand(player),
							1.0
						)
						if (it.hit is ServerPlayer) {
							it.hit.connection.disconnect(
								modTranslatable(
									"item",
									TOOL_GUN_DEF,
									"remover",
									"player_left_game"
								)
							)
						} else {
							it.hit.discard()
							level.server.playerList.players.forEach { player ->
								player.sendSystemMessage(
									Component.translatable("multiplayer.player.left", it.hit.displayName)
										.withStyle(ChatFormatting.YELLOW)
								)
							}
						}
					}
				}
			} else {
				val block = player.rayCast(500.0, blocks(Blocks.AIR))
				block?.let {
					level.setBlockAndUpdate(BlockPos.containing(it.position), Blocks.AIR.defaultBlockState())
					if (level is ServerLevel) {
						level.sendParticles(
							ParticleTypes.CRIT,
							it.position.x,
							it.position.y,
							it.position.z,
							20,
							this.rand(player),
							player.random.nextDouble(),
							this.rand(player),
							1.0
						)
					}
				}
			}
		}
//		player.sendSystemMessage(Component.literal("$block"))
//		player.sendSystemMessage(Component.literal("$entity"))
	}

	override fun keyboardInputAction(event: Key, stack: ItemStack, player: Player) {
		if (event.key == KeyMappings.toolGunAltOne.key.value && event.action == InputConstants.PRESS) {
			val data = ToolGunData.get(stack)
			data.modifyValueAndSync<Boolean>("test", !this.targetEntities)
		}
	}

	private fun rand(player: Player): Double = (player.random.nextDouble() - 0.5) * 1.2

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip
	override fun getUid(): ResourceLocation = this.toolGunLocation("remover_mode")
	override fun getCustomRenderer(): IToolGunModeRenderer = RemoverRenderer(this.getUid())

	override fun saveExtraData(tag: CompoundTag) {
		tag.putBoolean("test", this.targetEntities)
	}

	override fun loadExtraData(tag: CompoundTag) {
		this.targetEntities = tag.getBoolean("test")
	}

	class RemoverRenderer(id: ResourceLocation) : AbstractToolGunModeRenderer(id) {
		override fun buildModeWidget(): Builder = Builder()
			.icon(Items.STRUCTURE_VOID)
			.description(Companion.description)
			.name(Companion.name)

		override fun renderScreenStage(
			stack: ItemStack,
			displayContext: ItemDisplayContext,
			poseStack: PoseStack,
			buffer: MultiBufferSource,
			packedLight: Int,
			packedOverlay: Int,
		) {
			val (mode, _, _) = ToolGunData.get(stack)
			if (mode !is RemoverMode) return
			this.drawTextOnScreen(
				"Targeting: ${if (mode.targetEntities) "Entity" else "Block"}",
				Color.WHITE.rgb,
				Color.BLACK.rgb,
				false,
				localClient.font,
				poseStack,
				buffer,
				IToolGunModeRenderer.SCREEN_TEXT_X + 0.008,
				IToolGunModeRenderer.SCREEN_TEXT_Y - 0.015
			)
		}

		override fun getScreenTexture(): ResourceLocation =
			modLocation("textures", "tool_gun", "render", "screen_remover.png")
	}
}