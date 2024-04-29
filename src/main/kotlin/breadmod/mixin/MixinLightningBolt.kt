package breadmod.mixin

import breadmod.block.ILightningStrikeAction
import breadmod.mixin.accessors.IEntityAccessor
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.level.block.state.BlockState
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("NonJavaMixin")
@Mixin(LightningBolt::class)
abstract class MixinLightningBolt: IEntityAccessor {
    @Invoker("getStrikePosition") abstract fun iGetStrikePosition(): BlockPos

    @Inject(method = ["powerLightningRod"], at = [At("HEAD")], cancellable = true)
    private fun powerLightningRod(callbackInfo: CallbackInfo) {
        val blockPos: BlockPos = iGetStrikePosition()
        val blockState: BlockState = level.getBlockState(blockPos)
        blockState.block.let {
            if(it is ILightningStrikeAction) {
                it.onLightningStruck(level, blockPos, blockState)
                callbackInfo.cancel()
            }
        }
    }
}