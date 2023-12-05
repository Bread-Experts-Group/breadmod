package breadmod.datagen.provider.lang


import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.data.DataGenerator
import net.minecraftforge.common.data.LanguageProvider

class USEnglishLanguageProvider(generator: DataGenerator) : LanguageProvider(generator, BreadMod.ID, "en_us") {
    override fun addTranslations() {
        add("itemGroup.${BreadMod.ID}", "The Bread Mod")
        // Blocks
        add(ModItems.BREAD_BLOCK_ITEM, "Bread Block")
        // Items
        add(ModItems.TEST_BREAD, "Test Bread")
        add("item.breadmod.test_bread.desc", "Identical to bread on the outside - tumors on the inside.")
        add(ModItems.BREAD_HELMET, "Bread Helmet")
        add(ModItems.BREAD_CHESTPLATE, "Bread Chestplate")
        add(ModItems.BREAD_LEGGINGS, "Bread Leggings")
        add(ModItems.BREAD_BOOTS, "Bread Boots")
        add(ModItems.TEST_DISC, "Test Music Disc")
        add("item.breadmod.music_disc_test.desc", "Test Description")
        add(ModItems.BREAD_SHIELD, "Bread Shield")
        add("item.breadmod.bread_shield.desc", "Not repairable. Not by normal means at least.")

    }
}