package breadmod.block.machine

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.registries.RegistryObject

abstract class BaseAbstractMachineBlock<T: AbstractMachineBlockEntity<T>> private constructor(
    private val blockEntityType: RegistryObject<BlockEntityType<T>>,
    properties: Properties
): Block(properties), EntityBlock {
    open fun getClientTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<T>? = null
    open fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<T>? = null

    open fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {}
    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        adjustBlockStateDefinition(pBuilder)
        pBuilder.add(BlockStateProperties.ENABLED)
    }

    final override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity? = blockEntityType.get().create(pPos, pState)
    override fun <R : BlockEntity> getTicker(
        pLevel: Level,
        pState: BlockState,
        pBlockEntityType: BlockEntityType<R>
    ): BlockEntityTicker<R>? {
        return if(pBlockEntityType == blockEntityType) {
            @Suppress("UNCHECKED_CAST")
            val ticker = (if(pLevel.isClientSide) getClientTicker(pLevel, pState)
            else getServerTicker(pLevel, pState)) as BlockEntityTicker<R>
            return BlockEntityTicker { tLevel, tPos, tState, tBlockEntity ->
                if(tState.getValue(BlockStateProperties.ENABLED)) ticker.tick(tLevel, tPos, tState, tBlockEntity)
            }
        } else null
    }

    // TODO CHECK <T>
    abstract class Powered<T: AbstractMachineBlockEntity<T>>(
        blockEntityType: RegistryObject<BlockEntityType<T>>,
        properties: Properties
    ): BaseAbstractMachineBlock<T>(blockEntityType, properties) {
        final override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
            super.createBlockStateDefinition(pBuilder)
            pBuilder.add(BlockStateProperties.POWERED)
        }

        final override fun <R : BlockEntity> getTicker(
            pLevel: Level,
            pState: BlockState,
            pBlockEntityType: BlockEntityType<R>
        ): BlockEntityTicker<R>? = super.getTicker(pLevel, pState, pBlockEntityType)?.let {
            BlockEntityTicker { pLevel, pPos, pState, pBlockEntity ->
                @Suppress("UNCHECKED_CAST")
                val extracted = ((pBlockEntity as T).capabilityHolder.capability(ForgeCapabilities.ENERGY) as EnergyStorage).extractEnergy(1, true)
                if(extracted != 0) it.tick(pLevel, pPos, pState, pBlockEntity)
            }
        }
    }

    abstract class Toggleable<T: AbstractMachineBlockEntity<T>>(
        blockEntityType: RegistryObject<BlockEntityType<T>>,
        properties: Properties
    ): BaseAbstractMachineBlock<T>(blockEntityType, properties) {
        final override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) =
            super.createBlockStateDefinition(pBuilder)
        final override fun <R : BlockEntity> getTicker(pLevel: Level, pState: BlockState, pBlockEntityType: BlockEntityType<R>): BlockEntityTicker<R>? =
            super.getTicker(pLevel, pState, pBlockEntityType)
    }
}