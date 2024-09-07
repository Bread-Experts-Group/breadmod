package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.DataGenerateLanguage
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.function.Supplier
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField


/**
 * An annotation-based language provider.
 * @see DataGenerateLanguage
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartLanguageProvider(val modID: String, val forClassLoader: ClassLoader, val forPackage: Package) {
    private fun getClassesForPackage(): List<KClass<*>> = buildList {
        forClassLoader.getResources(forPackage.name.replace(".", "/")).toList().forEach {
            FileSystems.getFileSystem(it.toURI()).rootDirectories.forEach { rootDir ->
                Files.walk(rootDir)
                    .filter(Files::isRegularFile)
                    .filter { f -> f.name.endsWith(".class", true) }
                    .forEach { f ->
                        try {
                            val className = f
                                .absolutePathString()
                                .substring(1)
                                .removeSuffix(".class")
                                .replace('/', '.')
                            add(forClassLoader.loadClass(className).kotlin)
                        } catch (_: Throwable) {
                        }
                    }
            }
        }
    }

    /**
     * Generates language files according to [DataGenerateLanguage] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
            val allClasses = getClassesForPackage()

            // This is a map of locale → (descriptionId → translation)
            val languageKeys = mutableMapOf<String, MutableMap<String, String>>()
            allClasses.filter { it.objectInstance != null }.forEach {
                it.memberProperties.forEach { f ->
                    // Shoddy code.
                    // Because of how classes are loaded, kotlin annotations aren't "unpacked" like usual,
                    // so we have to do this manually.
                    val annotationsRaw = f.javaField?.annotations?.firstOrNull { a ->
                        a.annotationClass.qualifiedName?.contains(DataGenerateLanguage::class.simpleName!!) == true
                    }
                    if (annotationsRaw != null) {
                        val value = (f.call(it.objectInstance) as Supplier<*>).get()

                        @Suppress("UNCHECKED_CAST")
                        val annotations = if (annotationsRaw is DataGenerateLanguage) arrayOf(annotationsRaw)
                        else annotationsRaw.annotationClass.java.declaredMethods
                            .first { m -> m.name == "value" }
                            .invoke(annotationsRaw) as Array<DataGenerateLanguage>

                        annotations.forEach { a ->
                            val localeMap = languageKeys.getOrPut(a.locale) { mutableMapOf() }

                            when (value) {
                                is ItemLike -> localeMap[value.asItem().descriptionId] = a.translation
                                is CreativeModeTab -> {
                                    val contents = value.displayName.contents
                                    if (contents is TranslatableContents) localeMap[contents.key] = a.translation
                                }

                                else -> throw UnsupportedOperationException(
                                    "Unsupported type (pls add): ${value::class.qualifiedName}"
                                )
                            }
                        }
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
