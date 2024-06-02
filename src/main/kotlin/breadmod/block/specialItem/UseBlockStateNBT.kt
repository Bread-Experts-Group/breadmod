package breadmod.block.specialItem

import breadmod.block.specialItem.OreBlock.Companion.ORE_TYPE
import com.google.common.collect.ImmutableMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

annotation class UseBlockStateNBT {
    companion object {
        fun saveState(nbt: CompoundTag, blockState: BlockState) = CompoundTag().also {
            blockState.values.forEach { (_, data) ->
                when(data) {
                    is OreBlock.Companion.OreTypes -> it.putString("oreType", data.name)
                }
            }
            nbt.put("blockState", it)
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        fun loadState(nbt: CompoundTag, block: Block): BlockState {
            val bsTag = nbt.getCompound("blockState")

            return BlockState(block, ImmutableMap.copyOf(buildMap {
                bsTag.allKeys.forEach {
                    when(it) {
                        "oreType" -> this[ORE_TYPE] = OreBlock.Companion.OreTypes.valueOf(bsTag.getString(it))
                    }
                }
            }), null)
        }
    }
}