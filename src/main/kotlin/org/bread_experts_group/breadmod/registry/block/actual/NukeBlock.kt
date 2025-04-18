package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.sounds.SoundEvents
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedNukeBlock
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

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