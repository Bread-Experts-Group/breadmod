package org.bread_experts_group.breadmod.registry

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

data class ModDamageTypes(val key: ResourceKey<DamageType>, val exhaustion: Float) {
    private constructor(name: String) : this(name, 0f)
    private constructor(name: String, exhaustion: Float) : this(
        ResourceKey.create(Registries.DAMAGE_TYPE, modLocation(name)),
        exhaustion
    )

    fun source(level: Level): DamageSource = source(level.registryAccess())

    private fun source(registryAccess: RegistryAccess): DamageSource =
        DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key))

    private fun registryName(): ResourceLocation = key.location()
    private fun msgID(): String = registryName().namespace + "." + registryName().path
    fun translationKey(): String = "death.attack." + msgID()

    companion object {
        val TIMER_RAN_OUT: ModDamageTypes = ModDamageTypes("timer")
    }
}