package breadmod.datagen.lang

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.item.ModItems
import net.minecraft.data.PackOutput
import net.minecraftforge.common.data.LanguageProvider

class USEnglishLanguageProvider(output: PackOutput?, modid: String?, locale: String?) :
    LanguageProvider(output!!, modid.toString(), locale.toString()) {
    override fun addTranslations() {
        add(ModBlocks.BREAD_BLOCK, "Bread Block")
        add(BreadMod.ID + ".itemGroup", "The Bread Mod")
        add(ModItems.TEST_DISC, "Test music disc")
        add(ModItems.TEST_BREAD, "Test Bread")
        add(ModItems.BREAD_SHIELD, "Bread Shield")
        add("item.breadmod.music_disc_test.desc", "Music Disc Description")
        add("item.breadmod.test_bread.desc", "Identical to bread on the outside - tumors on the inside.")
    }
}