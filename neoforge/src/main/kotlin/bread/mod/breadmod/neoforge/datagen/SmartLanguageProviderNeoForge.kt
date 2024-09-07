package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.DataGenerateLanguage
import bread.mod.breadmod.datagen.DataProviderScanner
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


/**
 * An annotation-based language provider.
 *
 * @property modID The mod ID to save language translations for.
 *
 * @see DataGenerateLanguage
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartLanguageProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates language files according to [DataGenerateLanguage] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
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
                val newProvider = object : LanguageProvider(forEvent.generator.packOutput, modID, locale) {
                    override fun addTranslations() {
                        localeMap.forEach { (id, translation) -> add(id, translation) }
                    }
                }

                forEvent.generator.addProvider(true, newProvider)
            }
        }
    }
}
