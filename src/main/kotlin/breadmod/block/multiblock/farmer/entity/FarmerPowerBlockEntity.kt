package breadmod.block.multiblock.farmer.entity

import breadmod.ModMain
import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage

class FarmerPowerBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntities.FARMER_POWER.get(), pPos, pBlockState) {
    val energyHandlerOptional: LazyOptional<EnergyStorage> = LazyOptional.of {
        object : EnergyStorage(100000, 10000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = super.receiveEnergy(maxReceive, simulate).also { setChanged() }
            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = super.extractEnergy(maxExtract, simulate).also { setChanged() }
        }
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return when {
            (cap == ForgeCapabilities.ENERGY) && (side == null || side == this.blockState.getValue(DirectionalBlock.FACING)) -> energyHandlerOptional.cast()
            else -> super.getCapability(cap, side)
        }
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        energyHandlerOptional.invalidate()
    }

    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            energyHandlerOptional.ifPresent { dataTag.put("energy", it.serializeNBT()) }
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(ModMain.ID)
        energyHandlerOptional.ifPresent { it.deserializeNBT(dataTag.get("energy")) }
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
}