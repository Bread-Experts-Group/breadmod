package breadmod.block.registry

import breadmod.BreadMod
import breadmod.block.*
import breadmod.item.ModItems
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.common.extensions.IForgeItem
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlocks {
    val REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)

    private fun registerBlockItem(id: String, block: () -> Block, properties: Item.Properties): RegistryObject<BlockItem> =
        REGISTRY.register(id, block).let { ModItems.REGISTRY.register(id) { BlockItem(it.get(), properties) } }
    private fun registerBlockItem(id: String, block: () -> Block, item: (block: Block) -> BlockItem): RegistryObject<BlockItem> =
        REGISTRY.register(id, block).let { ModItems.REGISTRY.register(id) { item(it.get()) } }

    val BREAD_BLOCK = registerBlockItem(
        "bread_block",
        { BreadBlock() },
        Item.Properties().also {
            val breadFoodStats = Items.BREAD.getFoodProperties(Items.BREAD.defaultInstance, null)!!
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
        { Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(1f).sound(SoundType.NETHERITE_BLOCK)) },
        Item.Properties().fireResistant()
    )

    val CHARCOAL_BLOCK = registerBlockItem(
        "charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)) },
        { block -> object : BlockItem(block, Properties()), IForgeItem {
            override fun getBurnTime(itemStack: ItemStack?, recipeType: RecipeType<*>?): Int = 1600 * 9
        } }
    )
    val LOW_DENSITY_CHARCOAL_BLOCK = registerBlockItem(
        "ld_charcoal_block",
        { FlammableBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL)) },
        { block -> object : BlockItem(block, Properties()), IForgeItem {
            override fun getBurnTime(itemStack: ItemStack?, recipeType: RecipeType<*>?): Int = 1600 * 4
        } }
    )

    val BREAD_FURNACE_BLOCK = registerBlockItem(
        "bread_furnace",
        { BreadFurnaceBlock() },
        Item.Properties()
    )

    val HAPPY_BLOCK = registerBlockItem(
        "happy_block",
        { HappyBlock() },
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
            dropSelf(BREAD_FURNACE_BLOCK.get().block)
            dropSelf(HAPPY_BLOCK.get().block)
        }
    }
}