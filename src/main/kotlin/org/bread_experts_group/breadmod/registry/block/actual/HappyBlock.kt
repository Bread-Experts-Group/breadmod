package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedHappyBlock
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.toVec3

class HappyBlock : ExplosiveBlock(
	{ level, pos, igniter, delta ->
		PrimedHappyBlock(
			level,
			pos.toVec3().plus(Vec3(0.5, 0.0, 0.5)),
			delta,
			igniter,
			true
		)
	},
	ModSounds.HAPPY_BLOCK_FUSE.get()
)