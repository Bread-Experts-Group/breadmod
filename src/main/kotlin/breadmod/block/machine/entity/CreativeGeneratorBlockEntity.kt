package breadmod.block.machine.entity

import breadmod.registry.block.ModBlockEntities
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities

class CreativeGeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : AbstractMachineBlockEntity<CreativeGeneratorBlockEntity>(
    ModBlockEntities.CREATIVE_GENERATOR.get(),
    pPos,
    pBlockState,
    ForgeCapabilities.ENERGY to (EnergyBattery(Int.MAX_VALUE, 0, Int.MAX_VALUE) to null)
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

        val sides = (capabilityHolder.capabilities[ForgeCapabilities.ENERGY] ?: return).second
        capabilityHolder.capability<EnergyBattery>(ForgeCapabilities.ENERGY).distribute(
            pLevel, pPos, sides, pState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        )
    }
}