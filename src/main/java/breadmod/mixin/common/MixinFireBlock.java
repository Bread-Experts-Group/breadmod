package breadmod.mixin.common;

import breadmod.block.util.ISpecialFireAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
abstract class MixinFireBlock {
    @Inject(method = "tryCatchFire", at = @At("HEAD"), cancellable = true, remap = false)
    private void tryCatchFire(Level p_53432_, BlockPos p_53433_, int p_53434_, RandomSource p_53435_, int p_53436_, Direction face, CallbackInfo ci) {
        BlockState blockState = p_53432_.getBlockState(p_53433_);
        if(blockState.getBlock().getClass().isAnnotationPresent(ISpecialFireAction.class)) {
            blockState.onCaughtFire(p_53432_, p_53433_, face, null);
            ci.cancel();
        }
    }
}
