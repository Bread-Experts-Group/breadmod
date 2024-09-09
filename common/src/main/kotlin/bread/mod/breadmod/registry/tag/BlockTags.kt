package bread.mod.breadmod.registry.tag

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

object BlockTags {
    val MINEABLE_WITH_KNIFE = TagKey.create(Registries.BLOCK, modLocation("mineable/knife"))
}