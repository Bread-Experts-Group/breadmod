package breadmod.block.util

import breadmod.block.ILiquidCombustable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.capability.IFluidHandler
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVector3d
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

fun smokeAtEdge(pLevel: Level, pPos: BlockPos, particle: ParticleOptions, soundEvent: SoundEvent?, plumes: Pair<Int, Int>, facing: Direction) {
    repeat(Random.nextInt(plumes.first, plumes.second)) {
        val normal = facing.opposite.normal
        val from = pPos.offset(normal.div(when(facing) {
            Direction.WEST, Direction.NORTH -> 1
            else -> 2
        })).toVector3d()
        val normalDouble = normal.toVector3d()
        pLevel.addParticle(
            particle,
            from.x, pPos.y + 0.25, from.z,
            (normalDouble.x + (Random.nextDouble() - 0.5)) / 20,
            0.1,
            (normalDouble.z + (Random.nextDouble() - 0.5)) / 20
        )
    }
    if (soundEvent != null && Random.nextDouble() < 0.1) {
        val posDouble = pPos.toVector3d()
        pLevel.playLocalSound(
            posDouble.x, posDouble.y, posDouble.z,
            soundEvent,
            SoundSource.BLOCKS, 1.0f, 1.0f, false
        )
    }
}

fun getBurnTime(stack: ItemStack): Int {
    val burnTime = ForgeHooks.getBurnTime(stack, null)
    return if(burnTime > 0) burnTime else {
        stack.item.let { i ->
            if(i is BucketItem) i.fluid.let { f ->
                if(f is ILiquidCombustable) f.getBurnTime() else null }
            else null
        } ?:
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().getOrNull()?.let {
            val drained = it.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE)
            drained.fluid.let { f -> if(drained.amount > 0) {
                if(f is ILiquidCombustable) f.getBurnTime()
                else ForgeHooks.getBurnTime(f.bucket.defaultInstance, null)
            } else 0 }
        } ?: 0
    }
}

fun handlePlayerFluidInteraction(
    pPlayer: Player, pLevel: Level, pPos: BlockPos,
    stack: ItemStack, handler: IFluidHandler,
    alsoCheck: (() -> Boolean)? = null
): InteractionResult? {
    val fluidCheck = fluidFillCheck(handler, stack)
    if(fluidCheck == null) {
        val drainCheck = handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE)
        if(drainCheck.amount > 0) {
            val filled = if (stack.item == Items.BUCKET && drainCheck.amount >= FluidType.BUCKET_VOLUME) {
                stack.shrink(1)
                pPlayer.inventory.placeItemBackInInventory(drainCheck.fluid.bucket.defaultInstance)
                1000
            } else stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().getOrNull()
                ?.fill(drainCheck, IFluidHandler.FluidAction.EXECUTE) ?: 0

            if(filled > 0) return InteractionResult.SUCCESS
        }
    } else if(alsoCheck?.invoke() != false) {
        if (!pPlayer.isCreative) {
            if(stack.item is BucketItem) {
                stack.shrink(1)
                pPlayer.inventory.placeItemBackInInventory(BucketItem.getEmptySuccessItem(stack, pPlayer))
            } else stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent {
                it.drain(fluidCheck, IFluidHandler.FluidAction.EXECUTE)
            }
        }

        pLevel.playSound(
            null, pPos,
            fluidCheck.fluid.pickupSound.getOrNull() ?: SoundEvents.BUCKET_EMPTY,
            SoundSource.BLOCKS, 1.0f, 1.0f
        )

        handler.fill(fluidCheck, IFluidHandler.FluidAction.EXECUTE)
        return InteractionResult.SUCCESS
    }

    return null
}

fun fluidFillCheck(handler: IFluidHandler, stack: ItemStack): FluidStack? {
    val toMove: FluidStack = stack.item.let {
        if(it is BucketItem) {
            if(it == Items.BUCKET) return null
            else FluidStack(it.fluid, FluidType.BUCKET_VOLUME)
        } else stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().getOrNull()
            ?.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE)
            ?: return null
    }

    if(toMove.fluid.fluidType.isAir) return FluidStack.EMPTY
    return FluidStack(toMove.fluid, handler.fill(toMove, IFluidHandler.FluidAction.SIMULATE))
}