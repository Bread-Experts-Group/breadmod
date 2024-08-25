package breadmod.registry.worldgen.structures

import breadmod.ModMain
import breadmod.registry.worldgen.structures.ModPools.FARMHOUSE_POOL
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
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

object ModStructures {
    private fun createKey(id: String) = ResourceKey.create(Registries.STRUCTURE, ModMain.modLocation(id))

    val FARMHOUSE: ResourceKey<Structure> = createKey("farmhouse")

    private fun structure(
        biomes: HolderSet<Biome>,
        spawnOverrides: Map<MobCategory, StructureSpawnOverride> = mapOf(),
        step: GenerationStep.Decoration = GenerationStep.Decoration.SURFACE_STRUCTURES,
        adjustment: TerrainAdjustment = TerrainAdjustment.NONE
    ): StructureSettings = StructureSettings(biomes, spawnOverrides, step, adjustment)

    fun bootstrap(pContext: BootstapContext<Structure>) {
        val biomeHolder = pContext.lookup(Registries.BIOME)
        val templateHolder = pContext.lookup(Registries.TEMPLATE_POOL)
        pContext.register(
            FARMHOUSE, JigsawStructure(
                structure(
                    biomeHolder.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS),
                    adjustment = TerrainAdjustment.BEARD_THIN
                ),
                templateHolder.getOrThrow(FARMHOUSE_POOL), 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false,
                Heightmap.Types.WORLD_SURFACE_WG
            )
        )
    }
}