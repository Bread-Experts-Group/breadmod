package breadmod.block.machine.entity

import breadmod.block.machine.CraftingManager
import breadmod.registry.ModConfiguration
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.BucketItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.capabilities.ForgeCapabilities
import kotlin.random.Random

class GeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity<GeneratorBlockEntity>(
    ModBlockEntityTypes.GENERATOR.get(),
    pPos,
    pBlockState,
    ForgeCapabilities.ENERGY to (EnergyBattery(50000, 0, 2000) to mutableListOf(Direction.WEST, null)),
    ForgeCapabilities.ITEM_HANDLER to (IndexableItemHandler(mutableListOf(64 to StorageDirection.STORE_ONLY)) to mutableListOf(Direction.UP, null))
) {
    init {
        capabilityHolder.capability<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER).insertItemCheck = { _, stack, _ ->
            stack.item !is BucketItem && ForgeHooks.getBurnTime(stack, null) > 0
        }
    }

    val cManager = CraftingManager(
        capabilityHolder.capability(ForgeCapabilities.ITEM_HANDLER),
        listOf(0),
        1, 1,
        this
    )
    private var burnTime = 0

    fun addBurnTime(ticks: Int): Boolean {
        val new = burnTime + ticks
        if(ticks == 0 || new > MAX_BURN_TIME.get()) return false
        level?.playSound(
            null,
            worldPosition, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS,
            1.0f, 0.8f + ((Random.nextFloat() - 0.5f) / 10f)
        )
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
        pBlockEntity: AbstractMachineBlockEntity<GeneratorBlockEntity>
    ) {
        capabilityHolder.capability<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER).let {
            if(burnTime < MAX_BURN_TIME.get() && addBurnTime(ForgeHooks.getBurnTime(it[0], null)))
                it[0].shrink(1)
        }
    }

    override fun postTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractMachineBlockEntity<GeneratorBlockEntity>
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
                pState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            )
        }

        if (isLit != pState.getValue(BlockStateProperties.LIT))
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, isLit))
    }

    private companion object {
        val MAX_BURN_TIME = ModConfiguration.COMMON.GENERATOR_MAX_BURN_TIME_TICKS
        val RF_PER_TICK = ModConfiguration.COMMON.GENERATOR_RF_PER_TICK
    }
}