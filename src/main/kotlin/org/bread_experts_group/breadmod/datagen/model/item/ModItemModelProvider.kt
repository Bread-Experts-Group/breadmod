package org.bread_experts_group.breadmod.datagen.model.item

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.datagen.getLocation
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner

class ModItemModelProvider(
	packOutput: PackOutput,
	existingFileHelper: ExistingFileHelper
) : ItemModelProvider(packOutput, BreadMod.Companion.ID, existingFileHelper) {
	private val registryScanner: LibraryScanner = Registry::class.java.`package`.getScanner()

	override fun registerModels() {
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateModelSingleItem>().forEach { (_, data) ->
			this.defaultNamespacedItem(data.getLocation("Item model generation (single / generated)").path, "generated")
		}
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateModelHandheldItem>().forEach { (_, data) ->
			this.defaultNamespacedItem(data.getLocation("Item model generation (single / handheld)").path, "handheld")
		}
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateModelLayeredItem>().forEach { (annotation, data) ->
			val model = this.withExistingParent(
				data.getLocation("Item model generation (layered / generated)").path,
				ResourceLocation.withDefaultNamespace("item/generated")
			)
			annotation.layerTexture.forEachIndexed { i, l ->
				model.texture("layer$i", "item/$l")
			}
		}
		this.fenceInventory("bread_fence", this.modLoc("${BLOCK_FOLDER}/bread_block"))
	}

	private fun defaultNamespacedItem(path: String, minecraftLocation: String) {
		this.withExistingParent(
			path,
			ResourceLocation.withDefaultNamespace("item/$minecraftLocation")
		).texture(
			"layer0",
			BreadMod.Companion.modLocation("item/$path")
		)
	}
}