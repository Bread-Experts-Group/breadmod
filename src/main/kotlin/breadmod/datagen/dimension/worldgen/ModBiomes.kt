package breadmod.datagen.dimension.worldgen

import breadmod.ModMain
import breadmod.datagen.dimension.BootstrapContext
import breadmod.registry.sound.ModSounds
import net.minecraft.core.HolderGetter
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.placement.NetherPlacements
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.Musics
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraftforge.registries.ForgeRegistries
import java.awt.Color

typealias BiomeBuilder = (HolderGetter<PlacedFeature>, HolderGetter<ConfiguredWorldCarver<*>>, HolderGetter<SoundEvent>) -> Biome

object ModBiomes {
    private val entries = mutableListOf<Pair<ResourceKey<Biome>, BiomeBuilder>>()

    fun register(name: String, builder: BiomeBuilder): ResourceKey<Biome> = ResourceKey.create(
        Registries.BIOME,
        ModMain.modLocation(name)
    ).also {
        entries.add(it to builder)
    }

    val BREAD = register("bread") { featuresHolderGetter, carversHolderGetter, soundGetter ->
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
                    .backgroundMusic(Musics.createGameMusic(soundGetter.getOrThrow(ModSounds.ULTRAMARINE.key!!)))
                    .build()
            )
            .generationSettings(
                BiomeGenerationSettings.Builder(featuresHolderGetter, carversHolderGetter)
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
        val featuresGetter = ctx.lookup(Registries.PLACED_FEATURE)
        val carversGetter = ctx.lookup(Registries.CONFIGURED_CARVER)
        val soundEventGetter = ctx.lookup(ForgeRegistries.SOUND_EVENTS.registryKey)
        entries.forEach { ctx.register(it.first, it.second(featuresGetter, carversGetter, soundEventGetter)) }
    }
}