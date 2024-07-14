package breadmodadvanced.registry.block

import breadmod.util.registerBlockItem
import breadmodadvanced.ModMainAdv
import breadmodadvanced.block.DieselGeneratorBlock
import breadmodadvanced.item.render.DieselGeneratorItemRenderer
import breadmodadvanced.registry.item.ModItemsAdv
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer

object ModBlocksAdv {
    internal val deferredRegister: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMainAdv.ID)

    val DIESEL_GENERATOR = deferredRegister.registerBlockItem(
        ModItemsAdv.deferredRegister,
        "diesel_generator",
        { DieselGeneratorBlock() },
        { block -> object : BlockItem(block, Properties()) {
            override fun initializeClient(consumer: Consumer<IClientItemExtensions>) = consumer.accept(object : IClientItemExtensions {
                override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = DieselGeneratorItemRenderer()
            })
        }}
    )

    internal class ModBlockLootAdv : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags()) {
        override fun getKnownBlocks(): Iterable<Block> {
            return Iterable<Block> {
                deferredRegister.entries
                    .stream()
                    .flatMap<Block> { obj: RegistryObject<Block?> -> obj.stream() }
                    .iterator()
            }
        }

        override fun generate() {
            dropSelf(DIESEL_GENERATOR.get().block)
        }
    }
}