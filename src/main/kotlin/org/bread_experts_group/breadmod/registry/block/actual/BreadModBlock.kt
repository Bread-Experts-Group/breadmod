package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.ItemInteractionResult.SUCCESS
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.RenderShape.MODEL
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.BaseCapability
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.registries.DeferredHolder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import java.util.Optional

typealias BreadModTicker<T> = ((entity: BreadModBlockEntity, level: T, state: BlockState, pos: BlockPos) -> Unit)?

abstract class BreadModBlock(
	blockProperties: Properties
) : BaseEntityBlock(blockProperties) {
	protected val logger: Logger by lazy { LogManager.getLogger("${this.descriptionId} / ${this.name.string}") }
	final override fun codec(): MapCodec<out BaseEntityBlock> = BlockBehaviour.simpleCodec { this }
	override fun getRenderShape(state: BlockState): RenderShape = MODEL

	var blockEntityType: DeferredHolder<BlockEntityType<*>, BlockEntityType<*>>? = null
	open fun ofCapabilities(): Map<BaseCapability<*, *>, Map<Optional<Any>, Any>> = mapOf()
	open fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		null

	open fun shouldCreateEntity(pos: BlockPos, state: BlockState): Boolean = true
	final override fun newBlockEntity(pos: BlockPos, state: BlockState): BreadModBlockEntity? {
		if (this.blockEntityType == null || !this.shouldCreateEntity(pos, state)) return null
		return BreadModBlockEntity(
			this.blockEntityType!!.get(), pos, state,
			this.ofCapabilities()
		)
	}

	open val commonTickBM: BreadModTicker<Level> = null
	open val clientTickBM: BreadModTicker<ClientLevel> = null
	open val serverTickBM: BreadModTicker<ServerLevel> = null

	final override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T?>
	): BlockEntityTicker<T>? = if (
		this.commonTickBM != null
		|| (level.isClientSide && this.clientTickBM != null)
		|| (!level.isClientSide && this.serverTickBM != null)
	) {
		if (level.isClientSide) BlockEntityTicker<T> { level, pos, state, entity ->
			this.clientTickBM?.invoke(entity as BreadModBlockEntity, level as ClientLevel, state, pos)
			this.commonTickBM?.invoke(entity as BreadModBlockEntity, level, state, pos)
		} else BlockEntityTicker<T> { level, pos, state, entity ->
			this.serverTickBM?.invoke(entity as BreadModBlockEntity, level as ServerLevel, state, pos)
			this.commonTickBM?.invoke(entity as BreadModBlockEntity, level, state, pos)
		}
	} else null

	open fun useItemOnBM(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

	final override fun useItemOn(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult {
		if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.direction)) return SUCCESS
		return this.useItemOnBM(stack, state, level, pos, player, hand, hitResult)
	}
}