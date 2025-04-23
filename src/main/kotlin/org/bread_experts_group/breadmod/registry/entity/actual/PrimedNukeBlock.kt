package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.util.BreadModExplosion

class PrimedNukeBlock(
	level: Level,
	pos: Vec3 = Vec3.ZERO,
	delta: Vec3 = Vec3.ZERO,
	private val owner: Entity? = null
) : PrimedTnt(ModEntityTypes.NUKE_BLOCK_ENTITY.get(), level) {
	init {
		this.setPos(pos)
		this.deltaMovement = delta
		this.fuse = 200
	}

	override fun explode(): Unit = this.level().let {
		BreadModExplosion
			.calculate(it, this.position(), 200f, 100000)
			.explode(null)
	}

	override fun getType(): EntityType<*> = ModEntityTypes.NUKE_BLOCK_ENTITY.get()
	override fun getPickResult(): ItemStack = ModBlocks.NUKE.toStack()
}