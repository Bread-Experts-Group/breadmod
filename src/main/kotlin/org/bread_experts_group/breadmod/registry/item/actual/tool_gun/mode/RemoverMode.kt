package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode.Renderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i

@ToolGunMode
@Suppress("unused")
class RemoverMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		val block = player.rayCast(50, this.blocks(Blocks.AIR))
		val entity = player.rayCast(50, this.entities(EntityType.PLAYER))
		player.sendSystemMessage(Component.literal("$block"))
		player.sendSystemMessage(Component.literal("$entity"))
	}

	class HitResult<T>(val position: Vec3, val length: Int, val hit: T)

	private fun <T> rayCast(
		position: Vec3, direction: Vec3,
		length: Int,
		selector: (Vec3) -> T?
	): HitResult<T>? {
		var result: HitResult<T>? = null
		var distance = 0.0
		do {
			val hit = selector(position.add(direction.scale(distance)))
			if (hit != null) {
				result = HitResult(position, length, hit)
				break
			}
			distance++
		} while (distance < length)
		return result
	}

	private fun <T> Entity.rayCast(length: Int, selector: (Level, Vec3) -> T?) = this@RemoverMode.rayCast(
		this.eyePosition,
		this.calculateViewVector(this.xRot, this.yRot),
		length
	) { selector(this.level(), it) }

	fun blocks(vararg filterBlocks: Block): (Level, Vec3) -> BlockState? = { level, position ->
		val blockPos = BlockPos(position.toVec3i())
		val state = level.getBlockState(blockPos)
		if (filterBlocks.contains(state.block)) null
		else state
	}

	fun entities(vararg filterTypes: EntityType<*>): (Level, Vec3) -> Entity? = { level, position ->
		val entities = level.getEntities(null, AABB.ofSize(position, 1.0, 1.0, 1.0))
			.firstOrNull()
		if (entities == null || filterTypes.contains(entities.type)) null
		else entities
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