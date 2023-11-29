package breadmod.datagen.provider


import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.data.DataGenerator
import net.minecraftforge.common.data.LanguageProvider

class LanguageProvider(
    generator: DataGenerator,
    locale: String,
) : LanguageProvider(generator, BreadMod.ID, locale) {
    override fun addTranslations() {
        add(ModItems.BREAD_BLOCK_ITEM, "Bread Block")
        add(ModItems.TEST_BREAD, "Test Bread")
    }
}