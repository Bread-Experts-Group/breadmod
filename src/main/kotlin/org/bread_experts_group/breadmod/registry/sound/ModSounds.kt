package org.bread_experts_group.breadmod.registry.sound

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage

object ModSounds {
	val SOUND_REGISTRY: DeferredRegister<SoundEvent> =
		DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BreadMod.ID)

	@DataGenerateLanguage("en_us", "Test Sound plays")
	val TEST_SOUND: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("test_sound")

	@DataGenerateLanguage("en_us", "HAPPY HAPPY HAPPY")
	val HAPPY_BLOCK_FUSE: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("happy_block_fuse")

	@DataGenerateLanguage("en_us", "Pow!")
	val POW: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("pow")

	@DataGenerateLanguage("en_us", "AAAAAAAAAAAAAAA-")
	val SCREAM: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("scream")

	@DataGenerateLanguage("en_us", "Minigun fires")
	val MINIGUN: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("minigun")

	@DataGenerateLanguage("en_us", "Tool Gun fires")
	val TOOL_GUN: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("tool_gun")

	@DataGenerateLanguage("en_us", "HELL NAW!")
	val HELL_NAW: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("hell_naw")

	@DataGenerateLanguage("en_us", "War timer counts down")
	val WAR_TIMER: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("war_timer")

	@DataGenerateLanguage("en_us", "War timer counts up")
	val WAR_TIMER_UP: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("war_timer_up")
	val ULTRAMARINE: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("ultramarine")
	val MACH_ONE: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("mach_one")
	val MACH_TWO: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("mach_two")
	val MACH_THREE: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("mach_three")
	val MACH_FOUR: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("mach_four")
	val KILL_ENEMY: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("kill_enemy")
	val PUNCH: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("punch")
	private fun registerSoundEvents(name: String): DeferredHolder<SoundEvent, SoundEvent> {
		return this.SOUND_REGISTRY.register(name) { ->
			SoundEvent.createVariableRangeEvent(modLocation(name))
		}
	}

	/**
	 * @return the resource key of this [SoundEvent]
	 */
	fun SoundEvent.toResourceKey(): ResourceKey<SoundEvent> = BuiltInRegistries.SOUND_EVENT.getResourceKey(this).get()
}