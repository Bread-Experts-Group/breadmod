package breadmod.block

import net.minecraft.world.level.block.state.properties.IntegerProperty

class ModBlockStateProperties {
    val storageLevel: IntegerProperty = IntegerProperty.create("storage_level", 0, 4)
}