package org.bread_experts_group.breadmod.registry

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage

data class ModDamageType(val key: ResourceKey<DamageType>) {
	constructor(name: String) : this(ResourceKey.create(Registries.DAMAGE_TYPE, modLocation(name)))

	fun source(level: Level): DamageSource = this.source(level.registryAccess())
	private fun source(registryAccess: RegistryAccess): DamageSource =
		DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(this.key))

	private fun registryName(): ResourceLocation = this.key.location()
	private fun msgID(): String = this.registryName().namespace + "." + this.registryName().path
	fun translationKey(): String = "death.attack." + this.msgID()

	companion object {
		@DataGenerateLanguage("en_us", "%1\$s ran out of time!")
		val TIMER_RAN_OUT: ModDamageType = ModDamageType("timer")

		@DataGenerateLanguage("en_us", "%1\$s was vaporized by an explosion")
		val EXPLOSION_DAMAGE_VERY_HIGH: ModDamageType = ModDamageType("explosion_dmg_very_high")

		@DataGenerateLanguage("en_us", "%1\$s was obliterated by an explosion")
		val EXPLOSION_DAMAGE_HIGH: ModDamageType = ModDamageType("explosion_dmg_high")
	}
}