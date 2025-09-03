package org.bread_experts_group.breadmod.datagen.tag

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage

@DataGenerateLanguage(prefix = "tag.block.")
val MINEABLE_WITH_KNIFE: TagKey<Block> =
	TagKey.create(Registries.BLOCK, modLocation("mineable/knife"))

@DataGenerateLanguage(prefix = "tag.block.")
val INCORRECT_FOR_BREAD_TOOL: TagKey<Block> =
	TagKey.create(Registries.BLOCK, modLocation("incorrect_for_bread_tool"))

@DataGenerateLanguage(prefix = "tag.block.")
val INCORRECT_FOR_REINFORCED_BREAD_TOOL: TagKey<Block> =
	TagKey.create(Registries.BLOCK, modLocation("incorrect_for_reinforced_bread_tool"))

@DataGenerateLanguage(prefix = "tag.item.")
val KNIVES: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("knives"))

@DataGenerateLanguage(prefix = "tag.item.")
val TOASTABLE: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("toastable"))

@DataGenerateLanguage(prefix = "tag.item.")
val EXPLODES_IN_TOASTER: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("explodes_in_toaster"))