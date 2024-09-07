package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.language.SmartLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup


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
) : SmartLanguageProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    /**
     * Generates language files according to [DataGenerateLanguage] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: FabricDataGenerator.Pack) {
        getTranslationMap().forEach { (locale, localeMap) ->
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
