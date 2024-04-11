package breadmod.registry.dimension

import breadmod.BreadMod
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.dimension.DimensionType
import net.minecraftforge.registries.DeferredRegister

object ModDimensions {
    val deferredRegister: DeferredRegister<DimensionType> = DeferredRegister.create(Registries.DIMENSION_TYPE, BreadMod.ID)
}