package breadmodadvanced.block.machine

import breadmod.block.machine.BaseAbstractMachineBlock
import breadmod.block.util.smokeAtEdge
import breadmod.util.capability.FluidContainer
import breadmodadvanced.block.machine.entity.DieselGeneratorBlockEntity
import breadmodadvanced.registry.block.ModBlockEntitiesAdv
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.capability.IFluidHandler
import kotlin.jvm.optionals.getOrNull

class DieselGeneratorBlock: BaseAbstractMachineBlock.Toggleable<DieselGeneratorBlockEntity>(
    ModBlockEntitiesAdv.DIESEL_GENERATOR,
    Properties.of().noOcclusion()
        .strength(1.5f, 5.0f)
        .sound(SoundType.METAL)
        .lightLevel { state -> (if(state.getValue(BlockStateProperties.LIT)) 8 else 0) + state.getValue(BlockStateProperties.LEVEL) }
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT, BlockStateProperties.LEVEL, BlockStateProperties.OPEN)
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult
    ): InteractionResult {
        if(pLevel.isClientSide || pHand == InteractionHand.OFF_HAND) return InteractionResult.CONSUME
        val handStack = pPlayer.getItemInHand(pHand)
        val item = handStack.item

        if(item is BucketItem) {
            val burnTime = ForgeHooks.getBurnTime(handStack, null)
            if(burnTime > 0) {
                val blockEntity = pLevel.getBlockEntity(pPos) as? DieselGeneratorBlockEntity ?: return InteractionResult.FAIL
                val handler = blockEntity.capabilityHolder.capability<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)
                val stack = FluidStack(item.fluid, FluidType.BUCKET_VOLUME)

                val filled = handler.fill(stack, IFluidHandler.FluidAction.SIMULATE)
                if (filled == FluidType.BUCKET_VOLUME && blockEntity.addBurnTime(burnTime)) {
                    if (!pPlayer.isCreative) {
                        handStack.shrink(1)
                        pPlayer.inventory.placeItemBackInInventory(BucketItem.getEmptySuccessItem(handStack, pPlayer))
                    }
                    pLevel.playSound(
                        null, pPos,
                        item.fluid.pickupSound.getOrNull() ?: SoundEvents.BUCKET_EMPTY,
                        SoundSource.BLOCKS, 1.0f, 1.0f
                    )
                    handler.fill(stack, IFluidHandler.FluidAction.EXECUTE)

                    return InteractionResult.SUCCESS
                }
            }
        }
        return InteractionResult.FAIL
    }

    override fun animateTick(pState: BlockState, pLevel: Level, pPos: BlockPos, pRandom: RandomSource) {
        if (pState.getValue(BlockStateProperties.LIT))
            smokeAtEdge(
                pLevel, pPos,
                ParticleTypes.LARGE_SMOKE, SoundEvents.ENDER_DRAGON_GROWL,
                5 to 8, pState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            )
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(BlockStateProperties.LIT, false)
            .setValue(BlockStateProperties.LEVEL, 0)
            .setValue(BlockStateProperties.OPEN, false)
    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<DieselGeneratorBlockEntity> =
        BlockEntityTicker { tLevel, pPos, tState, pBlockEntity -> pBlockEntity.tick(tLevel, pPos, tState, pBlockEntity) }
}