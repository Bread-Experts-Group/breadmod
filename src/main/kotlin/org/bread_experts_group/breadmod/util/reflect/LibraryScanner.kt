package org.bread_experts_group.breadmod.util.reflect

import net.minecraft.util.thread.ReentrantBlockableEventLoop
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.io.path.absolutePathString
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod

/**
 * A scanner for JVM packages.
 *
 * @property localClasses The [KClass]
 * (note, classes don't need to be Kotlin) contained within the provided [Package].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class LibraryScanner private constructor(pForPackage: Package?, pData: List<ModFileScanData>?) {
	companion object {
		private val classes: MutableMap<Package, List<KClass<out Any>>> = mutableMapOf()
		private val coreLoader: ClassLoader = ReentrantBlockableEventLoop::class.java.classLoader
		val logger: Logger = LogManager.getLogger()

		private fun safeGetFileSystem(uri: URI): FileSystem = try {
			this.logger.info("Safe-getting file system from: $uri")
			FileSystems.getFileSystem(uri)
		} catch (_: FileSystemNotFoundException) {
			FileSystems.newFileSystem(uri, mapOf("create" to "true"))
		}

		fun Package.getOrScanCache(): List<KClass<out Any>> {
			return this@Companion.classes.getOrPut(this) {
				val cList = ConcurrentLinkedQueue<KClass<out Any>>()
				for (resource in this@Companion.coreLoader.getResources(
					this@getOrScanCache.name.replace(
						".",
						"/"
					)
				)) {
					try {
						val fs = this@Companion.safeGetFileSystem(resource.toURI())
						fs.rootDirectories.forEach { rootDir ->
							this@Companion.logger.info("Walking the directory at [$rootDir]")
							Files.walk(rootDir)
								.parallel()
								.filter(Files::isRegularFile)
								.map { p -> p.absolutePathString() }
								.filter { p -> p.endsWith(".class", true) }
								.filter { p -> !p.contains("mixin", true) }
								.map { p -> p.substring(1, p.length - 6).replace('/', '.', true) }
								.forEach { p ->
									try {
										cList.add(this@Companion.coreLoader.loadClass(p).kotlin)
									} catch (_: Throwable) {
									}
								}
						}
					} catch (e: Exception) {
						this@Companion.logger.warn("Failure when reading from file system", e)
					}
				}
				cList.toList()
			}
		}

		fun List<ModFileScanData>.piggybackCache(): List<KClass<out Any>> {
			val cList = ConcurrentLinkedQueue<KClass<out Any>>()
			val blacklist = ConcurrentLinkedQueue<String>()
			this.forEach {
				it.annotations
					.parallelStream()
					.filter { a -> a.annotationType.className.contains("OnlyIn", true) }
					.filter { a -> a.annotationData["value"]!! != FMLEnvironment.dist }
					.forEach { a -> blacklist.add(a.memberName) }
				it.classes
					.parallelStream()
					.map { c -> c.clazz.className }
					.filter { c -> !blacklist.contains(c) }
					.forEach { c ->
						try {
							cList.add(this@Companion.coreLoader.loadClass(c).kotlin)
						} catch (_: Throwable) {
						}
					}
			}
			return cList.toList()
		}

		fun Package.getScanner(): LibraryScanner = LibraryScanner(this, null)
		fun piggyback(data: List<ModFileScanData>): LibraryScanner = LibraryScanner(null, data)
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

	inline fun <reified T : Annotation> readAnnotations(from: Array<Annotation>?): Array<out Any?>? {
		val annotationsRaw = from?.firstOrNull { a ->
			a.annotationClass.qualifiedName?.contains(T::class.simpleName!!) == true
		}
		if (annotationsRaw != null) {
			return if (annotationsRaw is T) arrayOf(annotationsRaw)
			else annotationsRaw.annotationClass.java.declaredMethods
				.firstOrNull { m -> m.name == "value" }
				?.invoke(annotationsRaw) as Array<*>?
		}
		return null
	}

	inline fun <reified T : Annotation> handleProperty(
		list: MutableList<Pair<T, Any>>,
		field: KProperty<*>,
		obj: Any? = null
	) {
		val annotations = this.readAnnotations<T>(field.javaField?.annotations) ?: return
		if (annotations.isEmpty()) return
		val returned = field.call(obj)
		if (returned != null) annotations.forEach { list.add(it as T to returned) }
	}

	inline fun <reified T : Annotation> handleFunction(
		list: MutableList<Pair<T, Any>>,
		func: KFunction<*>,
		vararg args: Any
	) {
		if (func.parameters.size != args.size) return
		val annotations = this.readAnnotations<T>(func.javaMethod?.annotations) ?: return
		if (annotations.isEmpty()) return
		val returned = func.call(*args)
		if (returned != null) annotations.forEach { list.add(it as T to returned) }
	}

	/**
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	inline fun <reified T : Annotation> resolveAnnotationValuePairs(): List<Pair<T, Any>> =
		buildList {
			this@LibraryScanner.localClasses.forEach { clazz ->
				try {
					clazz.objectInstance?.let { obj ->
						for (func in clazz.memberFunctions) this@LibraryScanner.handleFunction(this, func, obj)
						for (field in clazz.memberProperties) this@LibraryScanner.handleProperty(this, field, obj)
					}
				} catch (_: Throwable) {
				}
				try {
					for (func in clazz.staticFunctions) this@LibraryScanner.handleFunction(this, func)
					for (field in clazz.staticProperties) this@LibraryScanner.handleProperty(this, field)
				} catch (_: Throwable) {
				}
			}
		}
}