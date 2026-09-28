package org.bread_experts_group.breadmod.registry

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getProvider
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import org.bread_experts_group.breadmod.Breadmod

object ModSounds {
    val REGISTRY: RegistryProvider<SoundEvent> = BuiltInRegistries.SOUND_EVENT.getProvider(Breadmod.MOD_ID)

    val SECRET_HOPPIN: RegistryObject<SoundEvent, SoundEvent> = this.registerSoundEvents("secret_hoppin")

    private fun registerSoundEvents(name: String): RegistryObject<SoundEvent, SoundEvent> =
        this.REGISTRY.register(name) { SoundEvent.createVariableRangeEvent(Breadmod.modLoc(name)) }

    /**
     * @return the resource key of this [SoundEvent]
     */
    fun SoundEvent.toResourceKey(): ResourceKey<SoundEvent> = BuiltInRegistries.SOUND_EVENT.getResourceKey(this).get()
}