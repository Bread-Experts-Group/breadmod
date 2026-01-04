package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
abstract class MixinServerPlayerGameModePhysGrid {
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
}
