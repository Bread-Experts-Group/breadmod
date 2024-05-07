package breadmod.block.entity

import breadmod.ModMain
import breadmod.capabilities.ModCapabilities
import breadmod.capabilities.temperature.ITemperatureCapability
import breadmod.capabilities.temperature.TemperatureImpl
import breadmod.capabilities.temperature.TemperatureType
import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus

class HeatingElementBlockEntity(
    pPos: BlockPos,
    pState: BlockState,
) : BlockEntity(ModBlockEntities.HEATING_ELEMENT.get(), pPos, pState) {
    private val energyHandlerOptional: LazyOptional<EnergyStorage> = LazyOptional.of {
        object : EnergyStorage(25000, 2000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
                setChanged()
                return super.receiveEnergy(maxReceive, simulate)
            }
        }
    }

    private val temperatureHandlerOptional: LazyOptional<ITemperatureCapability> = LazyOptional.of {
        TemperatureImpl(TemperatureType.ALUMINUM)
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        val currentDirection = this.blockState.getValue(DirectionalBlock.FACING)
        if(cap == ForgeCapabilities.ENERGY && (side == null || side == currentDirection || side == currentDirection.opposite))
            return energyHandlerOptional.cast()
        else if(cap == ModCapabilities.TEMPERATURE)
            return temperatureHandlerOptional.cast()
        return super.getCapability(cap, side)
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        energyHandlerOptional.invalidate()
        temperatureHandlerOptional.invalidate()
    }

    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            energyHandlerOptional.ifPresent { dataTag.put("energy", it.serializeNBT()) }
            temperatureHandlerOptional.ifPresent { dataTag.put("temperature", it.serializeNBT()) }
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(ModMain.ID)
        energyHandlerOptional.ifPresent { it.deserializeNBT(dataTag.get("energy")) }
        temperatureHandlerOptional.ifPresent { it.deserializeNBT(dataTag.get("temperature")) }
    }

    companion object: BlockEntityTicker<HeatingElementBlockEntity> {
        override fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: HeatingElementBlockEntity) {
            if(pLevel !is ServerLevel) return
            pBlockEntity.temperatureHandlerOptional.ifPresent {
                println(it.temperature)
                it.radiate(pLevel, pPos)
            }

            pBlockEntity.energyHandlerOptional.ifPresent { thisStorage ->
                if(thisStorage.energyStored > 0) {
                    val resistiveExtracted = thisStorage.extractEnergy(750, false)
                    if(resistiveExtracted == 750) {
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))
                        val pushExtracted = thisStorage.extractEnergy(1000, true)
                        if(pushExtracted > 0) {
                            val direction = pState.getValue(DirectionalBlock.FACING)
                            val blockEntity = pLevel.getBlockEntity(BlockPos(pPos.plus(direction.normal)))
                            blockEntity?.getCapability(ForgeCapabilities.ENERGY)?.ifPresent { extStorage ->
                                if(extStorage.canReceive()) {
                                    extStorage.receiveEnergy(pushExtracted, false)
                                    thisStorage.extractEnergy(pushExtracted, false)
                                }
                            }
                        }
                        return@ifPresent
                    }
                } else if(pState.getValue(BlockStateProperties.LIT)) {
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
                }
            }
        }
    }
}