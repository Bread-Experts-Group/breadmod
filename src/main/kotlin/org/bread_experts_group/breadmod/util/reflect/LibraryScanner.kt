package org.bread_experts_group.breadmod.util.reflect

import net.minecraft.client.Minecraft
import net.neoforged.neoforgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
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
 * @property pForPackage The package to scan for.
 * @property localClasses The [KClass]
 * (note, classes don't need to be Kotlin) contained within the provided [Package].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class LibraryScanner private constructor(pForPackage: Package?, pData: List<ModFileScanData>?) {
	companion object {
		private val classes: MutableMap<Package, List<KClass<out Any>>> = mutableMapOf()
		private val piggybackClasses: MutableMap<ModFileScanData, List<KClass<out Any>>> = mutableMapOf()
		val logger: Logger = LogManager.getLogger()

		private fun safeGetFileSystem(uri: URI): FileSystem = try {
			this.logger.info("Safe-getting file system from: $uri")
			FileSystems.getFileSystem(uri)
		} catch (_: FileSystemNotFoundException) {
			FileSystems.newFileSystem(uri, mapOf("create" to "true"))
		}

		fun Package.getOrScanCache(): List<KClass<out Any>> {
			return Companion.classes.getOrPut(this) {
				val loader = Minecraft::class.java.classLoader
				buildList {
					loader.getResources(this@getOrScanCache.name.replace(".", "/")).toList()
						.forEach {
							try {
								val fs = Companion.safeGetFileSystem(it.toURI())
								fs.rootDirectories.forEach { rootDir ->
									Files.walk(rootDir)
										.filter(Files::isRegularFile)
										.filter { f -> f.name.endsWith(".class", false) }
										.forEach { f ->
											try {
												this.add(
													loader.loadClass(
														f
															.absolutePathString()
															.substring(1)
															.removeSuffix(".class")
															.replace('/', '.')
													).kotlin
												)
											} catch (e: Throwable) {
												Companion.logger.warn("Failure when loading class: $f", e)
											}
										}
								}
							} catch (e: Exception) {
								Companion.logger.warn("Failure when reading from file system", e)
							}
						}
				}
			}
		}

		fun List<ModFileScanData>.piggybackCache(): List<KClass<out Any>> {
			var failures = 0
			var count = 0
			val list = buildList {
				this@piggybackCache.forEach {
					this.addAll(
						Companion.piggybackClasses.getOrPut(it) {
							buildList {
								count += it.classes.size
								it.classes.forEach { c ->
									try {
										this.add(Minecraft::class.java.classLoader.loadClass(c.clazz.className).kotlin)
									} catch (_: Throwable) {
										failures++
									}
								}
							}
						}
					)
				}
			}
			if (failures > 0) Companion.logger.warn(
				"Failed to load $failures piggyback classes, ${count - failures}/${count}"
			)
			return list
		}

		fun Package.getScanner(): LibraryScanner =
			LibraryScanner(this, null)

		fun piggyback(data: List<ModFileScanData>): LibraryScanner =
			LibraryScanner(null, data)
	}

	val localClasses: List<KClass<out Any>>

	init {
		if (pForPackage != null) {
			this.localClasses = pForPackage.getOrScanCache()
		} else if (pData != null) {
			this.localClasses = pData.piggybackCache()
		} else {
			throw IllegalArgumentException("Either a package or list of mod file data must be provided.")
		}
	}

	/**
	 * Gets all [KClass]es from the provided [Package] that are annotated with [T].
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	fun <T : Annotation> getClassesAnnotatedWith(annotation: KClass<T>): List<KClass<out Any>> {
		return buildList {
			for (clazz in this@LibraryScanner.localClasses) {
				try {
					if (clazz.annotations.any { a -> a.annotationClass == annotation }) {
						this.add(clazz)
					}
				} catch (_: Throwable) {
				}
			}
		}
	}

	/**
	 * Gets all [kotlin.reflect.KProperty1]s from Kotlin Objects in the provided [Package], annotated with [T].
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	@Suppress("UNCHECKED_CAST")
	inline fun <reified T : Annotation> getObjectPropertiesAnnotatedWith(): Map<KProperty1<*, *>, Pair<*, Array<T>>> =
		buildMap {
			this@LibraryScanner.localClasses.filter {
				try {
					it.objectInstance != null
				} catch (e: Exception) {
					// NOTE: This is quite inefficient. Look into fixes in the future?
					Companion.logger.warn("Failure when getting objectInstance: $e")
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
					Companion.logger.error("Failure when reading annotations off: ${it.qualifiedName}", e)
				}
			}
		}
}