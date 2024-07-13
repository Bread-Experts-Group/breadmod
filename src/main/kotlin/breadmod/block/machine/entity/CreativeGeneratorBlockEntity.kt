package breadmod.block.machine.entity

import breadmod.registry.block.ModBlockEntities
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import kotlin.math.min

class CreativeGeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : AbstractMachineBlockEntity.Powered<CreativeGeneratorBlockEntity>(
    ModBlockEntities.CREATIVE_GENERATOR.get(),
    pPos,
    pBlockState,
    EnergyBattery(Int.MAX_VALUE, 0, Int.MAX_VALUE) to null
) {

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: Powered<CreativeGeneratorBlockEntity>) {
//        println("TICKING!!")

        pBlockEntity.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY).let {
            it?.stored = Int.MAX_VALUE
        }
        postTick(pLevel, pPos, pState, pBlockEntity)
    }

    private fun runSignsOne(lambda: (Int) -> Unit) {
        var i = -1
        while (i < 2) {
            lambda(i)
            i += 2
        }
    }

    override fun postTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractMachineBlockEntity<CreativeGeneratorBlockEntity>
    ) {
        val energyHandle = capabilityHolder.capability<EnergyBattery>(ForgeCapabilities.ENERGY)
        val blockEntities = buildList {
            runSignsOne { x -> pLevel.getBlockEntity(pPos.offset(x, 0, 0))?.getCapability(ForgeCapabilities.ENERGY)?.let { add(it) } }
            runSignsOne { y -> pLevel.getBlockEntity(pPos.offset(0, y, 0))?.getCapability(ForgeCapabilities.ENERGY)?.let { add(it) } }
            runSignsOne { z -> pLevel.getBlockEntity(pPos.offset(0, 0, z))?.getCapability(ForgeCapabilities.ENERGY)?.let { add(it) } }
        }

        if(blockEntities.isNotEmpty()) {
            val total = min(energyHandle.bMaxExtract, energyHandle.stored)
            val toDistribute = total / blockEntities.size
            blockEntities.forEach { opt -> opt.ifPresent { present ->
                val energyToPush = min(present.maxEnergyStored - present.energyStored, toDistribute)
                println("present block entity storage/max storage: ${present.energyStored}/${present.maxEnergyStored}")
                println("to distribute: $energyToPush")
                present.receiveEnergy(energyToPush, false)
                energyHandle.extractEnergy(energyToPush, false)

//                distributed += present.receiveEnergy(energyToPush, false)
//                if(distributed > total) { energyHandle.extractEnergy(total, false) }
                return@ifPresent
            } }
        }
    }
}