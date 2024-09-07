package bread.mod.breadmod.util

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.level.Level

internal data class ModDamageTypes(val key: ResourceKey<DamageType>, val exhaustion: Float) {
    private constructor(name: String) : this(name, 0f)
    private constructor(name: String, exhaustion: Float) : this(ResourceKey.create(Registries.DAMAGE_TYPE, modLocation(name)), exhaustion)

    fun source(level: Level) = source(level.registryAccess())

    fun source(registryAccess: RegistryAccess): DamageSource =
        DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key))

    fun msgID(): String = registryName().namespace + "." + registryName().path

    fun registryName(): ResourceLocation = key.location()

    fun translationKey(): String = "death.attack." + msgID()

    companion object {
        val TIMER_RAN_OUT: ModDamageTypes = ModDamageTypes("timer")
    }
}