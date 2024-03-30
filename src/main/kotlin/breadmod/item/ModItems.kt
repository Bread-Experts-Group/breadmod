package breadmod.item

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.datagen.ModSounds
import breadmod.item.armor.ArmorTiers
import breadmod.item.armor.BreadArmorItem
import breadmod.item.tools.BreadShieldItem
import breadmod.item.tools.ToolTiers
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

@Suppress("unused", "SpellCheckingInspection")
object ModItems {
    val REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BreadMod.ID)

    val BREAD_BLOCK_ITEM by REGISTRY.registerObject("bread_block") {
        val breadFoodStats = TEST_BREAD.getFoodProperties(TEST_BREAD.defaultInstance, null)!!
        BlockItem(ModBlocks.BREAD_BLOCK, Item.Properties().food(
            FoodProperties.Builder()
            .nutrition(breadFoodStats.nutrition * 9)
            .saturationMod(breadFoodStats.saturationModifier * 9)
            .build()
        )) }
    val REINFORCED_BREAD_BLOCK_ITEM by REGISTRY.registerObject("reinforced_bread_block") {
        BlockItem(ModBlocks.REINFORCED_BREAD_BLOCK, Item.Properties()) }

    val BREAD_SHIELD by REGISTRY.registerObject("bread_shield") { BreadShieldItem() }
    val TEST_BREAD by REGISTRY.registerObject("test_bread") { TestBreadItem() }
    val DOPED_BREAD by REGISTRY.registerObject("doped_bread") { DopedBreadItem() }
    val BREAD_CRUMBS by REGISTRY.registerObject("bread_crumbs") { Item(Item.Properties()) }
    val BREAD_SLICE by REGISTRY.registerObject("bread_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(1).fast().build()))
    }

    // Armor
    val BREAD_HELMET by REGISTRY.registerObject("bread_helmet") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.HELMET, Item.Properties()) }
    val BREAD_CHESTPLATE by REGISTRY.registerObject("bread_chestplate") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.CHESTPLATE, Item.Properties()) }
    val BREAD_LEGGINGS by REGISTRY.registerObject("bread_leggings") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.LEGGINGS, Item.Properties()) }
    val BREAD_BOOTS by REGISTRY.registerObject("bread_boots") {
        BreadArmorItem(ArmorTiers.BREAD, ArmorItem.Type.BOOTS, Item.Properties()) }

    // Tools // Speed modifier slows down the tool based on how much of a negative value you give it (Maybe it's a multiplier?)
    val BREAD_PICKAXE by REGISTRY.registerObject("bread_pickaxe") {
        PickaxeItem(ToolTiers.BREAD,1,-2.5f, Item.Properties()) }
    val BREAD_SHOVEL by REGISTRY.registerObject("bread_shovel") {
        ShovelItem(ToolTiers.BREAD, 1.2f,-2.8f, Item.Properties()) }
    val BREAD_AXE by REGISTRY.registerObject("bread_axe") {
        AxeItem(ToolTiers.BREAD, 4.0f,-3f, Item.Properties()) }
    val BREAD_HOE by REGISTRY.registerObject("bread_hoe") {
        HoeItem(ToolTiers.BREAD, 1,-2.8f, Item.Properties()) }
    val BREAD_SWORD by REGISTRY.registerObject("bread_sword") {
        SwordItem(ToolTiers.BREAD, 2,-2.5f, Item.Properties()) }


    val TEST_DISC by REGISTRY.registerObject("music_disc_test") {
        RecordItem(15, ModSounds.TEST_SOUND, Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE),
            7900)
    }
}