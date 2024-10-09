package bread.mod.breadmod.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.block.*
import bread.mod.breadmod.block.util.FlammableBlock
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.language.DataGenerateTooltipLang
import bread.mod.breadmod.datagen.loot.DataGenerateLoot
import bread.mod.breadmod.datagen.model.block.DataGenerateButtonBlock
import bread.mod.breadmod.datagen.model.block.DataGenerateLayerBlock
import bread.mod.breadmod.datagen.model.block.Orientable
import bread.mod.breadmod.datagen.model.block.orientable.DataGenerateOrientableBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.simple.DataGenerateCubeAllBlockAndItemModel
import bread.mod.breadmod.datagen.model.block.withExistingParent.DataGenerateWithExistingParentBlockAndItemModel
import bread.mod.breadmod.datagen.recipe.shaped.DataGenerateShapedRecipeThis
import bread.mod.breadmod.datagen.recipe.shapeless.DataGenerateShapelessRecipeExternal
import bread.mod.breadmod.datagen.tag.DataGenerateTag
import bread.mod.breadmod.item.util.FuelItem
import bread.mod.breadmod.registry.item.IRegisterSpecialCreativeTab
import bread.mod.breadmod.registry.item.ModItems.registerBlockItem
import bread.mod.breadmod.registry.menu.ModCreativeTabs
import com.mojang.serialization.MapCodec
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.food.Foods
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor


// todo port the rest of the blocks and machines
// todo port over block tags
/**
 * Blocks for the base bread mod.
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
@Suppress("unused")
object ModBlocks {
    /**
     * The deferred register for blocks.
     * @author Logan McLean
     * @since 1.0.0
     */
    val BLOCK_REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.BLOCK)

    // todo dimension change
    @DataGenerateShapedRecipeThis(
        "bread_block_compression", RecipeCategory.BUILDING_BLOCKS,
        [
            "BB",
            "BB"
        ], 1, ['B'], ["minecraft:bread"]
    )
    @DataGenerateShapelessRecipeExternal(
        "bread_block_decompression",
        "minecraft:bread", RecipeCategory.FOOD,
        4
    )
    @DataGenerateLoot
    @DataGenerateTag("minecraft:block", "minecraft:mineable/hoe")
    @DataGenerateTag("minecraft:block", "breadmod:mineable/knife")
    @DataGenerateTag("minecraft:block", "breadmod:mineable/stone_ore_replaceables")
    @DataGenerateTag("minecraft:block", "c:storage_blocks/bread")
    @DataGenerateCubeAllBlockAndItemModel
    @DataGenerateLanguage("en_us", "Bread Block")
    @DataGenerateLanguage("es_es", "Bloque De Pan")
    @FlammableBlock(30, 60)
    val BREAD_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bread_block", { BreadBlock() },
        Item.Properties()
            .food(
                FoodProperties.Builder()
                    .nutrition(Foods.BREAD.nutrition * 9)
                    .saturationModifier(Foods.BREAD.saturation * 9)
                    .`arch$effect`({
                        MobEffectInstance(
                            MobEffects.LUCK, 500, 1, true, true
                        )
                    }, 100f)
                    .build()
            )
    )

    @DataGenerateLoot
    @DataGenerateCubeAllBlockAndItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Block")
    @DataGenerateTag("minecraft:block", "minecraft:mineable/pickaxe")
    @DataGenerateTag("minecraft:block", "minecraft:beacon_base_blocks")
    val REINFORCED_BREAD_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "reinforced_bread_block",
        {
            Block(
                BlockBehaviour.Properties.ofFullCopy(
                    Blocks.NETHERITE_BLOCK
                ).strength(25f, 1200f)
            )
        },
        Item.Properties()
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateWithExistingParentBlockAndItemModel
    @DataGenerateLoot
    @DataGenerateLanguage("en_us", "OMANEKO")
    val OMANEKO_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "omaneko_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateWithExistingParentBlockAndItemModel
    @DataGenerateLoot
    @DataGenerateLanguage("en_us", "Niko Tenshot")
    val NIKO_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "niko_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateWithExistingParentBlockAndItemModel
    @DataGenerateLoot
    @DataGenerateLanguage("en_us", "Ricardetex-Infinious")
    val RICARD_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "ricard_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateWithExistingParentBlockAndItemModel
    @DataGenerateLoot
    @DataGenerateLanguage("en_us", "Unfunnylad")
    val UNFUNNYLAD_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "unfunnylad_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateTag("minecraft:block", "minecraft:mineable/pickaxe")
    @FuelItem(1600 * 9)
    @DataGenerateLoot
    @DataGenerateCubeAllBlockAndItemModel
    @DataGenerateLanguage("en_us", "Charcoal Block")
    @DataGenerateTag("minecraft:block", "minecraft:mineable/pickaxe")
    @DataGenerateTag("minecraft:block", "c:storage_blocks/charcoal")
    val CHARCOAL_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "charcoal_block", {
            Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK).ignitedByLava())
        },
        Item.Properties()
    )

    @FuelItem(1600 * 4)
    @DataGenerateCubeAllBlockAndItemModel
    @DataGenerateLoot
    @DataGenerateLanguage("en_us", "Low-Density Charcoal Block")
    @DataGenerateTag("minecraft:block", "minecraft:mineable/hoe")
    @DataGenerateTag("minecraft:block", "c:storage_blocks/low_density_charcoal")
    val LOW_DENSITY_CHARCOAL_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "ld_charcoal_block", {
            Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL))
        },
        Item.Properties()
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateWithExistingParentBlockAndItemModel
    @DataGenerateLanguage("en_us", "War Terminal")
    @DataGenerateTooltipLang("en_us", "Prolongs the inevitable.")
    @DataGenerateLoot
    val WAR_TERMINAL: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "war_terminal", { WarTerminalBlock() }, Item.Properties()
    )

    @DataGenerateCubeAllBlockAndItemModel
    @DataGenerateLanguage("en_us", "Random Sound Generator")
    @DataGenerateTooltipLang("en_us", "Uses the power of a die to make random noises")
    @DataGenerateLoot
    val RANDOM_SOUND_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "random_sound_block", { RandomSoundBlock() }, Item.Properties()
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateOrientableBlockAndItemModel("_side", top = "_side")
    @DataGenerateLanguage("en_us", "Sound Block")
    @DataGenerateLoot
    val SOUND_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "sound_block", { SoundBlock() }, { block ->
            object : BlockItem(block, Properties()), IRegisterSpecialCreativeTab {
                override val creativeModeTabs: List<RegistrySupplier<CreativeModeTab>> =
                    listOf(ModCreativeTabs.SPECIALS_TAB)
            }
        }
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateOrientableBlockAndItemModel("_side", "_front", "_top")
    @DataGenerateLanguage("en_us", "Dough Machine")
    @DataGenerateLoot
    val DOUGH_MACHINE: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "dough_machine", { DoughMachineBlock() }, Item.Properties()
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateOrientableBlockAndItemModel("_side", "_front", "_top")
    @DataGenerateLanguage("en_us", "Wheat Crusher")
    @DataGenerateLoot
    val WHEAT_CRUSHER: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "wheat_crusher", { WheatCrusherBlock() }, Item.Properties()
    )

    @Orientable(Orientable.Type.HORIZONTAL)
    @DataGenerateWithExistingParentBlockAndItemModel
    @DataGenerateLanguage("en_us", "Toaster")
    @DataGenerateTooltipLang("en_us", "I wouldn't cook charcoal in it.")
    @DataGenerateLoot
    val TOASTER: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "toaster", { ToasterBlock() }, Item.Properties()
    )

    @DataGenerateButtonBlock
    @DataGenerateLanguage("en_us", "Hell Naw Button")
    @DataGenerateLoot
    val HELL_NAW_BUTTON: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "hell_naw_button", { HellNawButtonBlock() }, Item.Properties()
    )

    @DataGenerateLanguage("en_us", "Happy Block")
    @DataGenerateCubeAllBlockAndItemModel
    @DataGenerateLoot
    @FlammableBlock(20, 40)
    val HAPPY_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "happy_block", { HappyBlock() }, Item.Properties()
    )

    @DataGenerateLanguage("en_us", "Flour Block")
    @DataGenerateTag("minecraft:block", "minecraft:mineable/shovel")
    @DataGenerateCubeAllBlockAndItemModel
    @DataGenerateLoot
    @FlammableBlock(40, 60)
    val FLOUR_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "flour_block", { FlourBlock() }, Item.Properties()
    )

    private class FlourBlock :
        FallingBlock(Properties.of().sound(SoundType.SNOW).ignitedByLava().mapColor(MapColor.COLOR_YELLOW)) {
        val codec: MapCodec<FlourBlock> = simpleCodec { this }
        override fun codec(): MapCodec<out FallingBlock> = codec
    }

    @DataGenerateLanguage("en_us", "Flour Layer")
    @DataGenerateTag("minecraft:block", "minecraft:mineable/shovel")
    @DataGenerateLayerBlock
    @DataGenerateLoot(DataGenerateLoot.Type.SNOW_LAYER, "breadmod:flour_block", "breadmod:flour")
    @FlammableBlock(40, 50)
    val FLOUR_LAYER_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "flour_layer", {
            SnowLayerBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW)
                    .ignitedByLava()
                    .mapColor(MapColor.COLOR_YELLOW)
            )
        }, Item.Properties()
    )

    // todo import the machine blocks
}