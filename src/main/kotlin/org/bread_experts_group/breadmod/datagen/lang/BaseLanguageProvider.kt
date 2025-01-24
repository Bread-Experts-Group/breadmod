@file:Suppress("SameReturnValue")

package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.client.KeyMapping
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.registries.DeferredHolder
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.ModDamageType
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import java.io.Serial
import java.util.function.Supplier

/**
 * Basic internal language provider for BreadMod. This implementation can't automatically name items based on their IDs.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see LanguageDataGenerator
 * @see DataGenerateLanguage
 */
@Suppress("SameReturnValue", "SameReturnValue")
internal sealed class BaseLanguageProvider(
	output: PackOutput,
	@Suppress("SameParameterValue") val language: String
) : LanguageProvider(output, BreadMod.ID, language) {
	protected open fun assureName(name: String, otherwise: String): String =
		if (name == "<null>") throw UnsupportedOperationException() else name

	fun getLanguageID(item: Any, annotation: DataGenerateLanguage = DataGenerateLanguage("")): String {
		val actualItem = when (item) {
			is DeferredHolder<*, *> -> item.get()
			else                    -> item
		}
		return (if (annotation.prefix == "<null>") "" else annotation.prefix) + when (actualItem) {
			is Block            -> actualItem.descriptionId
			is Item             -> actualItem.descriptionId
			is ItemStack        -> actualItem.item.descriptionId
			is EntityType<*>    -> actualItem.descriptionId
			is CreativeModeTab  -> (actualItem.displayName.contents as TranslatableContents).key
			is ModDamageType    -> actualItem.translationKey()
			is SoundEvent       -> actualItem.location.toLanguageKey("sound")
			is KeyMapping       -> actualItem.name
			is String           -> actualItem
			is ResourceLocation -> actualItem.toLanguageKey()
			else                -> throw UnsupportedItemClassException(actualItem::class.java)
		} + (if (annotation.suffix == "<null>") "" else annotation.suffix)
	}

	protected fun bmAdd(key: Any, name: String = "<null>") = this.getLanguageID(key).let {
		this.add(it, this.assureName(name, it))
	}

	final override fun add(key: Block, name: String): Unit = this.bmAdd(key, name)
	final override fun add(key: Item, name: String): Unit = this.bmAdd(key, name)
	final override fun add(key: ItemStack, name: String): Unit = this.bmAdd(key, name)
	final override fun add(key: MobEffect, name: String): Unit = this.bmAdd(key, name)
	final override fun add(key: EntityType<*>, name: String): Unit = this.bmAdd(key, name)
	final override fun add(key: TagKey<*>, name: String): Unit = this.bmAdd(key, name)
	final override fun addBlock(key: Supplier<out Block>, name: String): Unit = this.bmAdd(key, name)
	final override fun addDimension(key: ResourceKey<Level>, name: String): Unit = this.bmAdd(key, name)
	final override fun addEffect(key: Supplier<out MobEffect>, name: String): Unit = this.bmAdd(key, name)
	final override fun addEntityType(key: Supplier<out EntityType<*>>, name: String): Unit = this.bmAdd(key, name)
	final override fun addItem(key: Supplier<out Item>, name: String): Unit = this.bmAdd(key, name)
	final override fun addItemStack(key: Supplier<ItemStack>, name: String): Unit = this.bmAdd(key, name)
	final override fun addTag(key: Supplier<out TagKey<*>>, name: String): Unit = this.bmAdd(key, name)

	private class UnsupportedItemClassException(clazz: Class<*>) :
		UnsupportedOperationException("Unsupported item class for translation: ${clazz.toGenericString()}") {
		companion object {
			@Serial
			private const val serialVersionUID: Long = 3635823838399466804L
		}
	}

	protected open fun addManualTranslations() {}

	private val registryScanner = Registry::class.java.`package`.getScanner()
	final override fun addTranslations() {
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateLanguage>()
			.filter { it.first.language == this.language }
			.forEach { (annotation, data) ->
				val languageID = this.getLanguageID(data, annotation)
				this.add(languageID, this.assureName(annotation.name, languageID))
			}
		this.addManualTranslations()
	}

	@Suppress("SameReturnValue")
	abstract fun getNameAdditional(): String
	final override fun getName(): String = "BreadMod internal language provider for ${this.getNameAdditional()}"
}