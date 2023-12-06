package breadmod.datagen.provider.lang

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.item.ModItems
import net.minecraft.data.DataGenerator
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.LanguageProvider

fun String.idToName() = substringAfterLast('.').split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }
// Transforms type.mod_id.example_object into Example Object
fun LanguageProvider.add(block: Block) = add(block, block.descriptionId.idToName())
fun LanguageProvider.add(item: Item) = add(item, item.descriptionId.idToName())

class USEnglishLanguageProvider(generator: DataGenerator) : LanguageProvider(generator, BreadMod.ID, "en_us") {
    override fun addTranslations() {
        add("itemGroup.${BreadMod.ID}", "The Bread Mod")
        // Blocks
        add(ModBlocks.BREAD_BLOCK)
        add(ModBlocks.REINFORCED_BREAD_BLOCK)
        // Items
        add(ModItems.TEST_BREAD)
        add("item.breadmod.test_bread.desc", "Identical to bread on the outside - tumors on the inside.")
        add(ModItems.BREAD_HELMET)
        add(ModItems.BREAD_CHESTPLATE)
        add(ModItems.BREAD_LEGGINGS)
        add(ModItems.BREAD_BOOTS)
        add(ModItems.BREAD_SHIELD)
        add("item.breadmod.bread_shield.desc", "Not repairable. Not by normal means at least.")
        add(ModItems.TEST_DISC, "Test Music Disc")
        add("item.breadmod.music_disc_test.desc", "Test Description")
    }
}