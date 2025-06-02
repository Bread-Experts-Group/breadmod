package org.bread_experts_group.breadmod.registry.block.actual.storage

import net.minecraft.ChatFormatting.RED
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlockWithEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.storage.EnergyStorageBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.component.ModDataComponents

class EnergyStorageBlock : BreadModBlockWithEntity(Properties.of()) {
	companion object {
		fun energyToLevel(stored: Int): Int =
			if (stored >= 1000000) 4
			else if (stored > 750000) 3
			else if (stored > 500000) 2
			else if (stored > 250000) 1
			else 0
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = EnergyStorageBlockEntity(pos, state)

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, ModBlockStateProperties.STORAGE_LEVEL)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
		val energyStored = context.itemInHand.getOrDefault(ModDataComponents.ENERGY, 0)
		return this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(ModBlockStateProperties.STORAGE_LEVEL, Companion.energyToLevel(energyStored))
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val energy = stack.getOrDefault(ModDataComponents.ENERGY, 0)
		tooltipComponents.add(Component.literal("energy: $energy").withStyle(RED))
	}

	override fun getCloneItemStack(
		state: BlockState,
		target: HitResult,
		level: LevelReader,
		pos: BlockPos,
		player: Player
	): ItemStack {
		val stack = super.getCloneItemStack(state, target, level, pos, player)
		val entity = level.getBlockEntity(pos) as EnergyStorageBlockEntity
		stack.applyComponents(entity.collectComponents())
		return stack
	}

	override fun getBlockEntityType(): BlockEntityType<*> = ModBlockEntityTypes.ENERGY_STORAGE.get()
}