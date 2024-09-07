package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.DataGenerateLanguage
import bread.mod.breadmod.datagen.DataProviderScanner
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike


/**
 * An annotation-based language provider.
 *
 * @property modID The mod ID to save language translations for.
 *
 * @see DataGenerateLanguage
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartLanguageProviderFabric(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    /**
     * Generates language files according to [DataGenerateLanguage] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: FabricDataGenerator.Pack) {
        val languageKeys = mutableMapOf<String, MutableMap<String, String>>()
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateLanguage>().forEach { (value, annotations) ->
            annotations.forEach { a ->
                val localeMap = languageKeys.getOrPut(a.locale) { mutableMapOf() }

                when (value) {
                    is ItemLike -> localeMap[value.asItem().descriptionId] = a.translation
                    is CreativeModeTab -> {
                        val contents = value.displayName.contents
                        if (contents is TranslatableContents) localeMap[contents.key] = a.translation
                    }

                    null -> throw NullPointerException()
                    else -> throw UnsupportedOperationException("Unsupported type (pls add): ${value::class.qualifiedName}")
                }
            }
        }

        languageKeys.forEach { (locale, localeMap) ->
            forEvent.addProvider { dataOutput, future ->
                object : FabricLanguageProvider(dataOutput, locale, future) {
                    override fun generateTranslations(
                        registryLookup: HolderLookup.Provider,
                        translationBuilder: TranslationBuilder
                    ) = localeMap.forEach { (id, translation) -> translationBuilder.add(id, translation) }
                }
            }
        }
    }
}
