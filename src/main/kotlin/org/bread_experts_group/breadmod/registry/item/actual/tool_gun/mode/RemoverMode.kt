package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.client.event.InputEvent.Key
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode.Renderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget.Builder
import org.bread_experts_group.breadmod.client.render.ToolGunRenderHelper
import org.bread_experts_group.breadmod.registry.KeyMappings
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunData
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.rayCast
import java.awt.Color

@ToolGunMode
@Suppress("unused")
class RemoverMode : AbstractToolGunMode() {
	var test: Boolean = false
	override fun action(level: Level, player: Player, stack: ItemStack) {
		if (!player.isShiftKeyDown) {
			val block = player.rayCast(50, blocks(Blocks.AIR))
//		val entity = player.rayCast(50, entities(EntityType.PLAYER))
			block?.let {
				level.setBlockAndUpdate(BlockPos.containing(it.position), Blocks.AIR.defaultBlockState())
				if (level is ServerLevel) {
					level.sendParticles(
						ParticleTypes.CRIT,
						it.position.x, it.position.y, it.position.z, 20,
						this.rand(player), player.random.nextDouble(), this.rand(player), 1.0
					)
				}
			}
		}
//		player.sendSystemMessage(Component.literal("$block"))
//		player.sendSystemMessage(Component.literal("$entity"))
	}

	override fun keyboardInputAction(event: Key, stack: ItemStack, player: Player) {
		if (event.key == KeyMappings.toolGunAltOne.key.value && event.action == InputConstants.PRESS) {
			player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 0.5f, 1f)
			player.sendSystemMessage(Component.literal("modifying value"))
			val data = ToolGunData.get(stack)
			data.modifyValueAndSync<Boolean>("test", !this.test)
		}
	}

	private fun rand(player: Player) = (player.random.nextDouble() - 0.5) * 1.2

	override fun getDisplayName(): Component = Component.literal("Remover")

	override fun getTooltip(): Component = Component.literal("Remove Entities and Blocks.")

	override fun getUid(): ResourceLocation = this.toolGunLocation("remover_mode")

	override fun getCustomRenderer(): Renderer = RemoverRenderer(this.getUid())

	override fun saveExtraData(tag: CompoundTag) {
		tag.putBoolean("test", this.test)
	}

	override fun loadExtraData(tag: CompoundTag) {
		this.test = tag.getBoolean("test")
	}

	class RemoverRenderer(id: ResourceLocation) : AbstractToolGunModeRenderer(id) {
		override fun buildModeWidget(): Builder = ModeWidget.Builder()
			.icon(Items.STRUCTURE_VOID)
			.description("Mode for removing entities and blocks from the world... humanely of course.")
			.name("Remover Mode")

		override fun renderScreenStage(
			stack: ItemStack,
			displayContext: ItemDisplayContext,
			poseStack: PoseStack,
			buffer: MultiBufferSource,
			packedLight: Int,
			packedOverlay: Int,
			helper: ToolGunRenderHelper
		) {
			val (mode, _) = ToolGunData.get(stack)
			if (mode !is RemoverMode) return
			helper.drawTextOnScreen(
				"${mode.test}",
				Color.WHITE.rgb,
				Color(0f, 0f, 0f, 0f).rgb,
				false,
				helper.font,
				poseStack,
				buffer,
				helper.screenTextX + 0.008,
				helper.screenTextY - 0.015
			)
		}

		override fun getScreenTexture(): ResourceLocation =
			modLocation("textures", "tool_gun", "render", "screen_remover.png")
	}
}