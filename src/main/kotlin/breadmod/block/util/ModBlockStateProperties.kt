package breadmod.block.util

import net.minecraft.world.level.block.state.properties.IntegerProperty

object ModBlockStateProperties {
    val STORAGE_LEVEL: IntegerProperty = IntegerProperty.create("storage_level", 0, 4)
}