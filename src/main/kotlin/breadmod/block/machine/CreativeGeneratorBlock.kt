package breadmod.block.machine

import breadmod.block.machine.entity.CreativeGeneratorBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState

class CreativeGeneratorBlock: BaseAbstractMachineBlock.Powered<CreativeGeneratorBlockEntity>(
    ModBlockEntities.CREATIVE_GENERATOR,
    Properties.of(),
    false
) {
    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<CreativeGeneratorBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}