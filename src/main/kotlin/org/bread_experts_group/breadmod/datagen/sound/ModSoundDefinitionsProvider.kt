package org.bread_experts_group.breadmod.datagen.sound

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.SoundDefinition
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import net.neoforged.neoforge.registries.DeferredHolder
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner

class ModSoundDefinitionsProvider(
	packOutput: PackOutput,
	existingFileHelper: ExistingFileHelper
) : SoundDefinitionsProvider(packOutput, BreadMod.Companion.ID, existingFileHelper) {
	private val registryScanner: LibraryScanner = Registry::class.java.`package`.getScanner()

	override fun registerSounds() {
		val events = mutableMapOf<DeferredHolder<SoundEvent, SoundEvent>, MutableList<SoundDefinition.Sound>>()
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateSound>().forEach { (annotation, data) ->
			@Suppress("UNCHECKED_CAST")
			val event = data as DeferredHolder<SoundEvent, SoundEvent>
			val sounds = events.getOrPut(event) { mutableListOf() }
			val sound = SoundDefinition.Sound.sound(
				if (annotation.sound == "<null>") event.id else ResourceLocation.parse(annotation.sound),
				SoundDefinition.SoundType.SOUND
			)
			sound.volume(annotation.volume)
			sound.stream(annotation.stream)
			sounds.add(sound)
		}
		events.forEach { (event, sounds) ->
			val definition = SoundDefinition.definition()
			definition.subtitle("sound.${BreadMod.Companion.ID}.${event.id.path}")
			definition.with(*sounds.toTypedArray())
			this.add(event, definition)
		}
	}
}