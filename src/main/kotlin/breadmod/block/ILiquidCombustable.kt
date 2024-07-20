package breadmod.block

import net.minecraftforge.common.extensions.IForgeFluid

interface ILiquidCombustable: IForgeFluid {
    fun getBurnTime(): Int
}