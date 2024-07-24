package breadmod.block.util

import net.minecraftforge.common.extensions.IForgeFluid

interface ILiquidCombustable: IForgeFluid {
    fun getBurnTime(): Int
}