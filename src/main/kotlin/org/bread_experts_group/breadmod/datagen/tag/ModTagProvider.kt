package org.bread_experts_group.breadmod.datagen.tag

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.core.registries.Registries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import org.bread_experts_group.breadmod.datagen.getLocation
import org.bread_experts_group.breadmod.datagen.path
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

class ModTagProvider(
	val packOutput: PackOutput
) : DataProvider {
	private val registryScanner: LibraryScanner =
		org.bread_experts_group.breadmod.registry.Registry::class.java.`package`
			.getScanner()

	override fun getName(): String = "BreadMod Smart Tag Provider"
	override fun run(output: CachedOutput): CompletableFuture<*> = buildList<CompletableFuture<*>> {
		val out = this@ModTagProvider.packOutput
		val tags = mutableMapOf<Path, MutableList<String>>()
		this@ModTagProvider.registryScanner.resolveAnnotationValuePairs<DataGenerateTagBlock>()
			.forEach { (annotation, data) ->
				annotation.tags.forEach {
					tags.getOrPut(Registries.BLOCK.path(out, it)) { mutableListOf() }
						.add(data.getLocation("Block tag generation").toString())
				}
			}
		this@ModTagProvider.registryScanner.resolveAnnotationValuePairs<DataGenerateTagItem>()
			.forEach { (annotation, data) ->
				annotation.tags.forEach {
					tags.getOrPut(Registries.ITEM.path(out, it)) { mutableListOf() }
						.add(data.getLocation("Item tag generation").toString())
				}
			}
		this@ModTagProvider.registryScanner.resolveAnnotationValuePairs<DataGenerateTagFluid>()
			.forEach { (annotation, data) ->
				annotation.tags.forEach {
					tags.getOrPut(Registries.FLUID.path(out, it)) { mutableListOf() }
						.add(data.getLocation("Fluid tag generation").toString())
				}
			}
		this@ModTagProvider.registryScanner.resolveAnnotationValuePairs<DataGenerateTagPainting>()
			.forEach { (annotation, data) ->
				annotation.tags.forEach {
					tags.getOrPut(Registries.PAINTING_VARIANT.path(out, it)) { mutableListOf() }
						.add(data.getLocation("Painting tag generation").toString())
				}
			}
		tags.getOrPut(Registries.ITEM.path(out, "breadmod:toastable")) { mutableListOf() }
			.add("minecraft:bread")
		tags.getOrPut(Registries.ITEM.path(out, "breadmod:explodes_in_toaster")) { mutableListOf() }
			.addAll(arrayOf("minecraft:coal", "minecraft:charcoal"))
		tags.getOrPut(Registries.BLOCK.path(out, "breadmod:mineable/knife")) { mutableListOf() }
			.add("minecraft:pumpkin")
		tags.forEach { (path, ids) ->
			this.add(
				DataProvider.saveStable(
					output,
					JsonObject().also { o -> o.add("values", JsonArray().also { ids.forEach { id -> it.add(id) } }) },
					path
				)
			)
		}
	}.toTypedArray().let { CompletableFuture.allOf(*it) }
}