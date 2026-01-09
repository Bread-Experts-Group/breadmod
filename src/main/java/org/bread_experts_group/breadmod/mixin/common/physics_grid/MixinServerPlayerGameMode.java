package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
abstract class MixinServerPlayerGameMode {
	@Shadow
	@Final
	protected ServerPlayer player;

	@ModifyExpressionValue(
			method = "useItemOn",
			at = @At(
					value = "NEW",
					target = "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/item/context/UseOnContext;"
			)
	)
	private UseOnContext redirectContextLevel(
			UseOnContext original,
			@Local(argsOnly = true) InteractionHand hand,
			@Local(argsOnly = true) BlockHitResult result
	) {
		ServerPlayer player = this.player;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? new UseOnContext(grid.microLevel, player, hand, player.getItemInHand(hand), result) : original;
	}

	@ModifyExpressionValue(
			method = "handleBlockBreakAction",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;level:Lnet/minecraft/server/level/ServerLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ServerLevel redirectLevelBlockBreakAction(ServerLevel original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? (ServerLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "handleBlockBreakAction",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;level()Lnet/minecraft/world/level/Level;"
			)
	)
	private Level redirectPlayerLevelBlockBreakAction(Level original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "incrementDestroyProgress",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;level:Lnet/minecraft/server/level/ServerLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ServerLevel redirectLevelDestroyProgress(ServerLevel original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? (ServerLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "incrementDestroyProgress",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;level()Lnet/minecraft/world/level/Level;"
			)
	)
	private Level redirectPlayerLevelDestroyProgress(Level original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "tick",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;level:Lnet/minecraft/server/level/ServerLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ServerLevel redirectLevelTick(ServerLevel original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? (ServerLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "destroyBlock",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;level:Lnet/minecraft/server/level/ServerLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ServerLevel redirectLevelDestroyBlock(ServerLevel original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? (ServerLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "destroyAndAck",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;level:Lnet/minecraft/server/level/ServerLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ServerLevel redirectLevelDestroyAndAck(ServerLevel original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? (ServerLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "removeBlock",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;level:Lnet/minecraft/server/level/ServerLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ServerLevel redirectLevelRemoveBlock(ServerLevel original) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		return (grid != null) ? (ServerLevel) grid.microLevel : original;
	}
}
