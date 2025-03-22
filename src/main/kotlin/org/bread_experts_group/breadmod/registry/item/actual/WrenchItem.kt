package org.bread_experts_group.breadmod.registry.item.actual

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask
import org.bread_experts_group.breadmod.registry.Registry.logger
import org.bread_experts_group.breadmod.util.normalizeHitLoc
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.util.BlockScanner
import org.bread_experts_group.breadmod.util.targetFace

class WrenchItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IMouseItem {
	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (level.isClientSide) {
			val partialTick = localClient.timer.gameTimeDeltaTicks
			val vec3 = player.getEyePosition(partialTick)
			val vec31 = player.getViewVector(partialTick)
			val vec32 = vec3.add(vec31.x * 10.0, vec31.y * 10.0, vec31.z * 10.0)
			val clip = level.clip(ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player))
			logger.info(clip.location)
			logger.info(clip.direction)
			logger.info(clip.blockPos)
			logger.info(level.getBlockState(clip.blockPos))
		}
		return super.use(level, player, usedHand)
	}

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		if (level.isClientSide && isSelected) {
			val result = localClient.hitResult
			if (result is BlockHitResult) {
				val state = level.getBlockState(result.blockPos)
				if (!state.`is`(Blocks.AIR)) {
					val player = entity as? Player ?: return
					val pos = result.blockPos
					val loc = result.location
					val x = normalizeHitLoc(loc.x, pos.x)
					val y = normalizeHitLoc(loc.y, pos.y)
					val z = normalizeHitLoc(loc.z, pos.z)
					player.displayClientMessage(
						Component.literal(
							"looking at: [X:$x, Y:$y, Z:$z] Direction: ${result.direction.name} Targeting: ${
								targetFace(
									result.direction,
									x,
									y,
									z
								)
							} "
						)
							.append(state.block.name),
						true
					)
//					localClient.particleEngine.createParticle(
//						ParticleTypes.SMALL_FLAME,
//						loc.x,
//						loc.y,
//						loc.z,
//						0.0, 0.0, 0.0
//					)
				}
			}
		}
	}

	var firstPos: BlockPos? = null
	var secondPos: BlockPos? = null
	var blockMap: MutableMap<BlockPos, BlockState> = mutableMapOf()
	override fun onMouseInputPost(mouseEvent: Post, heldStack: ItemStack, player: Player) {
		if (mouseEvent.action == InputConstants.PRESS) {
			when (mouseEvent.button) {
				InputConstants.MOUSE_BUTTON_LEFT -> {
//					TestCubeBufferTask.create(player.position())
					this.blockMap.clear()
					this.firstPos = null
					this.secondPos = null
				}
				InputConstants.MOUSE_BUTTON_RIGHT -> {
					val result = localClient.hitResult as? BlockHitResult ?: return
					val level = localClient.level ?: return
					if (this.firstPos == null) {
						this.firstPos = result.blockPos
						player.sendSystemMessage(Component.literal("first pos selected"))
					}
					if (this.secondPos == null && player.isShiftKeyDown) {
						this.secondPos = result.blockPos
						player.sendSystemMessage(Component.literal("second pos selected"))
					}
					if (this.firstPos != null && this.secondPos != null && !player.isShiftKeyDown) {
						BlockScanner.scanArea(this.firstPos ?: return, this.secondPos ?: return).forEach {
							this.blockMap[it] = level.getBlockState(it)
						}
						player.sendSystemMessage(Component.literal("block map created"))
					}
				}
				InputConstants.MOUSE_BUTTON_MIDDLE -> {
					BulkBlockBufferTask.create(player.position(), this.blockMap)
					player.sendSystemMessage(Component.literal("renderer created"))
				}
			}
		}
	}
}