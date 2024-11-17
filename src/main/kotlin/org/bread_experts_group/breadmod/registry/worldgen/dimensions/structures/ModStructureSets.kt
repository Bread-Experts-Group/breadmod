package org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures

import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.structure.StructureSet
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType
import org.bread_experts_group.breadmod.BreadMod
import kotlin.math.absoluteValue
import kotlin.random.Random

object ModStructureSets {
    private fun createKey(id: String) = ResourceKey.create(Registries.STRUCTURE_SET, BreadMod.modLocation(id))

    private val RANDOM = Random(39245)
    private val FARMHOUSE: ResourceKey<StructureSet> = createKey("farmhouse")

    fun bootstrap(pContext: BootstrapContext<StructureSet>) {
        val structureHolder = pContext.lookup(Registries.STRUCTURE)
        pContext.register(
            FARMHOUSE, StructureSet(
                structureHolder.getOrThrow(ModStructures.FARMHOUSE), RandomSpreadStructurePlacement(
                    34, 8, RandomSpreadType.LINEAR, RANDOM.nextInt().absoluteValue
                )
            )
        )
    }
}