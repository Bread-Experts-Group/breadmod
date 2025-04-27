package org.bread_experts_group.breadmod.experimental.physics_grid.dummy_level

import net.minecraft.util.AbortableIterationConsumer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.phys.AABB
import java.util.UUID
import java.util.function.Consumer

object DummyEntityGetter : LevelEntityGetter<Entity> {
	override fun get(id: Int): Entity? = null

	override fun get(uuid: UUID): Entity? = null

	override fun getAll(): MutableIterable<Entity> = mutableListOf()

	override fun <U : Entity?> get(
		test: EntityTypeTest<Entity, U>,
		bounds: AABB,
		consumer: AbortableIterationConsumer<U>
	) {
	}

	override fun get(boundingBox: AABB, consumer: Consumer<Entity>) {}

	override fun <U : Entity?> get(test: EntityTypeTest<Entity, U>, consumer: AbortableIterationConsumer<U>) {}
}