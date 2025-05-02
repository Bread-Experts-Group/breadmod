package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.sounds.SoundEvents
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedNukeBlock
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.toVec3

class NukeBlock : ExplosiveBlock(
	{ level, pos, igniter, delta ->
		PrimedNukeBlock(
			level,
			pos.toVec3().plus(Vec3(0.5, 0.0, 0.5)),
			delta,
			igniter
		)
	},
	SoundEvents.WITHER_SPAWN
)