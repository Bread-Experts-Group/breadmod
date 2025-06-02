package org.bread_experts_group.breadmod.registry.item.actual

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
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.Registry.logger
import org.bread_experts_group.breadmod.util.normalizeHitLoc
import org.bread_experts_group.breadmod.util.targetFace

class WrenchItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (level.isClientSide) {
			val partialTick = localClient.timer.gameTimeDeltaTicks
			val vec3 = player.getEyePosition(partialTick)
			val vec31 = player.getViewVector(partialTick)
			val vec32 = vec3.add(vec31.x * 10.0, vec31.y * 10.0, vec31.z * 10.0)
			val clip = level.clip(ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player))
//			logger.info(clip.location)
//			logger.info(clip.direction)
//			logger.info(clip.blockPos)
//			logger.info(level.getBlockState(clip.blockPos))
			logger.info("item handler id for ${clip.direction}: ${level.getCapability(Capabilities.ItemHandler.BLOCK, clip.blockPos, clip.direction).toString()}")
		}
		return super.use(level, player, usedHand)
	}

	override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
		if (level.isClientSide && isSelected) {
			val result = localClient.hitResult ?: return
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
}