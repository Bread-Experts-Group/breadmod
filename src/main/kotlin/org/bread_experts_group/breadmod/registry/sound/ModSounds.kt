package org.bread_experts_group.breadmod.registry.sound

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.datagen.sound.DataGenerateSound

private typealias SoundHolder = DeferredHolder<SoundEvent, SoundEvent>

object ModSounds {
	val SOUND_REGISTRY: DeferredRegister<SoundEvent> =
		DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BreadMod.ID)

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Test Sound plays")
	val TEST_SOUND: SoundHolder = this.registerSoundEvents("test_sound")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "LOUD INCORRECT BUZZER")
	val WRONG: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("wrong")

	@DataGenerateSound(stream = true)
	@DataGenerateLanguage("en_us", "GAS GAS GAS")
	val GAS_GAS_GAS: SoundHolder = this.registerSoundEvents("gas_gas_gas")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "HAPPY HAPPY HAPPY")
	val HAPPY_BLOCK_FUSE: SoundHolder = this.registerSoundEvents("happy_block_fuse")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Pow!")
	val POW: SoundHolder = this.registerSoundEvents("pow")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "AAAAAAAAAAAAAAA-")
	val SCREAM: SoundHolder = this.registerSoundEvents("scream")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Minigun fires")
	val MINIGUN: SoundHolder = this.registerSoundEvents("minigun")

	@DataGenerateSound(sound = "breadmod:tool_gun_one")
	@DataGenerateSound(sound = "breadmod:tool_gun_two")
	@DataGenerateLanguage("en_us", "Tool Gun fires")
	val TOOL_GUN: SoundHolder = this.registerSoundEvents("tool_gun")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "HELL NAW!")
	val HELL_NAW: SoundHolder = this.registerSoundEvents("hell_naw")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "War timer counts down")
	val WAR_TIMER: SoundHolder = this.registerSoundEvents("war_timer")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "War timer counts up")
	val WAR_TIMER_UP: SoundHolder = this.registerSoundEvents("war_timer_up")

	@DataGenerateSound(stream = true)
	val ULTRAMARINE: SoundHolder = this.registerSoundEvents("ultramarine")

	@DataGenerateSound(stream = true)
	val KSP_BUILDMODE: SoundHolder = this.registerSoundEvents("ksp_buildmode")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Speed")
	val MACH_ONE: SoundHolder = this.registerSoundEvents("mach_one")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Speedy")
	val MACH_TWO: SoundHolder = this.registerSoundEvents("mach_two")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Speedier")
	val MACH_THREE: SoundHolder = this.registerSoundEvents("mach_three")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "PEPPINO SPEED")
	val MACH_FOUR: SoundHolder = this.registerSoundEvents("mach_four")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "death.mp3")
	val KILL_ENEMY: SoundHolder = this.registerSoundEvents("kill_enemy")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Slam")
	val PUNCH: SoundHolder = this.registerSoundEvents("punch")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "1x Multi")
	val DOUBLE_1X: SoundHolder = this.registerSoundEvents("double_or_nothing_1x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "2x Multi")
	val DOUBLE_2X: SoundHolder = this.registerSoundEvents("double_or_nothing_2x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "3x Multi")
	val DOUBLE_3X: SoundHolder = this.registerSoundEvents("double_or_nothing_3x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "4x Multi!")
	val DOUBLE_4X: SoundHolder = this.registerSoundEvents("double_or_nothing_4x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "5x Multi!!")
	val DOUBLE_5X: SoundHolder = this.registerSoundEvents("double_or_nothing_5x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "6x Multi!!!")
	val DOUBLE_6X: SoundHolder = this.registerSoundEvents("double_or_nothing_6x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "7x!!!")
	val DOUBLE_7X: SoundHolder = this.registerSoundEvents("double_or_nothing_7x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "!!! 8x !!!")
	val DOUBLE_8X: SoundHolder = this.registerSoundEvents("double_or_nothing_8x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "!!!!! 9X !!!!!")
	val DOUBLE_9X: SoundHolder = this.registerSoundEvents("double_or_nothing_9x")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "JACKPOT!")
	val DOUBLE_JACKPOT: SoundHolder = this.registerSoundEvents("double_or_nothing_jackpot")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Cashout")
	val DOUBLE_CASHOUT: SoundHolder = this.registerSoundEvents("double_or_nothing_cashout")

	@DataGenerateSound
	@DataGenerateLanguage("en_us", "Nothing...")
	val DOUBLE_NOTHING: SoundHolder = this.registerSoundEvents("double_or_nothing_nothing")

	private fun registerSoundEvents(name: String): SoundHolder {
		return this.SOUND_REGISTRY.register(name) { ->
			SoundEvent.createVariableRangeEvent(modLocation(name))
		}
	}

	/**
	 * @return the resource key of this [SoundEvent]
	 */
	fun SoundEvent.toResourceKey(): ResourceKey<SoundEvent> = BuiltInRegistries.SOUND_EVENT.getResourceKey(this).get()
}