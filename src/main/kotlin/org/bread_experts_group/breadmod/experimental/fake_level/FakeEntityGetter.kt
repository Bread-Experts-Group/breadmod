package org.bread_experts_group.breadmod.experimental.fake_level

import net.minecraft.util.AbortableIterationConsumer
import net.minecraft.world.level.entity.EntityAccess
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.entity.LevelEntityGetter
import net.minecraft.world.phys.AABB
import java.util.UUID
import java.util.function.Consumer

class FakeEntityGetter<T : EntityAccess> : LevelEntityGetter<T> {
	override fun get(p0: Int): T? = null
	override fun get(p0: UUID): T? = null
	override fun <U : T> get(p0: EntityTypeTest<T, U>, p1: AABB, p2: AbortableIterationConsumer<U>) {
	}

	override fun get(p0: AABB, p1: Consumer<T>) {
	}

	override fun <U : T> get(p0: EntityTypeTest<T, U>, p1: AbortableIterationConsumer<U>) {
	}

	override fun getAll(): MutableIterable<T> = mutableListOf()

}