package bread.mod.breadmod.neoforge.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockAndItemModel
import bread.mod.breadmod.neoforge.registry.item.ModItemsForge.registerBlockItem
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.food.Foods
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredRegister

internal object ModBlocksForge {
    val BLOCK_REGISTRY_FORGE: DeferredRegister.Blocks = DeferredRegister.createBlocks(ModMainCommon.MOD_ID)

    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Bread Block")
    @DataGenerateLanguage("es_es", "Bloque De Pan")
    val BREAD_BLOCK = BLOCK_REGISTRY_FORGE.registerBlockItem(
        "bread_block", { BreadBlockForge() }, Item.Properties().food(
            FoodProperties.Builder()
                .nutrition(Foods.BREAD.nutrition * 9)
                .saturationModifier(Foods.BREAD.saturation * 9)
                .build()
        )
    )
}