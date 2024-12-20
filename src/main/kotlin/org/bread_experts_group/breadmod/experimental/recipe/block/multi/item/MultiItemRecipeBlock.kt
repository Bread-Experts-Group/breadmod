package org.bread_experts_group.breadmod.experimental.recipe.block.multi.item

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class MultiItemRecipeBlock : BaseEntityBlock(Properties.of()) {
	companion object {
		val CODEC : MapCodec<MultiItemRecipeBlock> = simpleCodec { MultiItemRecipeBlock() }
	}

	override fun codec() : MapCodec<out BaseEntityBlock> = Companion.CODEC
	override fun onRemove(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		newState : BlockState,
		movedByPiston : Boolean
	) {
		level.invalidateCapabilities(pos)
		super.onRemove(state, level, pos, newState, movedByPiston)
	}

	override fun useWithoutItem(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		player : Player,
		hitResult : BlockHitResult
	) : InteractionResult {
		if (!level.isClientSide) {
			val entity = level.getBlockEntity(pos) as? MultiItemRecipeBlockEntity ?: return InteractionResult.FAIL
			player.openMenu(entity, pos)
		}
		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun newBlockEntity(pos : BlockPos, state : BlockState) : BlockEntity =
		MultiItemRecipeBlockEntity(pos, state)

	override fun <T : BlockEntity?> getTicker(
		level : Level,
		state : BlockState,
		blockEntityType : BlockEntityType<T>
	) : BlockEntityTicker<T>? = BaseEntityBlock.createTickerHelper(
		blockEntityType,
		ModBlockEntityTypes.MULTI_ITEM_TEST.get()
	) { tLevel : Level, tPos : BlockPos, tState : BlockState, tBlockEntity : MultiItemRecipeBlockEntity ->
		tBlockEntity.tick(tLevel, tPos, tState)
	}
}