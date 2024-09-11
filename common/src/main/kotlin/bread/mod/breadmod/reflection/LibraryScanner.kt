package bread.mod.breadmod.reflection

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.annotations.ApiStatus
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.io.path.name
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField


/**
 * A scanner for JVM packages.
 *
 * @property pForLoader The class loader to use when finding/loading classes and getting CLASS files.
 * @property pForPackage The package to scan for.
 *
 * @property packageClasses The [KClass] (note, classes don't need to be Kotlin) contained within the provided [Package].
 *
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class LibraryScanner(private val pForLoader: ClassLoader, private val pForPackage: Package) {
    companion object {
        /**
         * [Logger] for [LibraryScanner] operations.
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        @ApiStatus.Internal
        val LOGGER: Logger = LogManager.getLogger()
    }

    private fun safeGetFileSystem(uri: URI): FileSystem = try {
        LOGGER.info("Safe-getting file system from: {}", uri)
        FileSystems.getFileSystem(uri)
    } catch (_: FileSystemNotFoundException) {
        FileSystems.newFileSystem(uri, mapOf("create" to "true"))
    }

    val packageClasses: List<KClass<out Any>> = buildList {
        pForLoader.getResources(pForPackage.name.replace(".", "/")).toList().forEach {
            try {
                safeGetFileSystem(it.toURI()).also { fs ->
                    fs.rootDirectories.forEach { rootDir ->
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
                                    add(pForLoader.loadClass(className).kotlin)
                                } catch (_: Throwable) {
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                LOGGER.warn("Failure when reading from file system: $e")
            }
        }
    }

    /**
     * Gets all [kotlin.reflect.KProperty1]s from Kotlin Objects in the provided [Package], annotated with [T].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Annotation> getObjectPropertiesAnnotatedWith(): Map<KProperty1<*, *>, Pair<*, Array<T>>> = buildMap {
        packageClasses.filter {
            try {
                it.objectInstance != null
            } catch (e: Exception) {
                // NOTE: This is quite inefficient. Look into fixes in the future?
                LOGGER.warn("Failure when getting objectInstance: $e")
                false
            }
        }.forEach {
            try {
                it.memberProperties.forEach { f ->
                    val annotationsRaw = f.javaField?.annotations?.firstOrNull { a ->
                        a.annotationClass.qualifiedName?.contains(T::class.simpleName!!) == true
                    }
                    if (annotationsRaw != null) {
                        val annotations = if (annotationsRaw is T) arrayOf(annotationsRaw)
                        else annotationsRaw.annotationClass.java.declaredMethods
                            .firstOrNull { m -> m.name == "value" }
                            ?.invoke(annotationsRaw) as Array<T>?

                        if (annotations != null) this[f] = f.call(it.objectInstance) to annotations
                    }
                }
            } catch (e: Exception) {
                LOGGER.error("Failure when reading annotations off ${it.qualifiedName}: $e")
            }
        }
    }
}