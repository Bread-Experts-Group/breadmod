package bread.mod.breadmod.datagen.tag

import bread.mod.breadmod.util.createTagKey
import net.minecraft.core.registries.BuiltInRegistries

object ModTags {
    private fun modBlockTag(path: String) = BuiltInRegistries.BLOCK.createTagKey(path)
    private fun modItemTag(path: String) = BuiltInRegistries.ITEM.createTagKey(path)

    val MINEABLE_WITH_KNIFE = modBlockTag("breadmod:mineable/knife")

    val KNIVES = modItemTag("breadmod:tools/knives")
    val TOASTABLE = modItemTag("breadmod:toastable")
}