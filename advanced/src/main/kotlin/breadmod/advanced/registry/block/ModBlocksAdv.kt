package breadmod.advanced.registry.block

import breadmod.advanced.ModMainAdv
import breadmod.advanced.registry.item.ModItemsAdv
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlocksAdv {
    val deferredRegister: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMainAdv.ID)

    private fun registerBlockItem(id: String, block: () -> Block, properties: Item.Properties): RegistryObject<BlockItem> =
        deferredRegister.register(id, block).let { ModItemsAdv.deferredRegister.register(id) { BlockItem(it.get(), properties) } }
    private fun registerBlockItem(id: String, block: () -> Block, item: (block: Block) -> BlockItem): RegistryObject<BlockItem> =
        deferredRegister.register(id, block).let { ModItemsAdv.deferredRegister.register(id) { item(it.get()) } }

    val DIESEL_GENERATOR = registerBlockItem(
        "diesel_generator",
        { Block(BlockBehaviour.Properties.of())},
        Item.Properties()
    )

    class ModBlockLootAdv : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags()) {

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