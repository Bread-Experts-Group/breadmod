package breadmod.registry.block

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty

class OreBlock(property: Properties = Properties.copy(Blocks.STONE)): Block(property) {
    init {
        this.registerDefaultState(this.defaultBlockState()
            .setValue(ORE_TYPE, OreTypes.STONE)
        )
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder
            .add(ORE_TYPE)
    }

    companion object {
        enum class OreTypes: StringRepresentable {
            BREAD,
            STONE;

            override fun getSerializedName(): String = this.name.lowercase()
        }
        val ORE_TYPE = object : EnumProperty<OreTypes>("ore_type", OreTypes::class.java, OreTypes.entries.toSet()) {}
    }
}