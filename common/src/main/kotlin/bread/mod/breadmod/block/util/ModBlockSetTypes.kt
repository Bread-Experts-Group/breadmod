package bread.mod.breadmod.block.util

import bread.mod.breadmod.registry.sound.ModSounds
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.BlockSetType

object ModBlockSetTypes {
    val BREAD: BlockSetType by lazy {
        BlockSetType(
            "bread",
            true,
            false,
            false,
            BlockSetType.PressurePlateSensitivity.EVERYTHING,
            SoundType.GRASS,
            SoundEvents.GRASS_BREAK,
            SoundEvents.GRASS_PLACE,
            SoundEvents.WOODEN_TRAPDOOR_CLOSE,
            SoundEvents.WOODEN_TRAPDOOR_OPEN,
            SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.WOODEN_BUTTON_CLICK_OFF,
            SoundEvents.WOODEN_BUTTON_CLICK_ON
        )
    }
    val HELL_NAW: BlockSetType by lazy {
        BlockSetType(
            "hell_naw",
            false,
            false,
            false,
            BlockSetType.PressurePlateSensitivity.EVERYTHING,
            SoundType.METAL,
            SoundEvents.METAL_BREAK,
            SoundEvents.METAL_PLACE,
            SoundEvents.IRON_TRAPDOOR_CLOSE,
            SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.EMPTY,
            ModSounds.HELL_NAW.get()
        )
    }
}