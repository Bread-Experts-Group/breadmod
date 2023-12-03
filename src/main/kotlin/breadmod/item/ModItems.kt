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

    // Blocks
    val BREAD_BLOCK_ITEM by REGISTRY.registerObject("bread_block") {
        BlockItem(ModBlocks.BREAD_BLOCK, Item.Properties().stacksTo(64).tab(BreadModCreativeTab)) }
    val TEST_BREAD by REGISTRY.registerObject("test_bread") {
        TestBreadItem() }

    // Armors
    val BREAD_HELMET by REGISTRY.registerObject("bread_helmet") {
        ArmorItem(ArmorTiers.BREAD, EquipmentSlot.HEAD, Item.Properties().tab(BreadModCreativeTab)) }
    val BREAD_CHESTPLATE by REGISTRY.registerObject("bread_chestplate") {
    ArmorItem(ArmorTiers.BREAD, EquipmentSlot.CHEST, Item.Properties().tab(BreadModCreativeTab)) }
    val BREAD_LEGGINGS by REGISTRY.registerObject("bread_leggings") {
        ArmorItem(ArmorTiers.BREAD, EquipmentSlot.LEGS, Item.Properties().tab(BreadModCreativeTab)) }
    val BREAD_BOOTS by REGISTRY.registerObject("bread_boots") {
        ArmorItem(ArmorTiers.BREAD, EquipmentSlot.FEET, Item.Properties().tab(BreadModCreativeTab)) }

    // Music Discs
    val TEST_DISC by REGISTRY.registerObject("music_disc_test") { // pAnalogOutput is the comparator value the disc outputs
        RecordItem(1, ModSounds.TEST_SOUND, Item.Properties()
            .stacksTo(1)
            .tab(BreadModCreativeTab)
            .rarity(Rarity.RARE),
            7900)
    }

}



// NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE NIGHTMARE