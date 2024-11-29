package org.bread_experts_group.breadmod.registry.sound

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

object ModSounds {
    val SOUND_REGISTRY: DeferredRegister<SoundEvent> =
        DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BreadMod.ID)

    val TEST_SOUND: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("test_sound")
    val HAPPY_BLOCK_FUSE: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("happy_block_fuse")
    val ULTRAMARINE: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("ultramarine")
    val POW: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("pow")
    val SCREAM: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("scream")
    val MINIGUN: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("minigun")
    val TOOL_GUN: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("tool_gun")
    val HELL_NAW: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("hell_naw")
    val WAR_TIMER: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("war_timer")
    val WAR_TIMER_UP: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("war_timer_up")
    val MACH_ONE: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("mach_one")
    val MACH_TWO: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("mach_two")
    val MACH_THREE: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("mach_three")
    val MACH_FOUR: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("mach_four")
    val KILL_ENEMY: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("kill_enemy")
    val PUNCH: DeferredHolder<SoundEvent, SoundEvent> = registerSoundEvents("punch")

    private fun registerSoundEvents(name: String): DeferredHolder<SoundEvent, SoundEvent> {
        return SOUND_REGISTRY.register(name) { ->
            SoundEvent.createVariableRangeEvent(modLocation(name))
        }
    }

    /**
     * @return the resource key of this [SoundEvent]
     */
    fun SoundEvent.toResourceKey(): ResourceKey<SoundEvent> = BuiltInRegistries.SOUND_EVENT.getResourceKey(this).get()
}