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
    }
}