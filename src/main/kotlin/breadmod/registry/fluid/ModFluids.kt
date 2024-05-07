package breadmod.registry.fluid

import breadmod.ModMain
import breadmod.block.BreadLiquidBlock
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModFluids {
    val deferredRegister: DeferredRegister<Fluid> = DeferredRegister.create(ForgeRegistries.FLUIDS, ModMain.ID)
    private fun registerWithBucket(id: String, sourceSupplier: () -> FlowingFluid, flowingSupplier: () -> FlowingFluid, itemProperties: Item.Properties, blockProperties: BlockBehaviour.Properties, fluidProperties: FluidType.Properties): FluidHolder {
        val source = deferredRegister.register(id, sourceSupplier)
        val flowing = deferredRegister.register("${id}_flowing", flowingSupplier)
        val block = ModBlocks.deferredRegister.register(id) { LiquidBlock({ source.get() }, blockProperties).also { ModBlocks.ModBlockLoot.dropNone.add(it) } }
        return FluidHolder(
            source, flowing,
            ModItems.deferredRegister.register("${id}_bucket") { BucketItem({ source.get() }, itemProperties) },
            block,
            FluidType(fluidProperties)
        )
    }

    val BREAD_LIQUID = registerWithBucket(
        "bread_liquid",
        { BreadLiquidBlock.Source() }, { BreadLiquidBlock.Flowing() },
        Item.Properties().stacksTo(1),
        BlockBehaviour.Properties.copy(Blocks.WATER),
        FluidType.Properties.create()
    )

    data class FluidHolder(
        val source: RegistryObject<FlowingFluid>, val flowing: RegistryObject<FlowingFluid>,
        val bucket: RegistryObject<BucketItem>, val block: RegistryObject<LiquidBlock>,
        val type: FluidType
    )
}