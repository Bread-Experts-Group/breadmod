package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.ClipContext.Block.OUTLINE
import net.minecraft.world.level.ClipContext.Fluid.NONE
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode.Renderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget

@ToolGunMode
@Suppress("unused")
class RemoverMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		player.sendSystemMessage(Component.literal("${this.blockRayCast(player, level)}"))
		player.sendSystemMessage(Component.literal("${this.entityRayCast(player, level)}"))
	}

	private fun blockRayCast(player: Player, level: Level): BlockHitResult? {
		var blockHitResult: BlockHitResult? = null
		var blockDistance = 0.0
		do {
			val eyePos = player.eyePosition
			val target = eyePos.add(player.calculateViewVector(player.xRot, player.yRot).scale(blockDistance))
			val clip = level.clip(ClipContext(eyePos, target, OUTLINE, NONE, player))
			if (!level.getBlockState(clip.blockPos).`is`(Blocks.AIR)) {
				blockHitResult = clip
				break
			}
			blockDistance += 0.1
		} while (blockDistance < 50.0)
		return blockHitResult
	}

	private fun entityRayCast(player: Player, level: Level): EntityHitResult? {
		var entityHitResult: EntityHitResult? = null
		var entityDistance = 0.0
		do {
			val eyePos = player.eyePosition
			val target = eyePos.add(player.calculateViewVector(player.xRot, player.yRot).scale(entityDistance))
			val aabb = AABB.ofSize(target, 1.0, 1.0, 1.0)
			val entities = level.getEntities(player, aabb) { it !is Player }
			if (entities.isNotEmpty()) {
				entityHitResult = EntityHitResult(entities.first())
				break
			}
			entityDistance += 0.1
		} while (entityDistance < 50.0)
		return entityHitResult
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