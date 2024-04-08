package breadmod.block

import breadmod.BreadMod
import breadmod.block.registry.ModBlockEntities.HEATING_ELEMENT
import breadmod.network.CapabilityDataTransfer
import breadmod.network.PacketHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.network.PacketDistributor
import org.joml.Vector3d
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVector3d

class HeatingElementBlock: Block(
    Properties.copy(Blocks.IRON_BLOCK)
        .ignitedByLava()
        .lightLevel { pState -> if(pState.getValue(BlockStateProperties.LIT)) 15 else 0 }
        .emissiveRendering { pState, _, _ -> pState.getValue(BlockStateProperties.LIT) }
), EntityBlock
{
    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(DirectionalBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun stepOn(pLevel: Level, pPos: BlockPos, pState: BlockState, pEntity: Entity) {
        if(pState.getValue(BlockStateProperties.LIT)) pEntity.setSecondsOnFire(3)
    }

    override fun animateTick(pState: BlockState, pLevel: Level, pPos: BlockPos, pRandom: RandomSource) {
        if(pState.getValue(BlockStateProperties.LIT)) {
            val position = pPos.toVector3d().plus(Vector3d(
                when(Direction.getRandom(pRandom).axis.plane) {
                    Direction.Plane.HORIZONTAL -> 0.0
                    Direction.Plane.VERTICAL -> 1.0
                    else -> throw IllegalStateException("")
                },
                pRandom.nextDouble(),
                pRandom.nextDouble()
            ))
            pLevel.addParticle(ParticleTypes.SMOKE, position.x, position.y, position.z, 0.0,0.0, 0.0)
        }
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? =
        if (type == HEATING_ELEMENT.get()) (HEBlockEntity.Companion as BlockEntityTicker<T>) else null

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(DirectionalBlock.FACING, pContext.nearestLookingDirection)
    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(DirectionalBlock.FACING, BlockStateProperties.LIT) }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState) = HEBlockEntity(pPos, pState)

    class HEBlockEntity(
        pPos: BlockPos,
        pState: BlockState,
    ) : BlockEntity(HEATING_ELEMENT.get(), pPos, pState) {
        private val energyHandlerOptional: LazyOptional<IEnergyStorage> = LazyOptional.of {
            object : EnergyStorage(25000, 1000, 0) {
                override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
                    setChanged()
                    return super.receiveEnergy(maxReceive, simulate)
                }
            }
        }

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            if(side == null || side == Direction.DOWN || side == Direction.UP)
                ForgeCapabilities.ENERGY.orEmpty(cap, energyHandlerOptional)
            return super.getCapability(cap, side)
        }

        override fun invalidateCaps() {
            super.invalidateCaps()
            energyHandlerOptional.invalidate()
        }

        override fun saveAdditional(pTag: CompoundTag) {
            super.saveAdditional(pTag)
            pTag.put(BreadMod.ID, CompoundTag().also { dataTag ->
                energyHandlerOptional.ifPresent { dataTag.put("energy", (it as EnergyStorage).serializeNBT()) }
            })
        }

        override fun load(pTag: CompoundTag) {
            super.load(pTag)
            val dataTag = pTag.getCompound(BreadMod.ID)
            if(dataTag.contains("energy"))
                energyHandlerOptional.ifPresent { (it as EnergyStorage).deserializeNBT(dataTag.get("energy")) }
        }

        companion object: BlockEntityTicker<HEBlockEntity> {
            private var tickCount = 0
            override fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: HEBlockEntity) {
                if(pLevel.isClientSide) return

                tickCount++
                pBlockEntity.energyHandlerOptional.ifPresent {
                    if(it.energyStored > 0) {
                        // TODO Better impl
                        if(tickCount % 20 == 0) PacketHandler.INSTANCE.send(
                            PacketDistributor.TRACKING_CHUNK.with { pLevel.getChunkAt(pPos) },
                            CapabilityDataTransfer(pPos, it.energyStored)
                        )

                        val extracted = it.extractEnergy(750, false)
                        pLevel.players().forEach { player ->
                            player.sendSystemMessage(
                                Component.literal("Voltage: ${extracted / 750.0}, Stored: ${it.energyStored}")
                            )
                        }
                    }
                }
            }
        }
    }
}