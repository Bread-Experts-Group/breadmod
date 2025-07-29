package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.render.entity.block.EnergyStorageRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.util.floatRoundEven
import java.math.BigDecimal
import java.util.Optional
import kotlin.math.roundToInt

class EnergyStorageBlock : BreadModBlock(Properties.of()) {
	override fun ofCapabilities(): CapabilityMap {
		val container = ExtendedEnergyHandler(
			BigDecimal.TWO.pow(256)
		)
		val storage = { _: BreadModBlockEntity, _: Any? -> container }
		return mapOf(
			Capabilities.EnergyStorage.BLOCK to mapOf(
				Optional.empty<Direction>() to storage,
				Optional.of(Direction.UP) to storage,
				Optional.of(Direction.DOWN) to storage,
				Optional.of(Direction.NORTH) to storage,
				Optional.of(Direction.SOUTH) to storage,
				Optional.of(Direction.EAST) to storage,
				Optional.of(Direction.WEST) to storage,
			)
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::EnergyStorageRenderer

	override val serverTickBM: BreadModTicker<ServerLevel> = { entity, level, state, pos ->
		val energy = entity.getCapability(Capabilities.EnergyStorage.BLOCK) as ExtendedEnergyHandler
		level.setBlockAndUpdate(
			pos,
			state.setValue(
				ModBlockStateProperties.STORAGE_LEVEL,
				((energy.bigAmount.divide(energy.bigCapacity, floatRoundEven)).toFloat() / (1 / 13f))
					.roundToInt()
			)
		)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, ModBlockStateProperties.STORAGE_LEVEL)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
		return this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: Item.TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		val amount = stack.getOrDefault(ModDataComponents.ENERGY, BigDecimal.ZERO)
		val capacity = stack.getOrDefault(ModDataComponents.ENERGY_CAPACITY, BigDecimal.ONE)
		tooltipComponents.add(Component.literal("$amount / $capacity").withStyle(ChatFormatting.RED))
	}
}