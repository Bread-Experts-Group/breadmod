package bread.mod.breadmod.registry.item

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modTranslatable
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.ItemModelType
import bread.mod.breadmod.item.BreadShieldItem
import bread.mod.breadmod.item.DopedBreadItem
import bread.mod.breadmod.item.TestBreadItem
import bread.mod.breadmod.item.UltimateBreadItem
import bread.mod.breadmod.item.tool.KnifeItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block

@Suppress("unused")
object ModItems {
    val ITEM_REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.ITEM)

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Test Bread")
    val TEST_BREAD: RegistrySupplier<Item> = ITEM_REGISTRY.register("test_bread") { TestBreadItem() }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Ultimate Bread")
    val ULTIMATE_BREAD: RegistrySupplier<Item> = ITEM_REGISTRY.register("ultimate_bread") { UltimateBreadItem() }

    @DataGenerateLanguage("en_us", "Bread Shield")
    val BREAD_SHIELD: RegistrySupplier<BreadShieldItem> = ITEM_REGISTRY.register("bread_shield") { BreadShieldItem() }

    @DataGenerateItemModel(ItemModelType.WITH_OVERLAY)
    @DataGenerateLanguage("en_us", "Doped Bread")
    val DOPED_BREAD: RegistrySupplier<DopedBreadItem> = ITEM_REGISTRY.register("doped_bread") { DopedBreadItem() }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Toasted Bread")
    val TOASTED_BREAD: RegistrySupplier<Item> = ITEM_REGISTRY.register("toasted_bread") {
        Item(Item.Properties().food(
            FoodProperties.Builder().nutrition(7).saturationModifier(0.8f).build())
        )
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Slice")
    val BREAD_SLICE: RegistrySupplier<Item> = ITEM_REGISTRY.register("bread_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(2).fast().build()))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Toast Slice")
    val TOAST_SLICE: RegistrySupplier<Item> = ITEM_REGISTRY.register("toast_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(5).fast().build()))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Flour")
    val FLOUR: RegistrySupplier<Item> = ITEM_REGISTRY.register("flour") { Item(Item.Properties()) }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Dough")
    val DOUGH: RegistrySupplier<Item> = ITEM_REGISTRY.register("dough") { Item(Item.Properties()) }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Die")
    val DIE: RegistrySupplier<Item> = ITEM_REGISTRY.register("die") { Item(Item.Properties()) }

    @DataGenerateItemModel(ItemModelType.HANDHELD)
    @DataGenerateLanguage("en_us", "Knife")
    val KNIFE: RegistrySupplier<KnifeItem> = ITEM_REGISTRY.register("knife") { KnifeItem(Tiers.IRON) }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bagel")
    val BAGEL: RegistrySupplier<Item> = ITEM_REGISTRY.register("bagel") {
        Item(
            Item.Properties()
                .food(FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).build())
        )
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Half Bagel")
    val HALF_BAGEL: RegistrySupplier<Item> = ITEM_REGISTRY.register("half_bagel") {
        object : Item(
            Properties()
                .food(FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).fast().build())
        ) {
            override fun appendHoverText(
                stack: ItemStack,
                context: TooltipContext,
                tooltipComponents: MutableList<Component>,
                tooltipFlag: TooltipFlag
            ) {
                tooltipComponents.add(modTranslatable("item", "half_bagel", "description")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC))
            }
        }
    }

    internal fun DeferredRegister<Block>.registerBlockItem(
        id: String,
        block: () -> Block,
        properties: Item.Properties
    ): RegistrySupplier<BlockItem> = this.register(id, block).let { blockSupply ->
        ITEM_REGISTRY.register(id) { BlockItem(blockSupply.get(), properties) }
    }

    internal fun DeferredRegister<Block>.registerBlockItem(
        id: String,
        block: () -> Block,
        item: (block: Block) -> BlockItem
    ): RegistrySupplier<BlockItem> = this.register(id, block).let { blockSupply ->
        ITEM_REGISTRY.register(id) { item(blockSupply.get()) }
    }
}