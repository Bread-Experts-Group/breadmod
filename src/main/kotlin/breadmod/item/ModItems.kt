package breadmod.item

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.core.ArmorTiers
import breadmod.datagen.provider.ModSounds
import breadmod.gui.BreadModCreativeTab
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

@Suppress("UNUSED", "SpellCheckingInspection")
object ModItems {
    val REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BreadMod.ID)

    val BREAD_BLOCK_ITEM by REGISTRY.registerObject("bread_block") {
        BlockItem(ModBlocks.BREAD_BLOCK, Item.Properties().stacksTo(64).tab(BreadModCreativeTab)) }
    val TEST_BREAD by REGISTRY.registerObject("test_bread") {
        TestBreadItem() }
    // I somehow reverted every change i made since last commit im gonna AAAAA
    val BREAD_HELMET by REGISTRY.registerObject("bread_helmet") {
        ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.HEAD, Item.Properties().tab(BreadModCreativeTab)) }
    val BREAD_CHESTPLATE by REGISTRY.registerObject("bread_chestplate") {
    ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.CHEST, Item.Properties().tab(BreadModCreativeTab)) }
    val BREAD_LEGGINGS by REGISTRY.registerObject("bread_legggings") {
        ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.LEGS, Item.Properties().tab(BreadModCreativeTab)) }
    val BREAD_BOOTS by REGISTRY.registerObject("bread_boots") {
        ArmorItem(ArmorTiers.BREAD_ARMOR, EquipmentSlot.FEET, Item.Properties().tab(BreadModCreativeTab)) }



    val TEST_DISC by REGISTRY.registerObject("music_disc_test") {
        RecordItem(1, ModSounds.TEST_SOUND, Item.Properties().stacksTo(1).tab(BreadModCreativeTab), 7900)
    }

}



// NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE