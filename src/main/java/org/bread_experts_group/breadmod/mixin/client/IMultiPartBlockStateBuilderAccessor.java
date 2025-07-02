package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiPartBlockStateBuilder.class)
public interface IMultiPartBlockStateBuilderAccessor {
	@Accessor(value = "owner")
	Block breadmod$getOwner();
}