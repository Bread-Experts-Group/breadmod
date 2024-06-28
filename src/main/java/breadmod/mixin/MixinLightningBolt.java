package breadmod.mixin;

import breadmod.block.ILightningStrikeAction;
import breadmod.mixin.accessors.IAccessorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
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
    private void powerLightningRod(CallbackInfo ci) {
        BlockPos blockPos = iGetStrikePosition();
        BlockState blockState = getLevel().getBlockState(blockPos);
        if(blockState.getBlock() instanceof ILightningStrikeAction) {
            ((ILightningStrikeAction) blockState.getBlock()).onLightningStruck(getLevel(), blockPos, blockState);
            ci.cancel();
        }
    }
}
