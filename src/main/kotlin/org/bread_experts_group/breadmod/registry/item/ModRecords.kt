package org.bread_experts_group.breadmod.registry.item

import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.JukeboxSong
import net.neoforged.neoforge.registries.DeferredHolder
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.sound.ModSounds

/**
 * [JukeboxSong] entry holder and registry.
 */
object ModRecords {
	val SECRET_HOPPIN: ResourceKey<JukeboxSong> = this.create("secret_hoppin")

	/**
	 * Registers and generates the "jukebox_song" entries in the mod's data folder
	 */
	fun bootstrap(context: BootstrapContext<JukeboxSong>) {
		this.register(context, this.SECRET_HOPPIN, ModSounds.SECRET_HOPPIN, 381f, 15, "secret_hoppin")
	}

	fun register(
		context: BootstrapContext<JukeboxSong>,
		key: ResourceKey<JukeboxSong>,
		soundEvent: DeferredHolder<SoundEvent, SoundEvent>,
		lengthInSeconds: Float,
		comparatorOutput: Int,
		description: String
	): Holder.Reference<JukeboxSong> = context.register(
		key, JukeboxSong(
			soundEvent,
			modTranslatable("item", "music_disc_$description", "desc"),
			lengthInSeconds,
			comparatorOutput
		)
	)

	fun create(name: String): ResourceKey<JukeboxSong> =
		ResourceKey.create(Registries.JUKEBOX_SONG, modLocation(name))
}