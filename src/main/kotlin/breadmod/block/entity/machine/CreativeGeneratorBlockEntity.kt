package breadmod.block.entity.machine

import breadmod.registry.block.ModBlockEntityTypes
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities

// todo block entity renderer for mini sun inside of model
class CreativeGeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : AbstractMachineBlockEntity<CreativeGeneratorBlockEntity>(
    ModBlockEntityTypes.CREATIVE_GENERATOR.get(),
    pPos,
    pBlockState,
    ForgeCapabilities.ENERGY to (EnergyBattery(Int.MAX_VALUE, 0, Int.MAX_VALUE) to
            mutableListOf(null, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH))
) {
    override fun postTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractMachineBlockEntity<CreativeGeneratorBlockEntity>
    ) {
        pBlockEntity.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY).let {
            it?.stored = Int.MAX_VALUE
        }
//        println(pLevel.gameTime % 80.0)

        if (pLevel.gameTime % 80.0 == 0.0) {
            // todo toggleable state that plays the beacon deactivate and activate beacon
            pLevel.playSound(null, pPos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f)
        }

        val sides = (capabilityHolder.capabilities[ForgeCapabilities.ENERGY] ?: return).second
        capabilityHolder.capability<EnergyBattery>(ForgeCapabilities.ENERGY).distribute(
            pLevel, pPos, sides, pState.getValue(BlockStateProperties.HORIZONTAL_FACING).opposite
        )
    }
}