package org.bread_experts_group.breadmod.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class ModSoundDefinitionsProvider(
    packOutput: PackOutput,
    existingFileHelper: ExistingFileHelper
) : SoundDefinitionsProvider(packOutput, Breadmod.ID, existingFileHelper) {
    override fun registerSounds() {
        this.add(
            ModSounds.TEST_SOUND, definition()
                .subtitle("sound.${Breadmod.ID}.test_sound")
                .with(sound(modLocation("test_sound")).volume(1f).stream())
        )
        this.add(
            ModSounds.HAPPY_BLOCK_FUSE, definition()
                .subtitle("sound.${Breadmod.ID}.happy_block_fuse")
                .with(sound(modLocation("happy_block_fuse")).volume(1f))
        )
        this.add(
            ModSounds.ULTRAMARINE, definition()
                .with(sound(modLocation("ultramarine")).volume(1f).stream())
        )
        this.add(
            ModSounds.POW, definition()
                .subtitle("sound.${Breadmod.ID}.pow")
                .with(sound(modLocation("pow")).volume(1f))
        )
        this.add(
            ModSounds.SCREAM, definition()
                .subtitle("sound.${Breadmod.ID}.scream")
                .with(sound(modLocation("scream")).volume(1f))
        )
        this.add(
            ModSounds.TOOL_GUN, definition()
                .subtitle("sound.${Breadmod.ID}.tool_gun")
                .with(
                    sound(modLocation("tool_gun_one")).volume(1f),
                    sound(modLocation("tool_gun_two")).volume(1f)
                )
        )
        this.add(
            ModSounds.MINIGUN, definition()
                .subtitle("sound.${Breadmod.ID}.minigun")
                .with(sound(modLocation("minigun")).volume(1f))
        )
        this.add(
            ModSounds.HELL_NAW, definition()
                .subtitle("sound.${Breadmod.ID}.hell_naw")
                .with(sound(modLocation("hell_naw")).volume(1f))
        )
        this.add(
            ModSounds.WAR_TIMER, definition()
                .subtitle("sound.${Breadmod.ID}.war_timer")
                .with(sound(modLocation("war_timer")).volume(1f))
        )
        this.add(
            ModSounds.WAR_TIMER_UP, definition()
                .subtitle("sound.${Breadmod.ID}.war_timer_up")
                .with(sound(modLocation("war_timer_up")).volume(0.8f))
        )
        this.add(
            ModSounds.MACH_ONE, definition()
                .subtitle("sound.${Breadmod.ID}.mach_one")
                .with(sound(modLocation("mach_one")).volume(1.0f))
        )
        this.add(
            ModSounds.MACH_TWO, definition()
                .subtitle("sound.${Breadmod.ID}.mach_two")
                .with(sound(modLocation("mach_two")).volume(1.0f))
        )
        this.add(
            ModSounds.MACH_THREE, definition()
                .subtitle("sound.${Breadmod.ID}.mach_three")
                .with(sound(modLocation("mach_three")).volume(1.0f))
        )
        this.add(
            ModSounds.MACH_FOUR, definition()
                .subtitle("sound.${Breadmod.ID}.mach_four")
                .with(sound(modLocation("mach_four")).volume(1.0f))
        )
        this.add(
            ModSounds.PUNCH, definition()
                .subtitle("sound.${Breadmod.ID}.punch")
                .with(sound(modLocation("punch")).volume(1.0f))
        )
        this.add(
            ModSounds.KILL_ENEMY, definition()
                .subtitle("sound,${Breadmod.ID}.kill_enemy")
                .with(sound(modLocation("kill_enemy")).volume(1.0f))
        )
    }
}