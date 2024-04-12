package breadmod.registry.dimension

import breadmod.BreadMod
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.dimension.DimensionType
import net.minecraftforge.registries.DeferredRegister

object ModDimensions {
    val deferredRegister: DeferredRegister<DimensionType> = DeferredRegister.create(Registries.DIMENSION_TYPE, BreadMod.ID)
    val BREAD = deferredRegister.register("bread") { 
        DimensionType(
            OptionalLong.empty(),
            true,
            false,
            true,
            true,
            4.0,
            true,
            false,
            -50, // minY TODO,
            100, // maxY TODO
            100,
            TODO("Figure this out later https://mcstreetguy.github.io/ForgeJavaDocs/1.20.1-latest/net/minecraft/world/level/dimension/DimensionType.html")
        )
    }
}
