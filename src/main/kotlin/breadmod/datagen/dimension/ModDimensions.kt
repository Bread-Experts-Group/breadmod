package breadmod.datagen.dimension

import breadmod.BreadMod.modLocation
import breadmod.datagen.dimension.worldgen.ModBiomes
import breadmod.datagen.dimension.worldgen.ModNoiseGenerators
import com.mojang.datafixers.util.Pair
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.biome.Climate.ParameterList
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import java.util.*

typealias BootstrapContext<T> = BootstapContext<T>

object ModDimensions {
    fun register(
        name: String,
        dimensionType: (key: ResourceKey<DimensionType>, location: ResourceLocation) -> DimensionType,
        climateParameterListBuilder: ClimateParameterListBuilder,
        noiseGenerationSettings: ResourceKey<NoiseGeneratorSettings>
    ) = modLocation(name).let {
        ModDimensionEntry(
            it,
            ResourceKey.create(Registries.DIMENSION_TYPE, it).let { typeKey -> typeKey to dimensionType.invoke(typeKey, it) },
            ResourceKey.create(Registries.LEVEL_STEM, it),
            climateParameterListBuilder,
            noiseGenerationSettings
        )
    }

    @Suppress("unused")
    val BREAD = register("bread", { _, _ ->
        DimensionType(
            OptionalLong.empty(),
            true,
            false,
            false,
            true,
            4.0,
            true,
            false,
            -64, // minY TODO,
            2048, // maxY TODO
            2048,
            BlockTags.INFINIBURN_OVERWORLD,
            BuiltinDimensionTypes.NETHER_EFFECTS,
            8F,
            DimensionType.MonsterSettings(
                false,
                false,
                ConstantInt.of(0),
                0
            )
        )
    }, { holderGetter ->
        ParameterList(
            listOf(
                Pair.of(
                    Climate.parameters(
                        0.9F,
                        0.5f,
                        0.0F,
                        0.25f,
                        1.0f,
                        1.0F,
                        0.175F
                    ), holderGetter.getOrThrow(ModBiomes.BREAD)
                )
            )
        )
    }, ModNoiseGenerators.BREAD_FLOATING_ISLANDS)

    fun bootstrapDimensionTypes(ctx: BootstrapContext<DimensionType>) =
        ModDimensionEntry.entries.forEach { ctx.register(it.dimensionType.first, it.dimensionType.second) }
    fun bootstrapLevelStems(ctx: BootstrapContext<LevelStem>) {
        val noiseSettings = ctx.lookup(Registries.NOISE_SETTINGS)
        val mnbspList = ctx.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
        val holderGetter = ctx.lookup(Registries.DIMENSION_TYPE)

        val biomeGetter = ctx.lookup(Registries.BIOME)

        ModDimensionEntry.entries.forEach {
            ctx.register(it.levelStemKey, LevelStem(
                holderGetter.getOrThrow(it.dimensionType.first),
                NoiseBasedChunkGenerator(
                    it.climateParameterListBuilder.let { builder ->
                        if(builder != null) MultiNoiseBiomeSource.createFromList(builder(biomeGetter))
                        else MultiNoiseBiomeSource.createFromPreset(mnbspList.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD))
                    },
                    noiseSettings.getOrThrow(it.noiseSettings)
                )
            ))
        }
    }

    init { ModDimensionEntry.frozen = true }
}