package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bread_experts_group.breadmod.block.util.ILightningStrikeAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("resource")
@Mixin(LightningBolt.class)
abstract class MixinLightningBolt implements IAccessorEntity {
    @Invoker("getStrikePosition")
    abstract BlockPos iGetStrikePosition();

    @Inject(method = "powerLightningRod", at = @At("HEAD"), cancellable = true)
    private void powerLightningRod(final CallbackInfo ci) {
        final BlockPos blockPos = iGetStrikePosition();
        final Level level = breadmod$getLevel();
        final BlockState blockState = level.getBlockState(blockPos);

        if (blockState.getBlock() instanceof ILightningStrikeAction) {
            ((ILightningStrikeAction) blockState.getBlock()).onLightningStruck(breadmod$getLevel(), blockPos, blockState);
            ci.cancel();
        }
    }
}
