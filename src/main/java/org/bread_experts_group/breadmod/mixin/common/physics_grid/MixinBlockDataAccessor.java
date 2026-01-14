package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.server.commands.data.BlockDataAccessor$1")
abstract class MixinBlockDataAccessor {
	@ModifyExpressionValue(
			method = "access",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;getLevel()Lnet/minecraft/server/level/ServerLevel;")
	)
	private ServerLevel redirectLevel(ServerLevel original, @Local(argsOnly = true) CommandContext<CommandSourceStack> context) {
		ServerPlayer player = context.getSource().getPlayer();
		if (player != null) {
			PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
			return (grid != null) ? (ServerLevel) grid.microLevel : original;
		}
		return original;
	}
}