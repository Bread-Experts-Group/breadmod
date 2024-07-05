package breadmodadvanced.datagen.lang

import breadmodadvanced.ModMainAdv
import breadmodadvanced.registry.block.ModBlocksAdv
import breadmodadvanced.registry.screen.ModCreativeTabsAdv
import net.minecraft.data.PackOutput
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.registries.RegistryObject

class USEnglishLanguageProviderAdv(
    packOutput: PackOutput,
    locale: String
): LanguageProvider(packOutput, ModMainAdv.ID, locale) {
    private fun String.joinUnderscoreWithCaps() =
        this.split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

    // Transforms type.mod_id.example_object into Example Object
    private fun String.addTransformed(override: String? = null) = add(this, override ?: this.substringAfterLast('.').joinUnderscoreWithCaps())

    private inline fun <reified T> add(obj: RegistryObject<T>, override: String? = null) = when(val entry = obj.get()) {
        is ItemLike -> entry.asItem().descriptionId.addTransformed(override)
        is FluidType -> entry.descriptionId.addTransformed(override)
        is CreativeModeTab -> add("itemGroup.${ModMainAdv.ID}.${obj.id.path}", override ?: obj.id.path.joinUnderscoreWithCaps())
        else -> throw IllegalArgumentException("Object provided, ${T::class.qualifiedName}, cannot be added")
    }

    override fun addTranslations() {
        add(ModBlocksAdv.DIESEL_GENERATOR)
        add(ModCreativeTabsAdv.MAIN_TAB, "Bread Mod: Advanced")
    }
}