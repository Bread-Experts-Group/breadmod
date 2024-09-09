package bread.mod.breadmod.registry.item

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modTranslatable
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.tag.DataGenerateTag
import bread.mod.breadmod.item.BreadShieldItem
import bread.mod.breadmod.item.DopedBreadItem
import bread.mod.breadmod.item.TestBreadItem
import bread.mod.breadmod.item.UltimateBreadItem
import bread.mod.breadmod.item.tool.KnifeItem
import bread.mod.breadmod.item.tool.ToolTiers
import bread.mod.breadmod.registry.item.armor.ArmorMaterials
import bread.mod.breadmod.registry.item.armor.BreadArmorItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorItem.Type
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.HoeItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PickaxeItem
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.item.SwordItem
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

    //    @DataGenerateItemModel(ItemModelType.WITH_OVERLAY)
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

    //    @DataGenerateItemModel(ItemModelType.HANDHELD)
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

    // todo bread amulets go here eventually

//    @DataGenerateItemModel(ItemModelType.WITH_OVERLAY)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Helmet")
    val BREAD_HELMET: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_helmet") {
        BreadArmorItem(Type.HELMET)
    }

//    @DataGenerateItemModel(ItemModelType.WITH_OVERLAY)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Chestplate")
    val BREAD_CHESTPLATE: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_chestplate") {
        BreadArmorItem(Type.CHESTPLATE)
    }

//    @DataGenerateItemModel(ItemModelType.WITH_OVERLAY)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Leggings")
    val BREAD_LEGGINGS: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_leggings") {
        BreadArmorItem(Type.LEGGINGS)
    }

//    @DataGenerateItemModel(ItemModelType.WITH_OVERLAY)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Boots")
    val BREAD_BOOTS: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_boots") {
        BreadArmorItem(Type.BOOTS)
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Helmet")
    val REINFORCED_BREAD_HELMET: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_helmet") {
        ArmorItem(ArmorMaterials.RF_BREAD, Type.HELMET, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Chestplate")
    val REINFORCED_BREAD_CHESTPLATE: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_chestplate") {
        ArmorItem(ArmorMaterials.RF_BREAD, Type.CHESTPLATE, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Leggings")
    val REINFORCED_BREAD_LEGGINGS: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_leggings") {
        ArmorItem(ArmorMaterials.RF_BREAD, Type.LEGGINGS, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Boots")
    val REINFORCED_BREAD_BOOTS: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_boots") {
        ArmorItem(ArmorMaterials.RF_BREAD, Type.BOOTS, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Pickaxe")
    val BREAD_PICKAXE: RegistrySupplier<PickaxeItem> = ITEM_REGISTRY.register("bread_pickaxe") {
        PickaxeItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Shovel")
    val BREAD_SHOVEL: RegistrySupplier<ShovelItem> = ITEM_REGISTRY.register("bread_shovel") {
        ShovelItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Axe")
    val BREAD_AXE: RegistrySupplier<AxeItem> = ITEM_REGISTRY.register("bread_axe") {
        AxeItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Hoe")
    val BREAD_HOE: RegistrySupplier<HoeItem> = ITEM_REGISTRY.register("bread_hoe") {
        HoeItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Sword")
    val BREAD_SWORD: RegistrySupplier<SwordItem> = ITEM_REGISTRY.register("bread_sword") {
        SwordItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Pickaxe")
    val RF_BREAD_PICKAXE: RegistrySupplier<PickaxeItem> = ITEM_REGISTRY.register("reinforced_bread_pickaxe") {
        PickaxeItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Shovel")
    val RF_BREAD_SHOVEL: RegistrySupplier<ShovelItem> = ITEM_REGISTRY.register("reinforced_bread_shovel") {
        ShovelItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Axe")
    val RF_BREAD_AXE: RegistrySupplier<AxeItem> = ITEM_REGISTRY.register("reinforced_bread_axe") {
        AxeItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Hoe")
    val RF_BREAD_HOE: RegistrySupplier<HoeItem> = ITEM_REGISTRY.register("reinforced_bread_hoe") {
        HoeItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Sword")
    val RF_BREAD_SWORD: RegistrySupplier<SwordItem> = ITEM_REGISTRY.register("reinforced_bread_sword") {
        SwordItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
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