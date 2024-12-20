package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
interface IAccessorEntity {
	@Accessor(value = "level")
	Level breadmod$getLevel();
}
