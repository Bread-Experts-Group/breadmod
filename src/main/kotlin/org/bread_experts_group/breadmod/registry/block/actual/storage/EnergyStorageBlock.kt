package org.bread_experts_group.breadmod.registry.block.actual.storage

import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import net.neoforged.neoforge.capabilities.BaseCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import org.bread_experts_group.breadmod.client.render.entity.block.EnergyStorageRenderer
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.BreadModTicker
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import java.util.Optional

class EnergyStorageBlock : BreadModBlock(Properties.of()) {
	private fun energyToLevel(stored: Int): Int =
		if (stored >= 1000000) 4
		else if (stored > 750000) 3
		else if (stored > 500000) 2
		else if (stored > 250000) 1
		else 0

	override fun ofCapabilities(): Map<BaseCapability<*, *>, Map<Optional<Any>, Any>> = mapOf(
		Capabilities.EnergyStorage.BLOCK to mapOf(Optional.empty<Any>() to EnergyStorage(1_000_000))
	)

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::EnergyStorageRenderer

	override val commonTickBM: BreadModTicker<Level> = { entity, level, state, pos ->
		val energy = entity.getCapability(Capabilities.EnergyStorage.BLOCK)
		level.setBlockAndUpdate(
			pos,
			state.setValue(ModBlockStateProperties.STORAGE_LEVEL, this.energyToLevel(energy.energyStored))
		)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, ModBlockStateProperties.STORAGE_LEVEL)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
		val energyStored = context.itemInHand.getOrDefault(ModDataComponents.ENERGY, 0)
		return this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(ModBlockStateProperties.STORAGE_LEVEL, this.energyToLevel(energyStored))
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: Item.TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val energy = stack.getOrDefault(ModDataComponents.ENERGY, 0)
		tooltipComponents.add(Component.literal("energy: $energy").withStyle(ChatFormatting.RED))
	}

	override fun getCloneItemStack(
		state: BlockState,
		target: HitResult,
		level: LevelReader,
		pos: BlockPos,
		player: Player
	): ItemStack {
		val stack = super.getCloneItemStack(state, target, level, pos, player)
		val entity = level.getBlockEntity(pos) as BreadModBlockEntity
		stack.applyComponents(entity.collectComponents())
		return stack
	}
}