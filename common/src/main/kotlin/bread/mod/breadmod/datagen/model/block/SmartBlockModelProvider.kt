package bread.mod.breadmod.datagen.model.block

import bread.mod.breadmod.datagen.DataProviderScanner

/**
 * Abstract class for [SmartBlockModelProvider]s, reading and assorting locales to description IDs and translations.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class SmartBlockModelProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    /**
     * The main function of the [SmartBlockModelProvider].
     * Reads off all properties tagged with [DataGenerateBlockModel], assorting them into
     * locale -> description ID, translation groups.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    protected fun getTranslationMap(): Map<String, MutableMap<String, String>> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateBlockModel>().forEach { (value, annotations) ->

        }
    }
}