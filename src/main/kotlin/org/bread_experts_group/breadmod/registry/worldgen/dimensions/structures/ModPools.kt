package org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures

import com.mojang.datafixers.util.Pair
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.Pools
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import org.bread_experts_group.breadmod.BreadMod

object ModPools {
    private fun createKey(id: String) = ResourceKey.create(Registries.TEMPLATE_POOL, BreadMod.modLocation(id))

    val FARMHOUSE_POOL: ResourceKey<StructureTemplatePool> = createKey("farmhouse")

    fun bootstrap(pContext: BootstrapContext<StructureTemplatePool>) {
        val templateHolder = pContext.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY)
        pContext.register(
            FARMHOUSE_POOL, StructureTemplatePool(
                templateHolder,
                listOf(Pair.of(StructurePoolElement.single("breadmod:farmhouse"), 50)),
                StructureTemplatePool.Projection.RIGID
            )
        )
    }
}