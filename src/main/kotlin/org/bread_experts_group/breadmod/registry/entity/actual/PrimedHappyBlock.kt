package org.bread_experts_group.breadmod.registry.entity.actual

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import kotlin.math.cos
import kotlin.math.sin

class PrimedHappyBlock(
	level: Level,
	pos: Vec3 = Vec3.ZERO,
	delta: Vec3 = Vec3.ZERO,
	private val owner: Entity? = null,
	private var shouldSpread: Boolean = false
) : PrimedTnt(ModEntityTypes.HAPPY_BLOCK_ENTITY.get(), level) {
	init {
		this.setPos(pos); this.deltaMovement = delta
	}

	private val spreadRadius = ModConfiguration.COMMON.happyBlockExplosionSpreadRadius.get()
	private val divisions = ModConfiguration.COMMON.happyBlockExplosionDivisions.get()

	// todo reimplement BMExplosion
	override fun explode(): Unit = this.level().let {
		it.explode(
			null,
			this.position().x,
			this.position().y,
			this.position().z,
			30f,
			Level.ExplosionInteraction.TNT
		)
//        BMExplosion(it, owner, position(), 10.0, 5, Explosion.BlockInteraction.DESTROY).explodeThreaded()
		if (this.shouldSpread) {
			repeat(this.divisions) { arc ->
				val current = arc.toDouble()
				val extraPrimedHappyBlock = PrimedHappyBlock(
					it, this.position(),
					Vec3(this.spreadRadius * cos(current), 0.5, this.spreadRadius * sin(current)),
					this.owner
				)
				it.addFreshEntity(extraPrimedHappyBlock)
			}
		}
	}

	override fun save(compound: CompoundTag): Boolean =
		if (!compound.getBoolean("shouldSpread")) {
			compound.putBoolean("shouldSpread", true)
			true
		} else false

	override fun load(compound: CompoundTag) {
		this.shouldSpread = compound.getBoolean("shouldSpread")
		super.load(compound)
	}

	override fun getType(): EntityType<*> = ModEntityTypes.HAPPY_BLOCK_ENTITY.get()
	override fun getPickResult(): ItemStack = ModBlocks.HAPPY_BLOCK.toStack()
}