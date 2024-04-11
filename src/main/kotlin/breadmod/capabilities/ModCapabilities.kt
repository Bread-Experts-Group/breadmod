package breadmod.capabilities

import breadmod.capabilities.temperature.ITemperatureCapability
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken

object ModCapabilities {
    val TEMPERATURE: Capability<ITemperatureCapability> = CapabilityManager.get(object : CapabilityToken<ITemperatureCapability>() {})
}