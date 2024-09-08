package bread.mod.breadmod.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.block.BreadBlock
import bread.mod.breadmod.block.util.FlammableBlock
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockAndItemModel
import bread.mod.breadmod.item.util.FuelItem
import bread.mod.breadmod.registry.item.ModItems.registerBlockItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.food.Foods
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour

/**
 * Blocks for the base bread mod.
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
object ModBlocks {
    /**
     * The deferred register for blocks.
     * @author Logan McLean
     * @since 1.0.0
     */
    val BLOCK_REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.BLOCK)

    @FlammableBlock(30, 60)
    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Bread Block")
    @DataGenerateLanguage("es_es", "Bloque De Pan")
    val BREAD_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bread_block", { BreadBlock() },
        Item.Properties()
            .food(
                FoodProperties.Builder()
                    .nutrition(Foods.BREAD.nutrition * 9)
                    .saturationModifier(Foods.BREAD.saturation * 9)
                    .build()
            )
    )

    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Block")
    val REINFORCED_BREAD_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "reinforced_bread_block",
        { Block(BlockBehaviour.Properties.ofFullCopy(
            Blocks.NETHERITE_BLOCK).strength(25f, 1200f))
        },
        Item.Properties()
    )

    @FuelItem((80 * 20) * 4)
    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Charcoal Block")
    val CHARCOAL_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "charcoal_block", {
            Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK).ignitedByLava())
        },
        Item.Properties()
    )
}