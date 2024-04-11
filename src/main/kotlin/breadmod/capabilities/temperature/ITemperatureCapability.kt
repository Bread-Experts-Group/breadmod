package breadmod.capabilities.temperature

import net.minecraft.core.BlockPos
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.common.capabilities.AutoRegisterCapability
import net.minecraftforge.common.util.INBTSerializable

@AutoRegisterCapability
interface ITemperatureCapability: INBTSerializable<Tag> {
    val type: TemperatureType
    var temperature: Float

    override fun serializeNBT(): FloatTag = FloatTag.valueOf(temperature)

    override fun deserializeNBT(nbt: Tag?) {
        temperature = (nbt as? FloatTag)?.asFloat ?: 273.15F
    }

    fun radiate(pLevel: ServerLevel, pPos: BlockPos) = pLevel.getBlockState(pPos).let { pState ->
        temperature += 10.0F
        if(pState.isAir) {
            // Probably an item
        } else {

        }
    }
}