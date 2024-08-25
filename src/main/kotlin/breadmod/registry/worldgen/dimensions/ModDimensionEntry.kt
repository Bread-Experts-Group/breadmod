package breadmod.registry.worldgen.dimensions

import net.minecraft.core.Holder
import net.minecraft.core.HolderGetter
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings

typealias ClimateParameterListBuilder = ((HolderGetter<Biome>) -> Climate.ParameterList<Holder<Biome>>)

data class ModDimensionEntry(
    val effectLocation: ResourceLocation,
    val dimensionType: Pair<ResourceKey<DimensionType>, DimensionType>,
    val levelStemKey: ResourceKey<LevelStem>,

    val climateParameterListBuilder: ClimateParameterListBuilder? = null,
    val noiseSettings: ResourceKey<NoiseGeneratorSettings> = NoiseGeneratorSettings.OVERWORLD
) {
    companion object {
        internal val entries = mutableListOf<ModDimensionEntry>()
        internal var frozen = false
    }

    init {
        if (frozen) throw IllegalStateException("Dimension registered after entry list froze")
        entries.add(this)
    }
}