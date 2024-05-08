package breadmod.registry.worldgen.structures

import breadmod.ModMain
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.structure.StructureSet
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType
import kotlin.math.absoluteValue
import kotlin.random.Random

object ModStructureSets {
    private fun createKey(id: String) = ResourceKey.create(Registries.STRUCTURE_SET, ModMain.modLocation(id))

    private val RANDOM = Random(39245)
    private val FARMHOUSE: ResourceKey<StructureSet> = createKey("farmhouse")
    
    fun bootstrap(pContext: BootstapContext<StructureSet>) {
        val structureHolder = pContext.lookup(Registries.STRUCTURE)
        pContext.register(
            FARMHOUSE, StructureSet(structureHolder.getOrThrow(ModStructures.FARMHOUSE), RandomSpreadStructurePlacement(
                50, 15, RandomSpreadType.LINEAR, RANDOM.nextInt().absoluteValue
            ))
        )
    }
}