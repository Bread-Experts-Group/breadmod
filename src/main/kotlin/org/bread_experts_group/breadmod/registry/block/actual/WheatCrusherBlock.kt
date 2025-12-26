package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.registry.block.handler.FERecipeHandler
import org.bread_experts_group.breadmod.registry.block.handler.FERecipeHandler.Companion.getRecipeHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import java.math.BigDecimal

class WheatCrusherBlock : BreadModBlock(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofMenu(): (MenuType<*>, Int, Inventory, BreadModBlockEntity) -> BreadModMenu = ::WheatCrusherMenu
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val itemStorage = ExtendedItemHandler(
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal()),
			ExtendedItemHandler.Slot(Item.DEFAULT_MAX_STACK_SIZE.toBigDecimal())
		)
		val energy = ExtendedEnergyHandler(BigDecimal(10000))
		val energyStorage = { _: BreadModBlockEntity -> energy }
		val recipe = FERecipeHandler(ModRecipeTypes.WHEAT_CRUSHING.get())
		return mapOf(
			Capabilities.ItemHandler.BLOCK to mapOf(null to { _ -> itemStorage }),
			Capabilities.EnergyStorage.BLOCK to mapOf(
				null to energyStorage,
				Direction.UP to energyStorage,
				Direction.DOWN to energyStorage,
				Direction.NORTH to energyStorage,
				Direction.SOUTH to energyStorage,
				Direction.EAST to energyStorage,
				Direction.WEST to energyStorage,
			),
			FERecipeHandler.BLOCK_VOID to mapOf(null to { _ -> recipe })
		)
	}

	override val serverTickBM: BreadModTicker<ServerLevel> = tick@{ entity, level, state, pos ->
		val recipeHandler = entity.getRecipeHandler<WheatCrusherRecipe>()
		val recipe = recipeHandler.recipe ?: return@tick
		if (recipeHandler.advanceAndFinishRecipe()) {
			recipeHandler.progress = 0uL
			recipe.value.consumeItemsAndFluids(recipeHandler.input)
			this.synchronizeEntity(entity)
		}
	}

	override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean =
		!player.isCreative

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(BlockStateProperties.POWERED, false)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED)
	}
}