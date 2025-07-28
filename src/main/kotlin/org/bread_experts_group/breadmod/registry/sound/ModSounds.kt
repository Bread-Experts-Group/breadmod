package org.bread_experts_group.breadmod.registry.sound

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.datagen.sound.DataGenerateSound
import org.bread_experts_group.breadmod.registry.RegistryProvider

private typealias SoundHolder = DeferredHolder<SoundEvent, SoundEvent>

object ModSounds : RegistryProvider(Registries.SOUND_EVENT) {
	@DataGenerateSound
	@DataGenerateLanguage(name = "Test Sound plays")
	val TEST_SOUND: SoundHolder = this.registerSoundEvents("test_sound")

	@DataGenerateSound
	@DataGenerateLanguage(name = "LOUD INCORRECT BUZZER")
	val WRONG: DeferredHolder<SoundEvent, SoundEvent> = this.registerSoundEvents("wrong")

	@DataGenerateSound(stream = true)
	@DataGenerateLanguage(name = "GAS GAS GAS")
	val GAS_GAS_GAS: SoundHolder = this.registerSoundEvents("gas_gas_gas")

	@DataGenerateSound
	@DataGenerateLanguage(name = "HAPPY HAPPY HAPPY")
	val HAPPY_BLOCK_FUSE: SoundHolder = this.registerSoundEvents("happy_block_fuse")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Pow!")
	val POW: SoundHolder = this.registerSoundEvents("pow")

	@DataGenerateSound
	@DataGenerateLanguage(name = "AAAAAAAAAAAAAAA-")
	val SCREAM: SoundHolder = this.registerSoundEvents("scream")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Minigun fires")
	val MINIGUN: SoundHolder = this.registerSoundEvents("minigun")

	@DataGenerateSound(sound = "breadmod:tool_gun_one")
	@DataGenerateSound(sound = "breadmod:tool_gun_two")
	@DataGenerateLanguage(name = "Tool Gun fires")
	val TOOL_GUN: SoundHolder = this.registerSoundEvents("tool_gun")

	@DataGenerateSound
	@DataGenerateLanguage(name = "HELL NAW!")
	val HELL_NAW: SoundHolder = this.registerSoundEvents("hell_naw")

	@DataGenerateSound
	@DataGenerateLanguage(name = "War timer counts down")
	val WAR_TIMER: SoundHolder = this.registerSoundEvents("war_timer")

	@DataGenerateSound
	@DataGenerateLanguage(name = "War timer counts up")
	val WAR_TIMER_UP: SoundHolder = this.registerSoundEvents("war_timer_up")

	@DataGenerateSound(stream = true)
	val ULTRAMARINE: SoundHolder = this.registerSoundEvents("ultramarine")

	@DataGenerateSound(stream = true)
	val KSP_BUILDMODE: SoundHolder = this.registerSoundEvents("ksp_buildmode")

	@DataGenerateSound(stream = true)
	val GOING_UP: SoundHolder = this.registerSoundEvents("going_up")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Speed")
	val MACH_ONE: SoundHolder = this.registerSoundEvents("mach_one")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Speedy")
	val MACH_TWO: SoundHolder = this.registerSoundEvents("mach_two")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Speedier")
	val MACH_THREE: SoundHolder = this.registerSoundEvents("mach_three")

	@DataGenerateSound
	@DataGenerateLanguage(name = "PEPPINO SPEED")
	val MACH_FOUR: SoundHolder = this.registerSoundEvents("mach_four")

	@DataGenerateSound
	@DataGenerateLanguage(name = "death.mp3")
	val KILL_ENEMY: SoundHolder = this.registerSoundEvents("kill_enemy")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Slam")
	val PUNCH: SoundHolder = this.registerSoundEvents("punch")

	@DataGenerateSound
	@DataGenerateLanguage(name = "1x Multi")
	val DOUBLE_1X: SoundHolder = this.registerSoundEvents("double_or_nothing_1x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "2x Multi")
	val DOUBLE_2X: SoundHolder = this.registerSoundEvents("double_or_nothing_2x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "3x Multi")
	val DOUBLE_3X: SoundHolder = this.registerSoundEvents("double_or_nothing_3x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "4x Multi!")
	val DOUBLE_4X: SoundHolder = this.registerSoundEvents("double_or_nothing_4x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "5x Multi!!")
	val DOUBLE_5X: SoundHolder = this.registerSoundEvents("double_or_nothing_5x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "6x Multi!!!")
	val DOUBLE_6X: SoundHolder = this.registerSoundEvents("double_or_nothing_6x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "7x!!!")
	val DOUBLE_7X: SoundHolder = this.registerSoundEvents("double_or_nothing_7x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "!!! 8x !!!")
	val DOUBLE_8X: SoundHolder = this.registerSoundEvents("double_or_nothing_8x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "!!!!! 9X !!!!!")
	val DOUBLE_9X: SoundHolder = this.registerSoundEvents("double_or_nothing_9x")

	@DataGenerateSound
	@DataGenerateLanguage(name = "JACKPOT!")
	val DOUBLE_JACKPOT: SoundHolder = this.registerSoundEvents("double_or_nothing_jackpot")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Cashout")
	val DOUBLE_CASHOUT: SoundHolder = this.registerSoundEvents("double_or_nothing_cashout")

	@DataGenerateSound
	@DataGenerateLanguage(name = "Nothing...")
	val DOUBLE_NOTHING: SoundHolder = this.registerSoundEvents("double_or_nothing_nothing")

	@DataGenerateSound
	@DataGenerateLanguage(name = "nyoooom")
	val SPEED_COIL: SoundHolder = this.registerSoundEvents("speed_coil")

	@DataGenerateSound
	@DataGenerateLanguage(name = "[low gravity noise]")
	val GRAVITY_COIL: SoundHolder = this.registerSoundEvents("gravity_coil")
	private fun registerSoundEvents(name: String): SoundHolder {
		val registry = this.getRegistry(Registries.SOUND_EVENT)
		return registry.register(name) { -> SoundEvent.createVariableRangeEvent(modLocation(name)) }
	}

	/**
	 * @return the resource key of this [SoundEvent]
	 */
	fun SoundEvent.toResourceKey(): ResourceKey<SoundEvent> = BuiltInRegistries.SOUND_EVENT.getResourceKey(this).get()
}