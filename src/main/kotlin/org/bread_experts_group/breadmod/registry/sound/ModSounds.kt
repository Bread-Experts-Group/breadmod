package org.bread_experts_group.breadmod.registry.sound

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation

object ModSounds {
    val SOUND_REGISTRY: DeferredRegister<SoundEvent> =
        DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Breadmod.ID)

    val TEST_SOUND = registerSoundEvents("test_sound")
    val HAPPY_BLOCK_FUSE = registerSoundEvents("happy_block_fuse")
    val ULTRAMARINE = registerSoundEvents("ultramarine")
    val POW = registerSoundEvents("pow")
    val SCREAM = registerSoundEvents("scream")
    val MINIGUN = registerSoundEvents("minigun")
    val TOOL_GUN = registerSoundEvents("tool_gun")
    val HELL_NAW = registerSoundEvents("hell_naw")
    val WAR_TIMER = registerSoundEvents("war_timer")
    val WAR_TIMER_UP = registerSoundEvents("war_timer_up")
    val MACH_ONE = registerSoundEvents("mach_one")
    val MACH_TWO = registerSoundEvents("mach_two")
    val MACH_THREE = registerSoundEvents("mach_three")
    val MACH_FOUR = registerSoundEvents("mach_four")
    val KILL_ENEMY = registerSoundEvents("kill_enemy")
    val PUNCH = registerSoundEvents("punch")

    private fun registerSoundEvents(name: String): DeferredHolder<SoundEvent, SoundEvent> {
        return SOUND_REGISTRY.register(name) { ->
            SoundEvent.createVariableRangeEvent(modLocation(name))
        }
    }

    /**
     * @return the resource key of this [SoundEvent]
     */
    fun SoundEvent.toResourceKey() = BuiltInRegistries.SOUND_EVENT.getResourceKey(this).get()
}