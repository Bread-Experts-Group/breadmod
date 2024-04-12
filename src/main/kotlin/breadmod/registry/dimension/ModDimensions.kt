package breadmod.registry.dimension

import breadmod.BreadMod.modLocation
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.DimensionType
import java.util.*

typealias BootstrapContext<T> = BootstapContext<T>

val entries = mutableListOf<ModDimensionEntry>()
private var freeze = false

@Suppress("unused")
object ModDimensions {
    fun register(
        name: String,
        dimensionType: (key: ResourceKey<DimensionType>, location: ResourceLocation) -> DimensionType
    ) = modLocation(name).let {
        val resourceKey = ResourceKey.create(Registries.DIMENSION_TYPE, it)
        ModDimensionEntry(
            resourceKey, it,
            dimensionType.invoke(resourceKey, it)
        )
    }

    val BREAD = register("bread") { _, _ ->
        DimensionType(
            OptionalLong.empty(),
            true,
            false,
            true,
            true,
            4.0,
            true,
            false,
            -64, // minY TODO,
            128, // maxY TODO
            128,
            BlockTags.INFINIBURN_OVERWORLD,
            BuiltinDimensionTypes.NETHER_EFFECTS,
            8F,
            DimensionType.MonsterSettings(
                false,
                false,
                ConstantInt.of(0),
                0
            )
        )
    }

    fun bootstrap(ctx: BootstrapContext<DimensionType>) {
        entries.forEach { ctx.register(it.resourceKey, it.dimensionType) }
        freeze = true
    }
}

data class ModDimensionEntry(
    val resourceKey: ResourceKey<DimensionType>,
    val effectLocation: ResourceLocation,
    val dimensionType: DimensionType
) {
    init {
        if(freeze) throw IllegalStateException("Dimension registered after entry list froze")
        entries.add(this)
    }
}