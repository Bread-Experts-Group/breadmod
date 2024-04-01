package breadmod.block

import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlocks {
    val REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)

    private fun registerBlockItem(id: String, block: () -> Block, properties: Item.Properties): RegistryObject<BlockItem> =
        this.REGISTRY.register(id, block).let { ModItems.REGISTRY.register(id) { BlockItem(it.get(), properties) } }

    val BREAD_BLOCK = registerBlockItem(
        "bread_block",
        { BreadBlock() },
        Item.Properties().also {
            val breadFoodStats = Items.BREAD.getFoodProperties(Items.BREAD.defaultInstance, null)
                ?: throw IllegalStateException("Bread is missing it's food traits..?")
            it.food(
                FoodProperties.Builder()
                    .nutrition(breadFoodStats.nutrition * 9)
                    .saturationMod(breadFoodStats.saturationModifier * 9)
                    .build()
            )
        }
    )
    val REINFORCED_BREAD_BLOCK = registerBlockItem(
        "reinforced_bread_block",
        { Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(1f)) },
        Item.Properties().fireResistant()
    )

    val CHARCOAL_BLOCK = registerBlockItem(
        "charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)) },
        Item.Properties()
    )
    val LOW_DENSITY_CHARCOAL_BLOCK = registerBlockItem(
        "ld_charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)) },
        Item.Properties()
    )

    class ModBlockLoot : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags()) {
        override fun getKnownBlocks(): Iterable<Block> {
            return Iterable<Block> {
                REGISTRY.entries
                    .stream()
                    .flatMap<Block> { obj: RegistryObject<Block?> -> obj.stream() }
                    .iterator()
            }
        }

        override fun generate() {
            dropSelf(BREAD_BLOCK.get().block)
            dropSelf(REINFORCED_BREAD_BLOCK.get().block)
            dropSelf(CHARCOAL_BLOCK.get().block)
            dropSelf(LOW_DENSITY_CHARCOAL_BLOCK.get().block)
        }
    }
}