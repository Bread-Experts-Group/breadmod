package org.bread_experts_group.breadmod.mixin.client.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MultiPlayerGameMode.class)
abstract class MixinMultiPlayerGameMode {
	@ModifyArg(
			method = "performUseItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;doesSneakBypassUse(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"
			)
	)
	private LevelReader redirectDoesSneakBypassUse(LevelReader par1, @Local(argsOnly = true) LocalPlayer player) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? grid.microLevel : par1;
	}

	@ModifyExpressionValue(
			method = "performUseItemOn",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	)
	private BlockState redirectBlockState(
			BlockState original,
			@Local(argsOnly = true) LocalPlayer player,
			@Local BlockPos blockPos
	) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		if (grid != null) {
			return grid.microLevel.getBlockState(blockPos);
		}
		return original;
	}
}
