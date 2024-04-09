package breadmod.item.registry

import breadmod.BreadMod
import breadmod.datagen.ModSounds
import breadmod.item.DopedBreadItem
import breadmod.item.TestBreadItem
import breadmod.item.armor.ArmorTiers
import breadmod.item.armor.BreadArmorItem
import breadmod.item.tools.BreadShieldItem
import breadmod.item.tools.ToolTiers
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

@Suppress("unused")
object ModItems {
    val REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BreadMod.ID)

    val TEST_BREAD: RegistryObject<TestBreadItem> = REGISTRY.register("test_bread") { TestBreadItem() }

    val BREAD_SHIELD: RegistryObject<BreadShieldItem> = REGISTRY.register("bread_shield") { BreadShieldItem() }
    val DOPED_BREAD: RegistryObject<DopedBreadItem> = REGISTRY.register("doped_bread") { DopedBreadItem() }
    val BREAD_CRUMBS: RegistryObject<Item> = REGISTRY.register("bread_crumbs") { Item(Item.Properties()) }
    val BREAD_SLICE: RegistryObject<Item> = REGISTRY.register("bread_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(1).fast().build())) }
    val FLOUR: RegistryObject<Item> = REGISTRY.register("flour") {Item(Item.Properties())}
    val DOUGH: RegistryObject<Item> = REGISTRY.register("dough") {Item(Item.Properties())}

    // Armor
    val BREAD_HELMET: RegistryObject<BreadArmorItem> = REGISTRY.register("bread_helmet") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.HELMET, Item.Properties()) }
    val BREAD_CHESTPLATE: RegistryObject<BreadArmorItem> = REGISTRY.register("bread_chestplate") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.CHESTPLATE, Item.Properties()) }
    val BREAD_LEGGINGS: RegistryObject<BreadArmorItem> = REGISTRY.register("bread_leggings") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.LEGGINGS, Item.Properties()) }
    val BREAD_BOOTS: RegistryObject<BreadArmorItem> = REGISTRY.register("bread_boots") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.BOOTS, Item.Properties()) }

    // Tools // Speed modifier slows down the tool based on how much of a negative value you give it (Maybe it's a multiplier?)
    val BREAD_PICKAXE: RegistryObject<PickaxeItem> = REGISTRY.register("bread_pickaxe") {
        PickaxeItem(ToolTiers.BREAD,1,-2.5f, Item.Properties()) }
    val BREAD_SHOVEL: RegistryObject<ShovelItem> = REGISTRY.register("bread_shovel") {
        ShovelItem(ToolTiers.BREAD, 1.2f,-2.8f, Item.Properties()) }
    val BREAD_AXE: RegistryObject<AxeItem> = REGISTRY.register("bread_axe") {
        AxeItem(ToolTiers.BREAD, 4.0f,-3f, Item.Properties()) }
    val BREAD_HOE: RegistryObject<HoeItem> = REGISTRY.register("bread_hoe") {
        HoeItem(ToolTiers.BREAD, 1,-2.8f, Item.Properties()) }
    val BREAD_SWORD: RegistryObject<SwordItem> = REGISTRY.register("bread_sword") {
        SwordItem(ToolTiers.BREAD, 2,-2.5f, Item.Properties()) }

    // Reinforced Tools
    val RF_BREAD_PICKAXE: RegistryObject<PickaxeItem> = REGISTRY.register("reinforced_bread_pickaxe") {
        PickaxeItem(ToolTiers.RF_BREAD, 2, -1f, Item.Properties()) }
    val RF_BREAD_SHOVEL: RegistryObject<ShovelItem> = REGISTRY.register("reinforced_bread_shovel") {
        ShovelItem(ToolTiers.RF_BREAD, 1.2f,-2.8f, Item.Properties()) }
    val RF_BREAD_AXE: RegistryObject<AxeItem> = REGISTRY.register("reinforced_bread_axe") {
        AxeItem(ToolTiers.RF_BREAD, 4.0f,-3f, Item.Properties()) }
    val RF_BREAD_HOE: RegistryObject<HoeItem> = REGISTRY.register("reinforced_bread_hoe") {
        HoeItem(ToolTiers.RF_BREAD, 1,-2.8f, Item.Properties()) }
    val RF_BREAD_SWORD: RegistryObject<SwordItem> = REGISTRY.register("reinforced_bread_sword") {
        SwordItem(ToolTiers.RF_BREAD, 2,-2.5f, Item.Properties()) }


    val TEST_DISC: RegistryObject<RecordItem> = REGISTRY.register("music_disc_test") {
        RecordItem(15, ModSounds.TEST_SOUND, Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE),
            7900)
    }
}