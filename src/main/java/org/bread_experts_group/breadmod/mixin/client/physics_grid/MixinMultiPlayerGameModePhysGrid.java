package org.bread_experts_group.breadmod.mixin.client.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SuppressWarnings("unused")
@Mixin(MultiPlayerGameMode.class)
abstract class MixinMultiPlayerGameModePhysGrid {
	@Shadow
	@Final
	private Minecraft minecraft;

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

	@ModifyExpressionValue(
			method = "performUseItemOn",
			at = @At(
					value = "NEW",
					target = "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/item/context/UseOnContext;"
			)
	)
	private UseOnContext redirectContextLevel(
			UseOnContext original,
			@Local(argsOnly = true) LocalPlayer player,
			@Local(argsOnly = true) InteractionHand hand,
			@Local(argsOnly = true) BlockHitResult result
	) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? new UseOnContext(grid.microLevel, player, hand, player.getItemInHand(hand), result) : original;
	}

	@ModifyExpressionValue(
			method = "performUseItemOn",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevel(ClientLevel original, @Local(argsOnly = true) LocalPlayer player) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? (ClientLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "startDestroyBlock",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevelStartDestroyBlock(ClientLevel original) {
		LocalPlayer player = this.minecraft.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? (ClientLevel) grid.microLevel : original;
	}

	@ModifyArgs(
			method = "lambda$startDestroyBlock$1",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
			)
	)
	private void redirectPlayerLevelStartDestroyBlock(Args args) {
		LocalPlayer player = this.minecraft.player;
		if (player != null) {
			PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
			if (grid != null) args.set(1, grid.microLevel);
		}
	}

	@ModifyExpressionValue(
			method = "continueDestroyBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;level()Lnet/minecraft/world/level/Level;"
			)
	)
	private Level redirectPlayerLevelContinueDestroyBlock(Level original) {
		LocalPlayer player = this.minecraft.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "stopDestroyBlock",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevelstartDestroyBlock(ClientLevel original) {
		LocalPlayer player = this.minecraft.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? (ClientLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "destroyBlock",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevelDestroyBlock(ClientLevel original) {
		LocalPlayer player = this.minecraft.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? (ClientLevel) grid.microLevel : original;
	}

	@ModifyExpressionValue(
			method = "continueDestroyBlock",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevelContinueDestroyBlock(ClientLevel original) {
		LocalPlayer player = this.minecraft.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? (ClientLevel) grid.microLevel : original;
	}
}
