package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
	@Shadow
	public ServerPlayer player;

	@ModifyExpressionValue(
			method = "handleUseItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;serverLevel()Lnet/minecraft/server/level/ServerLevel;",
					ordinal = 1
			)
	)
	private ServerLevel redirectLevel(ServerLevel original, @Local(argsOnly = true) ServerboundUseItemOnPacket packet) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? (ServerLevel) grid.getMicroLevel() : original;
	}

	@Definition(id = "player", field = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;player:Lnet/minecraft/server/level/ServerPlayer;")
	@Definition(id = "canInteractWithBlock", method = "Lnet/minecraft/server/level/ServerPlayer;canInteractWithBlock(Lnet/minecraft/core/BlockPos;D)Z")
	@Expression("this.player.canInteractWithBlock(?, ?)")
	@ModifyExpressionValue(
			method = "handleUseItemOn",
			at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private boolean redirectCanInteractWithBlock(boolean original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return grid != null || original;
	}

	@ModifyExpressionValue(
			method = "handlePlayerAction",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getMaxBuildHeight()I"
			)
	)
	private int getGridBuildHeight(int original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? grid.microLevel.getMaxBuildHeight() : original;
	}
}