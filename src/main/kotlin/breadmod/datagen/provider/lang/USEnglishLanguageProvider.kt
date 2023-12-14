package breadmod.datagen.provider.lang

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.item.ModItems
import net.minecraft.data.DataGenerator
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.LanguageProvider

class USEnglishLanguageProvider(generator: DataGenerator) : LanguageProvider(generator, BreadMod.ID, "en_us") {
    private fun String.idToName() = substringAfterLast('.').split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }
    // Transforms type.mod_id.example_object into Example Object
    private fun LanguageProvider.add(block: Block) = add(block, block.descriptionId.idToName())
    private fun LanguageProvider.add(item: Item) = add(item, item.descriptionId.idToName())

    override fun addTranslations() {
        add("itemGroup.${BreadMod.ID}", "The Bread Mod")
        // Blocks
        add(ModBlocks.BREAD_BLOCK)
        add(ModBlocks.REINFORCED_BREAD_BLOCK)
        // Items
        add(ModItems.TEST_BREAD)
        add("item.breadmod.test_bread.desc", "Identical to bread on the outside - tumors on the inside.")
        add(ModItems.DOPED_BREAD)
        add("item.breadmod.doped_bread.desc", "contains trace amounts of neurotoxin")
        add(ModItems.BREAD_HELMET)
        add(ModItems.BREAD_CHESTPLATE)
        add(ModItems.BREAD_LEGGINGS)
        add(ModItems.BREAD_BOOTS)
        add(ModItems.BREAD_SHIELD)
        add(ModItems.BREAD_SLICE)
        add("item.breadmod.bread_shield.desc", "No it does NOT look like balsa wood >:(")
        add(ModItems.TEST_DISC, "Test Music Disc")
        add("item.breadmod.music_disc_test.desc", "Test Description")
    }
}