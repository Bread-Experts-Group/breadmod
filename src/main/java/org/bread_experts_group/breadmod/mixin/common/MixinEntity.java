package org.bread_experts_group.breadmod.mixin.common;

import com.google.common.collect.AbstractIterator;
import net.minecraft.world.level.BlockCollisions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockCollisions.class)
abstract class MixinBlockCollisions<T> extends AbstractIterator<T> {
	@Inject(method = "computeNext", at = @At("HEAD"), cancellable = true)
	private void computeNext(CallbackInfoReturnable<? super T> cir) {
		cir.setReturnValue(this.endOfData());
	}
}