package breadmod.item

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.item.armor.ArmorTiers
import breadmod.datagen.provider.ModSounds
import breadmod.gui.BreadModCreativeTab
import breadmod.item.armor.BreadShieldItem
import breadmod.item.armor.BreadArmorItem
import breadmod.item.tools.ToolTiers
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

@Suppress("UNUSED")
object ModItems {
    private val DEFAULT_ITEM_PROPERTIES: Item.Properties = Item.Properties().tab(BreadModCreativeTab)
    // // //
    val REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BreadMod.ID)

    // Items
    val TEST_BREAD by REGISTRY.registerObject("test_bread") { TestBreadItem() }
    val DOPED_BREAD by REGISTRY.registerObject("doped_bread") { DopedBreadItem() }
    val BREAD_SLICE by REGISTRY.registerObject("bread_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(1).build()).tab(BreadModCreativeTab))
    }
    val BREAD_CRUMBS by REGISTRY.registerObject("bread_crumbs") { Item(DEFAULT_ITEM_PROPERTIES) }

    // Blocks
    val BREAD_BLOCK_ITEM by REGISTRY.registerObject("bread_block") {
        BlockItem(ModBlocks.BREAD_BLOCK, DEFAULT_ITEM_PROPERTIES) }
    val REINFORCED_BREAD_BLOCK_ITEM by REGISTRY.registerObject("reinforced_bread_block") {
        BlockItem(ModBlocks.REINFORCED_BREAD_BLOCK, DEFAULT_ITEM_PROPERTIES) }

    // Armor
    val BREAD_HELMET by REGISTRY.registerObject("bread_helmet") {
        BreadArmorItem(ArmorTiers.BREAD, EquipmentSlot.HEAD, DEFAULT_ITEM_PROPERTIES) }
    val BREAD_CHESTPLATE by REGISTRY.registerObject("bread_chestplate") {
        BreadArmorItem(ArmorTiers.BREAD, EquipmentSlot.CHEST, DEFAULT_ITEM_PROPERTIES) }
    val BREAD_LEGGINGS by REGISTRY.registerObject("bread_leggings") {
        BreadArmorItem(ArmorTiers.BREAD, EquipmentSlot.LEGS, DEFAULT_ITEM_PROPERTIES) }
    val BREAD_BOOTS by REGISTRY.registerObject("bread_boots") {
        BreadArmorItem(ArmorTiers.BREAD, EquipmentSlot.FEET, DEFAULT_ITEM_PROPERTIES) }

    val BREAD_SHIELD by REGISTRY.registerObject("bread_shield") { BreadShieldItem() }

    // Tools // Speed modifier slows down the tool based on how much of a negative value you give it (Maybe it's a multiplier?)
    val BREAD_PICKAXE by REGISTRY.registerObject("bread_pickaxe") {
        PickaxeItem(ToolTiers.BREAD,1,-2.5f, DEFAULT_ITEM_PROPERTIES) }
    val BREAD_SHOVEL by REGISTRY.registerObject("bread_shovel") {
        ShovelItem(ToolTiers.BREAD, 1.2f,-2.8f, DEFAULT_ITEM_PROPERTIES) }
    val BREAD_AXE by REGISTRY.registerObject("bread_axe") {
        AxeItem(ToolTiers.BREAD, 4.0f,-3f, DEFAULT_ITEM_PROPERTIES) }
    val BREAD_HOE by REGISTRY.registerObject("bread_hoe") {
        HoeItem(ToolTiers.BREAD, 1,-2.8f, DEFAULT_ITEM_PROPERTIES) }
    val BREAD_SWORD by REGISTRY.registerObject("bread_sword") {
        SwordItem(ToolTiers.BREAD, 2,-2.5f, DEFAULT_ITEM_PROPERTIES) }

    // Music Discs
    val TEST_DISC by REGISTRY.registerObject("music_disc_test") {
        RecordItem(15, ModSounds.TEST_SOUND, Item.Properties()
            .stacksTo(1)
            .tab(BreadModCreativeTab)
            .rarity(Rarity.RARE),
            7900)
    }
}