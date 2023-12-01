package breadmod.item

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.core.ArmorTiers
import breadmod.gui.ModCreativeTab
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject


object ModItems {
    val REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BreadMod.ID)

    val BREAD_BLOCK_ITEM by REGISTRY.registerObject("bread_block") {
        BlockItem(ModBlocks.BREAD_BLOCK, Item.Properties().stacksTo(64).tab(ModCreativeTab)) }
    val TEST_BREAD by REGISTRY.registerObject("test_bread") {
        TestBreadItem() }
    // I somehow reverted every change i made since last commit im gonna AAAAA
    val BREAD_HELMET by REGISTRY.registerObject("bread_helmet") {
        ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.HEAD, Item.Properties().tab(ModCreativeTab)) }
    val BREAD_CHESTPLATE by REGISTRY.registerObject("bread_chestplate") {
    ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.CHEST, Item.Properties().tab(ModCreativeTab)) }
    val BREAD_LEGGINGS by REGISTRY.registerObject("bread_legggings") {
        ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.LEGS, Item.Properties().tab(ModCreativeTab)) }
    val BREAD_BOOTS by REGISTRY.registerObject("bread_boots") {
        ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.FEET, Item.Properties().tab(ModCreativeTab)) }
}



// NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE