package bread.mod.breadmod.registry.tag

import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey

object RecordTags {
    val TEST_SOUND = ResourceKey.create(Registries.JUKEBOX_SONG, modLocation("test_sound"))
}