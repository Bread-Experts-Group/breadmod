package org.bread_experts_group.breadmod.registry.tag

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation

object BlockTags {
    val MINEABLE_WITH_KNIFE: TagKey<Block> = TagKey.create(Registries.BLOCK, modLocation("mineable/knife"))
    val INCORRECT_FOR_BREAD_TOOL: TagKey<Block> =
        TagKey.create(Registries.BLOCK, modLocation("incorrect_for_bread_tool"))
    val INCORRECT_FOR_REINFORCED_BREAD_TOOL: TagKey<Block> =
        TagKey.create(Registries.BLOCK, modLocation("incorrect_for_reinforced_bread_tool"))
}