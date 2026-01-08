package org.bread_experts_group.breadmod.mixin.client.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
abstract class MixinMinecraft {
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Shadow
	@Nullable
	public HitResult hitResult;

	@Shadow
	@Final
	private static Logger LOGGER;

	@ModifyExpressionValue(
			method = "startUseItem",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel startUseItemGridLevel(ClientLevel original) {
		if (this.player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		if (grid != null) return (ClientLevel) grid.microLevel;
		return original;
	}

	@ModifyExpressionValue(
			method = "startAttack",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel startAttackGridLevel(ClientLevel original) {
		if (this.player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		if (grid != null) return (ClientLevel) grid.microLevel;
		return original;
	}

	@ModifyExpressionValue(
			method = "pickBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
			)
	)
	private BlockState pickGridBlock(BlockState original) {
		LocalPlayer player = this.player;
		if (player == null) return original;
		BlockHitResult hitResult = (BlockHitResult) this.hitResult;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null && hitResult != null) ? grid.getMicroLevel().getBlockState(hitResult.getBlockPos()) : original;
	}

	@ModifyExpressionValue(
			method = "continueAttack",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel continueAttackGridLevel(ClientLevel original) {
		if (this.player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(this.player);
		if (grid != null) return (ClientLevel) grid.microLevel;
		return original;
	}
}
