package org.bread_experts_group.breadmod.registry.worldgen.dimensions

import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

typealias PlacedFeatureBuilder = (configuredFeaturesHolder: HolderGetter<ConfiguredFeature<*, *>>) -> PlacedFeature
typealias ConfiguredFeatureBuilder = () -> ConfiguredFeature<*, *>

object ModFeatures {
	private val entries = Pair(
		mutableListOf<Pair<ResourceKey<PlacedFeature>, PlacedFeatureBuilder>>(),
		mutableListOf<Pair<ResourceKey<ConfiguredFeature<*, *>>, ConfiguredFeatureBuilder>>()
	)

	fun registerPlacedFeature(name: String, builder: PlacedFeatureBuilder): ResourceKey<PlacedFeature> =
		ResourceKey.create(
			Registries.PLACED_FEATURE,
			modLocation(name)
		).also { this.entries.first.add(it to builder) }

	fun registerConfiguredFeature(
		name: String,
		builder: ConfiguredFeatureBuilder
	): ResourceKey<ConfiguredFeature<*, *>> = ResourceKey.create(
		Registries.CONFIGURED_FEATURE,
		modLocation(name)
	).also { this.entries.second.add(it to builder) }

//	private val BAUXITE_ORE_CONFIGURED = this.registerConfiguredFeature("bauxite") {
//		ConfiguredFeature(
//			Feature.ORE, OreConfiguration(
//				listOf(
//					OreConfiguration.target(
//						TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
//						ModBlocks.BAUXITE_ORE.get().block.defaultBlockState()
//					)
//				),
//				20
//			)
//		)
//	}
//	val BAUXITE_ORE: ResourceKey<PlacedFeature> = this.registerPlacedFeature("bauxite") {
//		PlacedFeature(
//			it.getOrThrow(this.BAUXITE_ORE_CONFIGURED), listOf(
//				HeightRangePlacement.uniform(
//					VerticalAnchor.absolute(0),
//					VerticalAnchor.absolute(128)
//				)
//			)
//		)
//	}

	fun bootstrapConfiguredFeatures(ctx: BootstrapContext<ConfiguredFeature<*, *>>) {
		this.entries.second.forEach { ctx.register(it.first, it.second()) }
	}

	fun bootstrapPlacedFeatures(ctx: BootstrapContext<PlacedFeature>) {
		val cfHolder = ctx.lookup(Registries.CONFIGURED_FEATURE)
		this.entries.first.forEach { ctx.register(it.first, it.second(cfHolder)) }
	}
}