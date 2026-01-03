package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
abstract class MixinServerPlayerGameModePhysGrid {
	@Inject(
			method = "useItemOn",
			at = @At("HEAD")
	)
	private void probeUseItemOn(
			ServerPlayer player, Level level, ItemStack stack, InteractionHand hand,
			BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir
			) {
//		GeneralKt.logDebugInfo("useItemOn[" + level + ", " + hitResult.getBlockPos() + "]");
	}
}
