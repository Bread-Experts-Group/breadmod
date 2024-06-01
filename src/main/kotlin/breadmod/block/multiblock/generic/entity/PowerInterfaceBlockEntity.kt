package breadmod.block.multiblock.farmer.entity

import breadmod.ModMain
import breadmod.registry.block.ModBlockEntities
import breadmod.util.CapabilityHolder
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

class PowerInterfaceBlockEntity(pPos: BlockPos, pBlockState: BlockState) : BlockEntity(ModBlockEntities.MULTIBLOCK_GENERIC_POWER.get(), pPos, pBlockState) {
    val capabilities = CapabilityHolder(mapOf(
        ForgeCapabilities.ENERGY to (object : EnergyStorage(100000, 10000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = super.receiveEnergy(maxReceive, simulate).also { setChanged() }
            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = super.extractEnergy(maxExtract, simulate).also { setChanged() }
        } to null)
    ))

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
        capabilities.capabilitySided(cap, blockState.getValue(DirectionalBlock.FACING), side) ?: super.getCapability(cap, side)

    override fun invalidateCaps() {
        super.invalidateCaps()
        capabilities.invalidate()
    }

    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, capabilities.serialize(CompoundTag()))
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        capabilities.deserialize(pTag.getCompound(ModMain.ID))
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
}