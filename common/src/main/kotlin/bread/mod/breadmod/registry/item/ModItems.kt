package bread.mod.breadmod.registry.item

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modTranslatable
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.language.DataGenerateTooltipLang
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.recipe.special.DataGenerateToastingRecipe
import bread.mod.breadmod.datagen.tag.DataGenerateTag
import bread.mod.breadmod.item.*
import bread.mod.breadmod.item.TieredBreadAmuletItem.BreadAmuletType
import bread.mod.breadmod.item.armor.BreadArmorItem
import bread.mod.breadmod.item.armor.ChefHatItem
import bread.mod.breadmod.item.armor.ModArmorMaterials
import bread.mod.breadmod.item.tool.KnifeItem
import bread.mod.breadmod.item.tool.ToolTiers
import bread.mod.breadmod.item.toolGun.ToolGunItem
import bread.mod.breadmod.item.toolGun.ToolGunItem.Companion.TOOL_GUN_DEF
import bread.mod.breadmod.registry.tag.RecordTags
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.ArmorItem.Type
import net.minecraft.world.level.block.Block

// todo port item tags
@Suppress("unused")
object ModItems {
    val ITEM_REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.ITEM)

    // todo port over features
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Test Bread")
    @DataGenerateTooltipLang("en_us", "Identical to bread on the outside - tumors on the inside.")
    val TEST_BREAD: RegistrySupplier<TestBreadItem> = ITEM_REGISTRY.register("test_bread") { TestBreadItem() }

    // todo port over features
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Ultimate Bread")
    val ULTIMATE_BREAD: RegistrySupplier<UltimateBreadItem> =
        ITEM_REGISTRY.register("ultimate_bread") { UltimateBreadItem() }

    @DataGenerateLanguage("en_us", "Bread Shield")
    @DataGenerateTooltipLang("en_us", "How is this supposed to protect against anything?")
    val BREAD_SHIELD: RegistrySupplier<BreadShieldItem> = ITEM_REGISTRY.register("bread_shield") { BreadShieldItem() }

    // todo port over features
    @DataGenerateItemModel(DataGenerateItemModel.Type.DOUBLE_LAYERED)
    @DataGenerateLanguage("en_us", "Doped Bread")
    @DataGenerateTooltipLang("en_us", "Contains trace amounts of neurotoxin")
    val DOPED_BREAD: RegistrySupplier<DopedBreadItem> = ITEM_REGISTRY.register("doped_bread") { DopedBreadItem() }

    @DataGenerateTag("minecraft:item", "breadmod:toastable")
    @DataGenerateToastingRecipe("bread_toasting", "minecraft:bread", time = 160)
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Toasted Bread")
    val TOASTED_BREAD: RegistrySupplier<Item> = ITEM_REGISTRY.register("toasted_bread") {
        Item(
            Item.Properties().food(
                FoodProperties.Builder().nutrition(7).saturationModifier(0.8f).build()
            )
        )
    }

    @DataGenerateTag("minecraft:item", "breadmod:toastable")
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Slice")
    val BREAD_SLICE: RegistrySupplier<Item> = ITEM_REGISTRY.register("bread_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(2).fast().build()))
    }

    @DataGenerateTag("minecraft:item", "breadmod:toastable")
    @DataGenerateToastingRecipe("slice_toasting", "breadmod:bread_slice")
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

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
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
    @DataGenerateTooltipLang("en_us", "What? you thought it was gonna be sliced like a normal bagel?")
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
                tooltipComponents.add(
                    modTranslatable("item", "half_bagel", "tooltip")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                )
            }
        }
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Alumina")
    val ALUMINA: RegistrySupplier<Item> = ITEM_REGISTRY.register("alumina") { Item(Item.Properties()) }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Test Music Disc")
    val TEST_RECORD: RegistrySupplier<Item> = ITEM_REGISTRY.register("music_disc_test") {
        Item(Item.Properties().jukeboxPlayable(RecordTags.TEST_SOUND).stacksTo(1))
    }

    // todo port over features (config exists now)
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Amulet")
    val BREAD_AMULET: RegistrySupplier<TieredBreadAmuletItem> = ITEM_REGISTRY.register("bread_amulet") {
        TieredBreadAmuletItem(BreadAmuletType.NORMAL, 500)
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Chef Hat")
    @DataGenerateTooltipLang("en_us", "IS THAT A PIZZA TOWER REFERENCE???")
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    val CHEF_HAT: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("chef_hat") { ChefHatItem() }

    // todo port over features (applies to all 4 bread armors)
    @DataGenerateItemModel(DataGenerateItemModel.Type.DOUBLE_LAYERED)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Helmet")
    val BREAD_HELMET: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_helmet") {
        BreadArmorItem(Type.HELMET)
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.DOUBLE_LAYERED)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Chestplate")
    val BREAD_CHESTPLATE: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_chestplate") {
        BreadArmorItem(Type.CHESTPLATE)
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.DOUBLE_LAYERED)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Leggings")
    val BREAD_LEGGINGS: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_leggings") {
        BreadArmorItem(Type.LEGGINGS)
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.DOUBLE_LAYERED)
    @DataGenerateTag("minecraft:item", "minecraft:dyeable")
    @DataGenerateLanguage("en_us", "Bread Boots")
    val BREAD_BOOTS: RegistrySupplier<BreadArmorItem> = ITEM_REGISTRY.register("bread_boots") {
        BreadArmorItem(Type.BOOTS)
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Helmet")
    val REINFORCED_BREAD_HELMET: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_helmet") {
        ArmorItem(ModArmorMaterials.RF_BREAD, Type.HELMET, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Chestplate")
    val REINFORCED_BREAD_CHESTPLATE: RegistrySupplier<ArmorItem> =
        ITEM_REGISTRY.register("reinforced_bread_chestplate") {
            ArmorItem(ModArmorMaterials.RF_BREAD, Type.CHESTPLATE, Item.Properties().stacksTo(1))
        }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Leggings")
    val REINFORCED_BREAD_LEGGINGS: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_leggings") {
        ArmorItem(ModArmorMaterials.RF_BREAD, Type.LEGGINGS, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Boots")
    val REINFORCED_BREAD_BOOTS: RegistrySupplier<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_boots") {
        ArmorItem(ModArmorMaterials.RF_BREAD, Type.BOOTS, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Bread Pickaxe")
    val BREAD_PICKAXE: RegistrySupplier<PickaxeItem> = ITEM_REGISTRY.register("bread_pickaxe") {
        PickaxeItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Bread Shovel")
    val BREAD_SHOVEL: RegistrySupplier<ShovelItem> = ITEM_REGISTRY.register("bread_shovel") {
        ShovelItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Bread Axe")
    val BREAD_AXE: RegistrySupplier<AxeItem> = ITEM_REGISTRY.register("bread_axe") {
        AxeItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Bread Hoe")
    val BREAD_HOE: RegistrySupplier<HoeItem> = ITEM_REGISTRY.register("bread_hoe") {
        HoeItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Bread Sword")
    val BREAD_SWORD: RegistrySupplier<SwordItem> = ITEM_REGISTRY.register("bread_sword") {
        SwordItem(ToolTiers.BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Reinforced Bread Pickaxe")
    val RF_BREAD_PICKAXE: RegistrySupplier<PickaxeItem> = ITEM_REGISTRY.register("reinforced_bread_pickaxe") {
        PickaxeItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Reinforced Bread Shovel")
    val RF_BREAD_SHOVEL: RegistrySupplier<ShovelItem> = ITEM_REGISTRY.register("reinforced_bread_shovel") {
        ShovelItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Reinforced Bread Axe")
    val RF_BREAD_AXE: RegistrySupplier<AxeItem> = ITEM_REGISTRY.register("reinforced_bread_axe") {
        AxeItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Reinforced Bread Hoe")
    val RF_BREAD_HOE: RegistrySupplier<HoeItem> = ITEM_REGISTRY.register("reinforced_bread_hoe") {
        HoeItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Reinforced Bread Sword")
    val RF_BREAD_SWORD: RegistrySupplier<SwordItem> = ITEM_REGISTRY.register("reinforced_bread_sword") {
        SwordItem(ToolTiers.RF_BREAD, Item.Properties().stacksTo(1))
    }

    @DataGenerateItemModel(DataGenerateItemModel.Type.HANDHELD)
    @DataGenerateLanguage("en_us", "Wrench")
    val WRENCH: RegistrySupplier<WrenchItem> = ITEM_REGISTRY.register("wrench") { WrenchItem() }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Bullet")
    val BREAD_BULLET_ITEM: RegistrySupplier<Item> = ITEM_REGISTRY.register("bread_bullet") { Item(Item.Properties()) }

    // todo shootProjectile exists now, fix up this item to work again
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Bread Gun")
    val BREAD_GUN_ITEM: RegistrySupplier<ProjectileWeaponItem> = ITEM_REGISTRY.register("bread_gun") { BreadGunItem() }

    // todo re-implementation of features
    @DataGenerateLanguage("en_us", "Tool Gun")
    val TOOL_GUN: RegistrySupplier<Item> = ITEM_REGISTRY.register(TOOL_GUN_DEF) { ToolGunItem() }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Caprispin")
    val CAPRISPIN: RegistrySupplier<Item> = ITEM_REGISTRY.register("caprispin") {
        object : Item(
            Properties()
                .food(
                    FoodProperties.Builder()
                        .alwaysEdible()
                        .nutrition(20)
                        .effect(MobEffectInstance(MobEffects.LEVITATION, 100, 20), 1f)
                        .build()
                )
                .rarity(Rarity.EPIC)
        ) {
            override fun getUseAnimation(pStack: ItemStack): UseAnim = UseAnim.DRINK
        }
    }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Toaster Heating Element")
    val TOASTER_HEATING_ELEMENT: RegistrySupplier<Item> =
        ITEM_REGISTRY.register("toaster_heating_element") { Item(Item.Properties()) }

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "THE CREATURE")
    val CREATURE: RegistrySupplier<Item> = ITEM_REGISTRY.register("creature") { Item(Item.Properties()) }

    // todo port over screen and menu
    @DataGenerateLanguage("en_us", "Certificate")
    @DataGenerateTooltipLang("en_us", "Wouldn't be official without some light blue dye, would it?")
    val CERTIFICATE: RegistrySupplier<Item> = ITEM_REGISTRY.register("certificate") { CertificateItem() }

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