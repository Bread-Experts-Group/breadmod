package bread.mod.breadmod.registry.tag

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

object BlockTags {
    val MINEABLE_WITH_KNIFE = TagKey.create(Registries.BLOCK, modLocation("mineable/knife"))
    val INCORRECT_FOR_BREAD_TOOL = TagKey.create(Registries.BLOCK, modLocation("incorrect_for_bread_tool"))
    val INCORRECT_FOR_REINFORCED_BREAD_TOOL = TagKey.create(Registries.BLOCK, modLocation("incorrect_for_reinforced_bread_tool"))
}