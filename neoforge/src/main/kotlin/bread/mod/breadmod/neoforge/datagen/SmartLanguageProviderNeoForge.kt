package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.DataGenerateLanguage
import bread.mod.breadmod.datagen.SmartLanguageProvider
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
) : SmartLanguageProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates language files according to [DataGenerateLanguage] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
            getTranslationMap().forEach { (locale, localeMap) ->
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
