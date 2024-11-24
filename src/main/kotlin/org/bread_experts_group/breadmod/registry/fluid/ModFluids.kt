package org.bread_experts_group.breadmod.registry.fluid

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.block.actual.BreadLiquidBlock
import org.bread_experts_group.breadmod.datagen.ModBlockLootProvider
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.function.Supplier

object ModFluids {
    val FLUID_REGISTRY: DeferredRegister<Fluid> = DeferredRegister.create(Registries.FLUID, BreadMod.ID)
    val FLUID_TYPE_REGISTRY: DeferredRegister<FluidType> =
        DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, BreadMod.ID)

    private fun <S : BaseFlowingFluid, F : BaseFlowingFluid> registerWithBucket(
        id: String,
        sourceSupplier: () -> S,
        flowingSupplier: () -> F,
        itemProperties: Item.Properties,
        blockProperties: BlockBehaviour.Properties,
        fluidProperties: FluidType.Properties
    ): FluidHolder<S, F> {
        val source = FLUID_REGISTRY.register(id, sourceSupplier)
        val flowing = FLUID_REGISTRY.register("flowing_$id", flowingSupplier)
        val block = ModBlocks.BLOCK_REGISTRY.register(id) { ->
            LiquidBlock(
                source.get(),
                blockProperties
            ).also { ModBlockLootProvider.dropNone.add(it) }
        }

        val fluidType: Supplier<FluidType> = FLUID_TYPE_REGISTRY.register(id) { -> FluidType(fluidProperties) }

        return FluidHolder(
            source, flowing,
            ModItems.ITEM_REGISTRY.register("${id}_bucket") { -> BucketItem(source.get(), itemProperties) },
            block,
            fluidType
        )
    }

    val BREAD_LIQUID = registerWithBucket(
        "bread_liquid",
        { BreadLiquidBlock.Source() }, { BreadLiquidBlock.Flowing() },
        Item.Properties().stacksTo(1),
        BlockBehaviour.Properties.ofFullCopy(Blocks.WATER),
        FluidType.Properties.create()
    )

    data class FluidHolder<S : BaseFlowingFluid, F : BaseFlowingFluid>(
        val source: Supplier<S>, val flowing: Supplier<F>,
        val bucket: DeferredItem<BucketItem>, val block: Supplier<LiquidBlock>,
        val type: Supplier<FluidType>
    )
}