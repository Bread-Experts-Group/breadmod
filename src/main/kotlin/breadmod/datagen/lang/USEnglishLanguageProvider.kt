package breadmod.datagen.lang

import breadmod.BreadMod
import breadmod.block.registry.ModBlocks
import breadmod.item.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.LanguageProvider

@Suppress("SpellCheckingInspection")
class USEnglishLanguageProvider(output: PackOutput?, modid: String?, locale: String?) : LanguageProvider(output!!, modid.toString(), locale.toString()) {

    private fun String.idToName() = substringAfterLast('.').split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }
    // Transforms type.mod_id.example_object into Example Object
    private fun LanguageProvider.add(block: Block) = add(block, block.descriptionId.idToName())
    private fun LanguageProvider.add(item: Item) = add(item, item.descriptionId.idToName())
    override fun addTranslations() {
        add(BreadMod.ID + ".itemGroup", "The Bread Mod")
        add(ModBlocks.BREAD_BLOCK.get())
        add(ModItems.TEST_DISC.get(), "Test Music Disc")
        add(ModItems.TEST_BREAD.get())
        add(ModItems.BREAD_SHIELD.get())
        add(ModItems.BREAD_HELMET.get())
        add(ModItems.BREAD_CHESTPLATE.get())
        add(ModItems.BREAD_LEGGINGS.get())
        add(ModItems.BREAD_BOOTS.get())
        add(ModItems.DOPED_BREAD.get())
        add(ModItems.BREAD_SHOVEL.get())
        add(ModItems.BREAD_HOE.get())
        add(ModItems.BREAD_AXE.get())
        add(ModItems.BREAD_PICKAXE.get())
        add(ModItems.BREAD_SWORD.get())
        add(ModItems.RF_BREAD_SHOVEL.get())
        add(ModItems.RF_BREAD_HOE.get())
        add(ModItems.RF_BREAD_AXE.get())
        add(ModItems.RF_BREAD_PICKAXE.get())
        add(ModItems.RF_BREAD_SWORD.get())
        add(ModBlocks.REINFORCED_BREAD_BLOCK.get().block)
        add(ModBlocks.CHARCOAL_BLOCK.get().block)
        add(ModBlocks.BREAD_FURNACE_BLOCK.get().block)
        add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.get().block, "Low-Density Charcoal Block")
        add(ModBlocks.HAPPY_BLOCK.get())
        add("item.breadmod.music_disc_test.desc", "Music Disc Description")
        add("item.breadmod.test_bread.desc", "Identical to bread on the outside - tumors on the inside.")
        add("item.breadmod.bread_shield.desc", "No it does NOT look like balsa wood >:(")
        add("item.breadmod.doped_bread.desc", "contains trace amounts of neurotoxin")
        add("container.bread_furnace", "Bread Furnace")
    }
}