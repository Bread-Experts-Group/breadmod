package breadmod.registry.worldgen.dimensions

import breadmod.BootstrapContext
import breadmod.ModMain
import breadmod.registry.block.ModBlocks
import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest

typealias PlacedFeatureBuilder = (configuredFeaturesHolder: HolderGetter<ConfiguredFeature<*, *>>) -> PlacedFeature
typealias ConfiguredFeatureBuilder = () -> ConfiguredFeature<*,*>

object ModFeatures {
    private val entries = Pair(
        mutableListOf<Pair<ResourceKey<PlacedFeature>, PlacedFeatureBuilder>>(),
        mutableListOf<Pair<ResourceKey<ConfiguredFeature<*, *>>, ConfiguredFeatureBuilder>>()
    )
    private fun registerPlacedFeature(name: String, builder: PlacedFeatureBuilder): ResourceKey<PlacedFeature> = ResourceKey.create(
        Registries.PLACED_FEATURE,
        ModMain.modLocation(name)
    ).also { entries.first.add(it to builder) }
    private fun registerConfiguredFeature(name: String, builder: ConfiguredFeatureBuilder): ResourceKey<ConfiguredFeature<*, *>> = ResourceKey.create(
        Registries.CONFIGURED_FEATURE,
        ModMain.modLocation(name)
    ).also { entries.second.add(it to builder) }

    private val BAUXITE_ORE_CONFIGURED = registerConfiguredFeature("bauxite") {
        ConfiguredFeature(
            Feature.ORE, OreConfiguration(
                listOf(
                    OreConfiguration.target(
                        TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                        ModBlocks.BAUXITE_ORE.get().block.defaultBlockState()
                    )
                ),
                20
            )
        )
    }

    val BAUXITE_ORE = registerPlacedFeature("bauxite") {
        PlacedFeature(
            it.getOrThrow(BAUXITE_ORE_CONFIGURED), listOf(
                HeightRangePlacement.uniform(
                    VerticalAnchor.absolute(0),
                    VerticalAnchor.absolute(128)
                )
            )
        )
    }

    fun bootstrapConfiguredFeatures(ctx: BootstrapContext<ConfiguredFeature<*, *>>) {
        entries.second.forEach { ctx.register(it.first, it.second()) }
    }
    fun bootstrapPlacedFeatures(ctx: BootstrapContext<PlacedFeature>) {
        val cfHolder = ctx.lookup(Registries.CONFIGURED_FEATURE)
        entries.first.forEach { ctx.register(it.first, it.second(cfHolder)) }
    }
}