package breadmod.block.color

import breadmod.capabilities.ModCapabilities
import breadmod.util.getRGBFromK
import net.minecraft.client.color.block.BlockColor
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import java.awt.Color

// TODO: Look into mixing-in BlockColor for dynamic changes
object BlackbodyBlockColor: BlockColor {
    override fun getColor(pState: BlockState, pLevel: BlockAndTintGetter?, pPos: BlockPos?, pTintIndex: Int): Int {
        val blockEntity = pPos?.let { pLevel?.getBlockEntity(it) } ?: return 0
        println("WOaO!")
        blockEntity.let { pEntity ->
            val capability = pEntity.getCapability(ModCapabilities.TEMPERATURE).resolve()
            if(capability.isPresent) println("!${capability.get().temperature}")
            return if(capability.isPresent) getRGBFromK(capability.get().temperature).rgb else 0
        }
    }
}