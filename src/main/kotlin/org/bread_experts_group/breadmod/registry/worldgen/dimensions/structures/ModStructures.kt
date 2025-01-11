package org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures

import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.BiomeTags
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures.ModPools.FARMHOUSE_POOL

object ModStructures {
	fun createKey(id: String): ResourceKey<Structure> = ResourceKey.create(Registries.STRUCTURE, modLocation(id))
	val FARMHOUSE: ResourceKey<Structure> = this.createKey("farmhouse")
	fun structure(
		biomes: HolderSet<Biome>,
		spawnOverrides: Map<MobCategory, StructureSpawnOverride> = mapOf(),
		step: GenerationStep.Decoration = GenerationStep.Decoration.SURFACE_STRUCTURES,
		adjustment: TerrainAdjustment = TerrainAdjustment.NONE
	): StructureSettings = StructureSettings(biomes, spawnOverrides, step, adjustment)

	fun bootstrap(pContext: BootstrapContext<Structure>) {
		val biomeHolder = pContext.lookup(Registries.BIOME)
		val templateHolder = pContext.lookup(Registries.TEMPLATE_POOL)
		pContext.register(
			this.FARMHOUSE, JigsawStructure(
				this.structure(
					biomeHolder.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS),
					adjustment = TerrainAdjustment.BEARD_THIN
				),
				templateHolder.getOrThrow(FARMHOUSE_POOL), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false,
				Heightmap.Types.WORLD_SURFACE_WG
			)
		)
	}
}