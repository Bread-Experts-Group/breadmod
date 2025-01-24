package org.bread_experts_group.breadmod.datagen.tag

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class ModItemTags(
	output: PackOutput,
	lookupProvider: CompletableFuture<HolderLookup.Provider>,
	blockTags: CompletableFuture<TagLookup<Block>>,
	existingFileHelper: ExistingFileHelper
) : ItemTagsProvider(output, lookupProvider, blockTags, BreadMod.ID, existingFileHelper) {
	inline fun <T, reified A : T> IntrinsicTagAppender<T>.add(vararg toAdd: Supplier<A>): IntrinsicTagAppender<T> =
		this.also { this.add(*toAdd.map(Supplier<A>::get).toTypedArray()) }

	override fun addTags(provider: HolderLookup.Provider) {
		this.tag(Tags.Items.MUSIC_DISCS)
			.add(ModItems.RECORD_SECRET_HOPPIN)
		this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
			.add(ModItems.RECORD_SECRET_HOPPIN)
		this.tag(ItemTags.DYEABLE)
			.add(ModItems.CHEF_HAT)
			.add(ModItems.BREAD_HELMET)
			.add(ModItems.BREAD_CHESTPLATE)
			.add(ModItems.BREAD_LEGGINGS)
			.add(ModItems.BREAD_BOOTS)

		this.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "flour/wheat")))
			.add(ModItems.FLOUR)

		this.tag(Companion.TOASTABLE)
			.add(
				Items.BREAD,
				ModItems.TOASTED_BREAD.get(),
				ModItems.BREAD_SLICE.get(),
				ModItems.TOAST_SLICE.get()
			)

		this.tag(Companion.EXPLODES_IN_TOASTER)
			.add(
				Items.COAL,
				Items.CHARCOAL
			)

		this.tag(Companion.KNIVES)
			.add(ModItems.KNIFE)
	}

	companion object {
		@DataGenerateLanguage("en_us", prefix = "tag.item.")
		val KNIVES: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("knives"))

		@DataGenerateLanguage("en_us", prefix = "tag.item.")
		val TOASTABLE: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("toastable"))

		@DataGenerateLanguage("en_us", prefix = "tag.item.")
		val EXPLODES_IN_TOASTER: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("explodes_in_toaster"))
	}
}