package breadmod.util.capability

import net.minecraft.nbt.Tag
import net.minecraftforge.common.util.INBTSerializable

interface ICapabilitySavable<out T: Tag>: INBTSerializable<@UnsafeVariance T> {
    var changed: (() -> Unit)?
}