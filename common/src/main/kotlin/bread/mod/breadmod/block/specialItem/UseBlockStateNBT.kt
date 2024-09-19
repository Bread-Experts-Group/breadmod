package bread.mod.breadmod.block.specialItem

import bread.mod.breadmod.block.specialItem.OreBlock.Companion.ORE_TYPE
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import kotlin.collections.component1
import kotlin.collections.component2

annotation class UseBlockStateNBT {
    companion object {
        fun saveState(nbt: CompoundTag, blockState: BlockState) = CompoundTag().also {
            blockState.values.forEach { (_, data) ->
                when (data) {
                    is OreBlock.Companion.OreTypes -> it.putString("oreType", data.name)
                }
            }
            nbt.put("blockState", it)
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        fun loadState(nbt: CompoundTag, block: Block): BlockState {
            val bsTag = nbt.getCompound("blockState")

            return BlockState(
                block,
                Reference2ObjectArrayMap<Property<*>, Comparable<*>>().also { array ->
                    bsTag.allKeys.forEach {
                        when (it) {
                            "oreType" -> array[ORE_TYPE] = OreBlock.Companion.OreTypes.valueOf(bsTag.getString(it))
                        }
                    }
                },
                null
            )
        }
    }
}
