package breadmod.capabilities

import net.minecraft.core.Direction
import net.minecraft.nbt.IntTag
import net.minecraftforge.common.capabilities.AutoRegisterCapability
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional

class UltimateBreadItemCapabilityProvider: ICapabilityProvider {
    override fun <T : Any?> getCapability(p0: Capability<T>, p1: Direction?): LazyOptional<T>
         = LazyOptional.empty() //TODO

    @AutoRegisterCapability
    interface UltimateBreadItemCapability: INBTSerializable<IntTag> {
        var ticksRemaining: Int
        override fun serializeNBT(): IntTag = IntTag.valueOf(ticksRemaining)
        override fun deserializeNBT(nbt: IntTag?) { ticksRemaining = nbt?.asInt ?: 0 }
    }

    class UltimateBreadItemCapabilityImpl : UltimateBreadItemCapability {
        override var ticksRemaining: Int = 0
    }
}