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
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.ForgeFlowingFluid
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer

object ModFluids {
    internal val deferredRegister: DeferredRegister<Fluid> = DeferredRegister.create(ForgeRegistries.FLUIDS, ModMain.ID)
    internal val deferredTypesRegister: DeferredRegister<FluidType> = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ModMain.ID)

    private fun <S: ForgeFlowingFluid, F: ForgeFlowingFluid> registerWithBucket(
        id: String,
        sourceSupplier: () -> S, flowingSupplier: () -> F,
        itemProperties: Item.Properties, blockProperties: BlockBehaviour.Properties, fluidProperties: FluidType.Properties,
        clientExtensions: IClientFluidTypeExtensions
    ): FluidHolder<S,F> {
        val source = deferredRegister.register(id, sourceSupplier)
        val flowing = deferredRegister.register("flowing_$id", flowingSupplier)
        val block = ModBlocks.deferredRegister.register(id) { LiquidBlock({ source.get() }, blockProperties).also { ModBlocks.ModBlockLoot.dropNone.add(it) } }

        val fluidType: RegistryObject<FluidType> = deferredTypesRegister.register(id) {
            object : FluidType(fluidProperties) {
                override fun initializeClient(consumer: Consumer<IClientFluidTypeExtensions>) = consumer.accept(clientExtensions)
            }
        }

        return FluidHolder(
            source, flowing,
            ModItems.deferredRegister.register("${id}_bucket") { BucketItem({ source.get() }, itemProperties) },
            block,
            fluidType
        )
    }

    val BREAD_LIQUID = registerWithBucket(
        "bread_liquid",
        { BreadLiquidBlock.Source() }, { BreadLiquidBlock.Flowing() },
        Item.Properties().stacksTo(1),
        BlockBehaviour.Properties.copy(Blocks.WATER),
        FluidType.Properties.create(),
        BreadLiquidBlock.ClientExtensions
    )

    data class FluidHolder<S: ForgeFlowingFluid, F: ForgeFlowingFluid>(
        val source: RegistryObject<S>, val flowing: RegistryObject<F>,
        val bucket: RegistryObject<BucketItem>, val block: RegistryObject<LiquidBlock>,
        val type: RegistryObject<FluidType>
    )
}