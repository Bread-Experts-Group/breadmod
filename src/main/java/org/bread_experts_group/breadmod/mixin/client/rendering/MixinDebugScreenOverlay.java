package org.bread_experts_group.breadmod.mixin.client.rendering;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.bread_experts_group.breadmod.experimental.physics_grid.GridHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DebugScreenOverlay.class)
abstract class MixinDebugScreenOverlay {
	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyExpressionValue(
			method = "getSystemInformation",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
			)
	)
	private BlockState getGridBlock(BlockState original) {
		Player player = this.minecraft.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		if (grid == null) return original;
		GridHitResult cast = grid.gridBlockCast(player);
		return (cast != null) ? cast.getState() : original;
	}

	@ModifyExpressionValue(
			method = "getSystemInformation",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;")
	)
	private BlockPos getGridPos(BlockPos original) {
		Player player = this.minecraft.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		if (grid == null) return original;
		GridHitResult cast = grid.gridBlockCast(player);
		return (cast != null) ? cast.getLocalPos() : original;
	}
}
