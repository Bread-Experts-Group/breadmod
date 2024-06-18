package breadmodadvanced.registry.block

import breadmod.registry.block.ModBlocks.registerBlockItem
import breadmodadvanced.ModMainAdv
import breadmodadvanced.block.DieselGeneratorBlock
import breadmodadvanced.registry.item.ModItemsAdv
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlocksAdv {
    internal val deferredRegister: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMainAdv.ID)

    val DIESEL_GENERATOR = deferredRegister.registerBlockItem(
        ModItemsAdv.deferredRegister,
        "diesel_generator",
        { DieselGeneratorBlock() },
        Item.Properties()
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