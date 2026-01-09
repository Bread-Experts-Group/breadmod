package org.bread_experts_group.breadmod.mixin.compat.jade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.overlay.WailaTickHandler;

// todo doesn't work, look into making grid block entities work in jade
@Mixin(WailaTickHandler.class)
abstract class MixinWailaTickHandler {
	@ModifyExpressionValue(
			method = "tickClient",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
			)
	)
	private BlockEntity getGridBlockEntity(
			BlockEntity original,
			@Local(name = "mc") Minecraft mc,
			@Local(name = "blockTarget") BlockHitResult blockTarget
	) {
		LocalPlayer player = mc.player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? grid.microLevel.getBlockEntity(blockTarget.getBlockPos()) : original;
	}
}
