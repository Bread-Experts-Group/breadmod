package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode.Renderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.rayCast

@ToolGunMode
@Suppress("unused")
class RemoverMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		val block = player.rayCast(50, blocks(Blocks.AIR))
//		val entity = player.rayCast(50, entities(EntityType.PLAYER))
		block?.let {
			level.setBlockAndUpdate(BlockPos.containing(it.position), Blocks.WHITE_WOOL.defaultBlockState())
		}
//		player.sendSystemMessage(Component.literal("$block"))
//		player.sendSystemMessage(Component.literal("$entity"))
	}

	override fun getDisplayName(): Component = Component.literal("Remover")

	override fun getTooltip(): Component = Component.literal("Remove Entities and Blocks.")

	override fun getUid(): ResourceLocation = modLocation("tool_gun", "remover_mode")

	override fun getCustomRenderer(): Renderer = RemoverRenderer(this.getUid())

	class RemoverRenderer(private val id: ResourceLocation) : AbstractToolGunModeRenderer() {
		override fun getModeWidget(): ModeWidget = ModeWidget.Builder()
			.icon(Items.STRUCTURE_VOID.defaultInstance)
			.description("Mode for removing entities and blocks from the world... humanely of course.")
			.name("Remover Mode")
			.id(this.id)
			.build()
	}
}