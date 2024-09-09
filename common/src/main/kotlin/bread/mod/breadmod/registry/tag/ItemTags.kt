package bread.mod.breadmod.registry.tag

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

object ItemTags {
    val KNIVES = TagKey.create(Registries.ITEM, modLocation("knives"))
}