package breadmod.capabilities

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.AutoRegisterCapability

@AutoRegisterCapability
class TemperatureCapability {
    var currentTemperature: Float = 293.15F
    fun radiate(pBlockEntity: BlockEntity) {
        // TODO! :3
    }
}

