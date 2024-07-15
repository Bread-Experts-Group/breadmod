package breadmodadvanced.block.machine.entity

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.StorageDirection
import breadmodadvanced.registry.ModConfigurationAdv
import breadmodadvanced.registry.block.ModBlockEntitiesAdv
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.capability.templates.FluidTank
import kotlin.math.min

class DieselGeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity<DieselGeneratorBlockEntity>(
    ModBlockEntitiesAdv.DIESEL_GENERATOR.get(),
    pPos,
    pBlockState,
    ForgeCapabilities.ENERGY to (EnergyBattery(50000, 0, 2000) to mutableListOf(Direction.NORTH, null)),
    ForgeCapabilities.FLUID_HANDLER to (FluidContainer(mutableMapOf(FluidTank(8000) to StorageDirection.STORE_ONLY)) to mutableListOf(Direction.UP, null))
) {
    init {
        capabilityHolder.capability<FluidContainer>(ForgeCapabilities.FLUID_HANDLER).insertFluidCheck = { stack, _ ->
            ForgeHooks.getBurnTime(stack.fluid.bucket.defaultInstance, null) > 0
        }
    }

    private var burnTime = 0

    fun addBurnTime(ticks: Int): Boolean {
        val new = burnTime + ticks
        if(ticks == 0 || new > MAX_BURN_TIME.get()) return false
        burnTime = new
        setChanged()
        return true
    }

    override fun adjustSaveAdditional(pTag: CompoundTag) { pTag.putInt("BurnTime", burnTime) }
    override fun adjustLoad(pTag: CompoundTag) { burnTime = pTag.getInt("BurnTime") }

    override fun preTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractMachineBlockEntity<DieselGeneratorBlockEntity>
    ) {
        capabilityHolder.capability<FluidContainer>(ForgeCapabilities.FLUID_HANDLER).let {
            val tank = it.allTanks.first()
            val maxDrink = min(tank.fluid.amount, FluidType.BUCKET_VOLUME / 75)

            val lightLevel = tank.fluid.fluid.fluidType.lightLevel
            if(lightLevel != pState.getValue(BlockStateProperties.LEVEL))
                pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LEVEL, tank.fluid.fluid.fluidType.lightLevel))

            if(
                burnTime < MAX_BURN_TIME.get() &&
                addBurnTime(
                    ((maxDrink.toDouble() / FluidType.BUCKET_VOLUME) * ForgeHooks.getBurnTime(tank.fluid.fluid.bucket.defaultInstance, null))
                        .toInt()
                )
            ) tank.fluid.amount -= maxDrink
        }
    }

    override fun postTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractMachineBlockEntity<DieselGeneratorBlockEntity>
    ) {
        var isLit = false

        val holder = capabilityHolder.capabilities[ForgeCapabilities.ENERGY]
        val battery = (holder!!.first.resolve().get() as EnergyBattery)

        val rfTick = RF_PER_TICK.get()
        if(burnTime > 0 && battery.stored < battery.maxEnergyStored - rfTick) {
            isLit = true
            battery.stored += rfTick
            burnTime--
        }

        if (battery.stored > 0) {
            battery.distribute(
                pLevel, pPos, holder.second,
                pState.getValue(BlockStateProperties.HORIZONTAL_FACING).opposite
            )
        }

        if (isLit != pState.getValue(BlockStateProperties.LIT))
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, isLit))
    }

    private companion object {
        val MAX_BURN_TIME = ModConfigurationAdv.COMMON.DIESEL_GENERATOR_MAX_BURN_TIME_TICKS
        val RF_PER_TICK = ModConfigurationAdv.COMMON.DIESEL_GENERATOR_RF_PER_TICK
    }
}