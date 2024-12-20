package org.bread_experts_group.breadmod.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class ModSoundDefinitionsProvider(
	packOutput : PackOutput,
	existingFileHelper : ExistingFileHelper
) : SoundDefinitionsProvider(packOutput, BreadMod.ID, existingFileHelper) {
	override fun registerSounds() {
		this.add(
			ModSounds.TEST_SOUND, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.test_sound")
				.with(SoundDefinitionsProvider.sound(modLocation("test_sound")).volume(1f).stream())
		)
		this.add(
			ModSounds.HAPPY_BLOCK_FUSE, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.happy_block_fuse")
				.with(SoundDefinitionsProvider.sound(modLocation("happy_block_fuse")).volume(1f))
		)
		this.add(
			ModSounds.ULTRAMARINE, SoundDefinitionsProvider.definition()
				.with(SoundDefinitionsProvider.sound(modLocation("ultramarine")).volume(1f).stream())
		)
		this.add(
			ModSounds.POW, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.pow")
				.with(SoundDefinitionsProvider.sound(modLocation("pow")).volume(1f))
		)
		this.add(
			ModSounds.SCREAM, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.scream")
				.with(SoundDefinitionsProvider.sound(modLocation("scream")).volume(1f))
		)
		this.add(
			ModSounds.TOOL_GUN, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.tool_gun")
				.with(
					SoundDefinitionsProvider.sound(modLocation("tool_gun_one")).volume(1f),
					SoundDefinitionsProvider.sound(modLocation("tool_gun_two")).volume(1f)
				)
		)
		this.add(
			ModSounds.MINIGUN, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.minigun")
				.with(SoundDefinitionsProvider.sound(modLocation("minigun")).volume(1f))
		)
		this.add(
			ModSounds.HELL_NAW, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.hell_naw")
				.with(SoundDefinitionsProvider.sound(modLocation("hell_naw")).volume(1f))
		)
		this.add(
			ModSounds.WAR_TIMER, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.war_timer")
				.with(SoundDefinitionsProvider.sound(modLocation("war_timer")).volume(1f))
		)
		this.add(
			ModSounds.WAR_TIMER_UP, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.war_timer_up")
				.with(SoundDefinitionsProvider.sound(modLocation("war_timer_up")).volume(0.8f))
		)
		this.add(
			ModSounds.MACH_ONE, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.mach_one")
				.with(SoundDefinitionsProvider.sound(modLocation("mach_one")).volume(1.0f))
		)
		this.add(
			ModSounds.MACH_TWO, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.mach_two")
				.with(SoundDefinitionsProvider.sound(modLocation("mach_two")).volume(1.0f))
		)
		this.add(
			ModSounds.MACH_THREE, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.mach_three")
				.with(SoundDefinitionsProvider.sound(modLocation("mach_three")).volume(1.0f))
		)
		this.add(
			ModSounds.MACH_FOUR, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.mach_four")
				.with(SoundDefinitionsProvider.sound(modLocation("mach_four")).volume(1.0f))
		)
		this.add(
			ModSounds.PUNCH, SoundDefinitionsProvider.definition()
				.subtitle("sound.${BreadMod.ID}.punch")
				.with(SoundDefinitionsProvider.sound(modLocation("punch")).volume(1.0f))
		)
		this.add(
			ModSounds.KILL_ENEMY, SoundDefinitionsProvider.definition()
				.subtitle("sound,${BreadMod.ID}.kill_enemy")
				.with(SoundDefinitionsProvider.sound(modLocation("kill_enemy")).volume(1.0f))
		)
	}
}