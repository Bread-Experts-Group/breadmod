package breadmod.block.machine

import breadmod.block.machine.entity.DoughMachineBlockEntity
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import breadmod.util.capability.FluidContainer
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.network.NetworkHooks
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

class DoughMachineBlock : BaseAbstractMachineBlock.Powered<DoughMachineBlockEntity>(
    ModBlockEntities.DOUGH_MACHINE,
    Properties.of()
        .strength(1f, 5.0f)
        .mapColor(MapColor.COLOR_GRAY)
        .sound(SoundType.METAL)
        .lightLevel { pState -> if(pState.getValue(BlockStateProperties.POWERED)) 5 else 0 }
        .emissiveRendering { pState, _, _ -> pState.getValue(BlockStateProperties.POWERED) },
    true
) {
    override fun canHarvestBlock(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pPlayer: Player): Boolean =
        !pPlayer.isCreative
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING) }

    private val rand: Double
        get() = (Random.nextDouble() - 0.5) * 2

    override fun onDestroyedByPlayer(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pMovedByPiston: Boolean,
        pFluid: FluidState
    ): Boolean {
        val entity = pLevel.getBlockEntity(pPos) as DoughMachineBlockEntity
        if(pState.getValue(BlockStateProperties.POWERED)) {
            pLevel.explode(null, pPos.x.toDouble(), pPos.y.toDouble(), pPos.z.toDouble(), 5f, Level.ExplosionInteraction.NONE)
            val stack = entity.cManager.items[0]
            when(stack.item) {
                ModItems.FLOUR.get() -> {
                    while(stack.count > 0) {
                        val toSubtract = pLevel.random.nextInt(1, 4)
                        val state = ModBlocks.FLOUR_LAYER_BLOCK.get().block.defaultBlockState().setValue(BlockStateProperties.LAYERS, toSubtract)
                        val fallingFlour = FallingBlockEntity.fall(pLevel, pPos.atY(pPos.y + 2), state)
                        fallingFlour.deltaMovement = Vec3(rand, rand, rand)
                        fallingFlour.dropItem = false
                        pLevel.addFreshEntity(fallingFlour)
                        stack.shrink(toSubtract)
                    }
                }
            }
        }

        Containers.dropContents(pLevel, pPos, entity.cManager)
        return super.onDestroyedByPlayer(pState, pLevel, pPos, pPlayer, pMovedByPiston, pFluid)
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult,
    ): InteractionResult {
        if(pLevel.isClientSide || pHand == InteractionHand.OFF_HAND) return InteractionResult.CONSUME
        val blockEntity = pLevel.getBlockEntity(pPos) as? DoughMachineBlockEntity ?: return InteractionResult.FAIL
        val handStack = pPlayer.getItemInHand(pHand)
        val item = handStack.item

        if(item is BucketItem) {
            val handler = blockEntity.capabilityHolder.capability<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)
            val stack = FluidStack(item.fluid, FluidType.BUCKET_VOLUME)

            val filled = handler.fill(stack, IFluidHandler.FluidAction.SIMULATE)
            println(filled)
            if (filled == FluidType.BUCKET_VOLUME) {
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

        NetworkHooks.openScreen(pPlayer as ServerPlayer, blockEntity, pPos)
        return InteractionResult.CONSUME
    }

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<DoughMachineBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}