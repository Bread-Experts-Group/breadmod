package bread.mod.breadmod.datagen.language

import bread.mod.breadmod.datagen.DataProviderScanner
import bread.mod.breadmod.util.ensureRegistrySupplierAndValue
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike

/**
 * Abstract class for [SmartLanguageProvider]s, reading and assorting locales to description IDs and translations.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SmartLanguageProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    /**
     * The main function of the [SmartLanguageProvider].
     * Reads off all properties tagged with [DataGenerateLanguage], assorting them into
     * locale -> description ID, translation groups.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getTranslationMap(): Map<String, MutableMap<String, String>> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateLanguage>().forEach { (property, data) ->
            data.second.forEach { a ->
                val localeMap = getOrPut(a.locale) { mutableMapOf() }
                when (val actual = data.first.ensureRegistrySupplierAndValue<Any>(property).get()) {
                    is ItemLike -> localeMap[actual.asItem().descriptionId] = a.translation
                    is CreativeModeTab -> {
                        val contents = actual.displayName.contents
                        if (contents is TranslatableContents) localeMap[contents.key] = a.translation
                    }

                    is SoundEvent -> localeMap[actual.location.toLanguageKey("sound")] = a.translation

                    null -> throw NullPointerException()
                    else -> throw UnsupportedOperationException("Unsupported type (pls add): ${actual::class.qualifiedName}")
                }
            }
        }
    }
}