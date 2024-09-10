package bread.mod.breadmod.mixin.common;

import bread.mod.breadmod.block.util.ILightningStrikeAction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
abstract class MixinLightningBolt implements IAccessorEntity {
    @Invoker("getStrikePosition")
    abstract BlockPos iGetStrikePosition();

    @Inject(method = "powerLightningRod", at = @At("HEAD"), cancellable = true)
    private void powerLightningRod(final CallbackInfo ci) {
        final BlockPos blockPos = this.iGetStrikePosition();
        final Level level = this.iGetLevel();
        final BlockState blockState = level.getBlockState(blockPos);

        if (blockState.getBlock() instanceof ILightningStrikeAction) {
            ((ILightningStrikeAction) blockState.getBlock()).onLightningStruck(level, blockPos, blockState);
            ci.cancel();
        }
    }
}
