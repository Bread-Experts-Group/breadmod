package org.bread_experts_group.breadmod.mixin.client.physics_grid;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.bread_experts_group.breadmod.experimental.physics_grid.GridHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
	@Inject(
			method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/HitResult;getLocation()Lnet/minecraft/world/phys/Vec3;"
			),
			cancellable = true
	)
	private void bypassFilteringForGrid(
			Entity entity,
			double blockInteractionRange,
			double entityInteractionRange,
			float partialTick,
			CallbackInfoReturnable<HitResult> cir,
			@Local HitResult original
	) {
		if (original instanceof GridHitResult) cir.setReturnValue((GridHitResult) original);
	}
}
