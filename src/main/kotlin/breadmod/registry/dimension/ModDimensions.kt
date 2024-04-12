package breadmod.registry.dimension

import breadmod.BreadMod
import java.util.OptionalLong
import net.minecraft.tags.BlockTags
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.dimension.DimensionType
import net.minecraftforge.registries.DeferredRegister
import net.minecraft.world.level.dimension.BuiltinDimensionTypes

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
            BlockTags.INFINIBURN_OVERWORLD,
            BuiltinDimensionTypes.NETHER_EFFECTS,
            8F,
            MonsterSettings(
                false,
                false,
                ConstantInt.of(0),
                0
            )
        )
    }
}
