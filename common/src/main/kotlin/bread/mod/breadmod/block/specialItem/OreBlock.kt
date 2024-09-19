package bread.mod.breadmod.block.specialItem

import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty

@UseBlockStateNBT
class OreBlock : Block(Properties.of()) {
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(ORE_TYPE, OreTypes.STONE)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(ORE_TYPE)
    }

    companion object {
        val ORE_TYPE = object : EnumProperty<OreTypes>("ore_type", OreTypes::class.java, OreTypes.entries) {}

        enum class OreTypes : StringRepresentable {
            BREAD,
            STONE;

            override fun getSerializedName(): String = this.name.lowercase()
        }
    }
}