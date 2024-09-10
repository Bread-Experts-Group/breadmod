package bread.mod.breadmod.registry.tag

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object ItemTags {
    val KNIVES: TagKey<Item> = TagKey.create(Registries.ITEM, modLocation("knives"))
}