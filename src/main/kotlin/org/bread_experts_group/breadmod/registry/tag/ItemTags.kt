package org.bread_experts_group.breadmod.registry.tag

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation

object ItemTags {
    val KNIVES: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("knives"))
    val TOASTABLE: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("toastable"))
}