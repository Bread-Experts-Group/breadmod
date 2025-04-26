package org.bread_experts_group.breadmod.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.SoundDefinition
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class ModSoundDefinitionsProvider(
	packOutput: PackOutput,
	existingFileHelper: ExistingFileHelper
) : SoundDefinitionsProvider(packOutput, BreadMod.ID, existingFileHelper) {
	override fun registerSounds() {
		this.add(
			ModSounds.TEST_SOUND, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.test_sound")
				.with(sound(modLocation("test_sound")).volume(1f).stream())
		)
		this.add(
			ModSounds.GAS_GAS_GAS, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.gas_gas_gas")
				.with(sound(modLocation("gas_gas_gas")).volume(1f).stream())
		)
		this.add(
			ModSounds.HAPPY_BLOCK_FUSE, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.happy_block_fuse")
				.with(sound(modLocation("happy_block_fuse")).volume(1f))
		)
		this.add(
			ModSounds.ULTRAMARINE, SoundDefinition.definition()
				.with(sound(modLocation("ultramarine")).volume(1f).stream())
		)
		this.add(
			ModSounds.POW, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.pow")
				.with(sound(modLocation("pow")).volume(1f))
		)
		this.add(
			ModSounds.SCREAM, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.scream")
				.with(sound(modLocation("scream")).volume(1f))
		)
		this.add(
			ModSounds.TOOL_GUN, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.tool_gun")
				.with(
					sound(modLocation("tool_gun_one")).volume(1f),
					sound(modLocation("tool_gun_two")).volume(1f)
				)
		)
		this.add(
			ModSounds.MINIGUN, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.minigun")
				.with(sound(modLocation("minigun")).volume(1f))
		)
		this.add(
			ModSounds.HELL_NAW, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.hell_naw")
				.with(sound(modLocation("hell_naw")).volume(1f))
		)
		this.add(
			ModSounds.WAR_TIMER, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.war_timer")
				.with(sound(modLocation("war_timer")).volume(1f))
		)
		this.add(
			ModSounds.WAR_TIMER_UP, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.war_timer_up")
				.with(sound(modLocation("war_timer_up")).volume(0.8f))
		)
		this.add(
			ModSounds.MACH_ONE, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.mach_one")
				.with(sound(modLocation("mach_one")).volume(1.0f))
		)
		this.add(
			ModSounds.MACH_TWO, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.mach_two")
				.with(sound(modLocation("mach_two")).volume(1.0f))
		)
		this.add(
			ModSounds.MACH_THREE, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.mach_three")
				.with(sound(modLocation("mach_three")).volume(1.0f))
		)
		this.add(
			ModSounds.MACH_FOUR, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.mach_four")
				.with(sound(modLocation("mach_four")).volume(1.0f))
		)
		this.add(
			ModSounds.PUNCH, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.punch")
				.with(sound(modLocation("punch")).volume(1.0f))
		)
		this.add(
			ModSounds.KILL_ENEMY, SoundDefinition.definition()
				.subtitle("sound.${BreadMod.ID}.kill_enemy")
				.with(sound(modLocation("kill_enemy")).volume(1.0f))
		)
	}
}