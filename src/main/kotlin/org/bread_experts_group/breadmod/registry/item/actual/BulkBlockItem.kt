package org.bread_experts_group.breadmod.registry.item.actual

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BedPart.FOOT
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import kotlin.jvm.optionals.getOrNull

class BulkBlockItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IMouseItem {
	private var firstPos: BlockPos? = null
	private var secondPos: BlockPos? = null
	private val blockMap: MutableMap<Vec3, Pair<BlockState, BlockPos>> = mutableMapOf()
	private val blockEntityMap: MutableMap<Vec3, BlockEntity> = mutableMapOf()
	private var blockData: BulkBlockData? = null
	private var clearFlag = false

	override fun useOn(context: UseOnContext): InteractionResult {
		val player = context.player ?: return super.useOn(context)
		val clickPos = context.clickedPos

		if (this.firstPos == null && !player.isShiftKeyDown) {
			this.firstPos = clickPos
			player.sendSystemMessage(Component.literal("first pos selected"))
		} else if (this.secondPos == null && player.isShiftKeyDown) {
			this.secondPos = clickPos
			player.sendSystemMessage(Component.literal("second pos selected"))
		}

		return InteractionResult.sidedSuccess(context.level.isClientSide)
	}

	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (usedHand != InteractionHand.MAIN_HAND) return super.use(level, player, usedHand)
		if (player.isShiftKeyDown && this.clearFlag) {
			this.blockMap.clear()
			this.blockEntityMap.clear()
			this.firstPos = null
			this.secondPos = null
			this.clearFlag = false
			this.blockData = null
			player.sendSystemMessage(Component.literal("data cleared"))
		} else if (this.firstPos != null && this.secondPos != null && !player.isShiftKeyDown && !this.clearFlag) {
			val aabb = AABB(this.firstPos!!.toVec3(), this.secondPos!!.toVec3())
			BlockPos.betweenClosedStream(aabb)
				.forEach {
					val blockState = level.getBlockState(it)
					if (blockState.getOptionalValue(BlockStateProperties.BED_PART)
							.getOrNull() == FOOT || blockState.block is AirBlock
					) return@forEach
					val x = it.x - (this.firstPos ?: return@forEach).x
					val y = it.y - (this.firstPos ?: return@forEach).y
					val z = it.z - (this.firstPos ?: return@forEach).z
					this.blockMap[Vec3(x.toDouble(), y.toDouble(), z.toDouble())] = blockState to it
					val blockEntity = level.getBlockEntity(it) ?: return@forEach
					this.blockEntityMap[Vec3(x.toDouble(), y.toDouble(), z.toDouble())] = blockEntity
				}
			player.sendSystemMessage(Component.literal("block map created"))
			val center = this.firstPos!!.toVec3().div(2.0) - this.secondPos!!.toVec3().div(2.0)
			this.blockData = BulkBlockData(this.blockMap, this.blockEntityMap, center, level)
			this.clearFlag = true
		}
		return InteractionResultHolder.sidedSuccess(getStackInPlayerHand(player), level.isClientSide)
	}

	override fun onMouseInputPost(mouseEvent: Post, heldStack: ItemStack, player: Player) {
		if (mouseEvent.action == InputConstants.PRESS) {
			if (mouseEvent.button == InputConstants.MOUSE_BUTTON_MIDDLE) {
				BulkBlockBufferTask.create(player.position(), this.blockData ?: return)
				player.sendSystemMessage(Component.literal("renderer created"))
			}
		}
	}

	data class BulkBlockData(
		val blocks: Map<Vec3, Pair<BlockState, BlockPos>>,
		val blockEntities: Map<Vec3, BlockEntity>,
		val aabbCenter: Vec3,
		val level: Level
	)
}