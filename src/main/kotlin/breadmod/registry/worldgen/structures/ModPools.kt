package breadmod.registry.worldgen.structures

import breadmod.ModMain
import com.mojang.datafixers.util.Pair
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.data.worldgen.Pools
import net.minecraft.data.worldgen.ProcessorLists
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool

object ModPools {
    private fun createKey(id: String) = ResourceKey.create(Registries.TEMPLATE_POOL, ModMain.modLocation(id))

    val FARMHOUSE_POOL: ResourceKey<StructureTemplatePool> = createKey("farmhouse")

    fun bootstrap(pContext: BootstapContext<StructureTemplatePool>) {
        val processorHolder = pContext.lookup(Registries.PROCESSOR_LIST)
        val templateHolder = pContext.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY)
        pContext.register(
            FARMHOUSE_POOL, StructureTemplatePool(
                templateHolder,
                listOf(Pair.of(StructurePoolElement.legacy("farmhouse/farmhouse", processorHolder.getOrThrow(ProcessorLists.MOSSIFY_10_PERCENT)), 50)),
                StructureTemplatePool.Projection.TERRAIN_MATCHING
            ))
    }
}