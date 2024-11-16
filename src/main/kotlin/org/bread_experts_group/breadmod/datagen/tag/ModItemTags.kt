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
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class ModItemTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    blockTags: CompletableFuture<TagLookup<Block>>,
    existingFileHelper: ExistingFileHelper
) : ItemTagsProvider(output, lookupProvider, blockTags, Breadmod.ID, existingFileHelper) {
    inline fun <T, reified A : T> IntrinsicTagAppender<T>.add(vararg toAdd: Supplier<A>): IntrinsicTagAppender<T> =
        this.also { this.add(*toAdd.map { it.get() }.toTypedArray()) }

    override fun addTags(provider: HolderLookup.Provider) {
        tag(Tags.Items.MUSIC_DISCS)
            .add(ModItems.TEST_RECORD)
        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
            .add(ModItems.TEST_RECORD)
        tag(ItemTags.DYEABLE)
            .add(ModItems.CHEF_HAT)
            .add(ModItems.BREAD_HELMET)
            .add(ModItems.BREAD_CHESTPLATE)
            .add(ModItems.BREAD_LEGGINGS)
            .add(ModItems.BREAD_BOOTS)

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "flour/wheat")))
            .add(ModItems.FLOUR)

        tag(TOASTABLE)
            .add(
                Items.BREAD,
                Items.CHARCOAL
            )

        tag(KNIVES)
            .add(ModItems.KNIFE)
    }

    companion object {
        val KNIVES: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("knives"))
        val TOASTABLE: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("toastable"))
    }
}