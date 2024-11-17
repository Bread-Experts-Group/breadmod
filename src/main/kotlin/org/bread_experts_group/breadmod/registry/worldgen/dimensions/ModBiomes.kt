package org.bread_experts_group.breadmod.registry.worldgen.dimensions

import net.minecraft.core.HolderGetter
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.placement.NetherPlacements
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.Musics
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.registry.sound.ModSounds.toResourceKey
import java.awt.Color

typealias biomeBuilder = (HolderGetter<PlacedFeature>, HolderGetter<ConfiguredWorldCarver<*>>, HolderGetter<SoundEvent>) -> Biome

object ModBiomes {
    private val entries = mutableListOf<Pair<ResourceKey<Biome>, biomeBuilder>>()

    fun register(name: String, builder: biomeBuilder): ResourceKey<Biome> = ResourceKey.create(
        Registries.BIOME,
        modLocation(name)
    ).also { entries.add(it to builder) }

    val BREAD = register("bread") { features, carvers, sound ->
        Biome.BiomeBuilder()
            .hasPrecipitation(false)
            .temperatureAdjustment(Biome.TemperatureModifier.NONE)
            .specialEffects(
                BiomeSpecialEffects.Builder()
                    .fogColor(Color(0, 150, 120).rgb)
                    .skyColor(Color(0, 255, 192).rgb)
                    .waterColor(Color(200, 100, 0).rgb)
                    .waterFogColor(Color(200, 75, 0).rgb)
                    .ambientParticle(AmbientParticleSettings(ParticleTypes.ASH, 0.05F))
                    .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
                    .backgroundMusic(
                        Musics.createGameMusic(
                            sound.getOrThrow(ModSounds.ULTRAMARINE.get().toResourceKey())
                        )
                    )
                    .build()
            )
            .generationSettings(
                BiomeGenerationSettings.Builder(features, carvers)
                    .addFeature(GenerationStep.Decoration.RAW_GENERATION, ModFeatures.BAUXITE_ORE)
                    .addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, NetherPlacements.DELTA)
                    .build()
            )
            .mobSpawnSettings(MobSpawnSettings.EMPTY)
            .temperature(1.0F)
            .downfall(0.0F)
            .build()
    }

    fun bootstrapBiomes(ctx: BootstrapContext<Biome>) {
        val features = ctx.lookup(Registries.PLACED_FEATURE)
        val carvers = ctx.lookup(Registries.CONFIGURED_CARVER)
        val sound = ctx.lookup(BuiltInRegistries.SOUND_EVENT.key())
        entries.forEach { ctx.register(it.first, it.second(features, carvers, sound)) }
    }
}