package breadmod.mixin

import breadmod.block.CustomFireLogic
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.FireBlock
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(FireBlock::class)
class MixinFireBlock {
    @Inject(method = ["tryCatchFire"], at = [At("HEAD")], cancellable = true)
    private fun tryCatchFire(
        pLevel: Level, pPos: BlockPos, pChance: Int,
        pRandom: RandomSource, pAge: Int, pFace: Direction,
        info: CallbackInfo
    ) {
        val blockState = pLevel.getBlockState(pPos)
        if (blockState.block is CustomFireLogic) {
            blockState.onCaughtFire(pLevel, pPos, pFace, null)
            info.cancel()
        }
    }
}