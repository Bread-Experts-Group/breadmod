package breadmod.capabilities.temperature

open class TemperatureImpl(override val type: TemperatureType, override var temperature: Float = 273.15F) : ITemperatureCapability