package breadmod.datagen.dimension

import breadmod.BreadMod
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.biome.*
import java.awt.Color

object ModBiomes {
    private val entries = mutableListOf<Pair<ResourceKey<Biome>, Biome>>()

    fun register(
        name: String, biomeBuilder: (id: ResourceLocation) -> Biome
    ) = BreadMod.modLocation(name).let {
        val pair = ResourceKey.create(Registries.BIOME, it) to biomeBuilder(it)
        entries.add(pair)
        return@let pair
    }

    val BREAD = register("bread") { _ ->
        Biome.BiomeBuilder()
            .hasPrecipitation(false)
            .temperatureAdjustment(Biome.TemperatureModifier.NONE)
            .specialEffects(
                BiomeSpecialEffects.Builder()
                    .fogColor(Color(0, 150,120).rgb)
                    .skyColor(Color(0, 255,192).rgb)
                    .waterColor(Color(200,100,0).rgb)
                    .waterFogColor(Color(200, 75, 0).rgb)
                    .ambientParticle(AmbientParticleSettings(ParticleTypes.SMOKE, 0.1F))
                    .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
                    .build()
            )
            .generationSettings(BiomeGenerationSettings.EMPTY)
            .mobSpawnSettings(MobSpawnSettings.EMPTY)
            .temperature(1.0F)
            .downfall(0.0F)
            .build()
    }

    fun bootstrapBiomes(ctx: BootstrapContext<Biome>) = entries.forEach { ctx.register(it.first, it.second) }
}