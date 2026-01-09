package org.bread_experts_group.breadmod.network.serverbound.physics_grid

import net.minecraft.ChatFormatting
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.Direction
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateBlockUpdatePhysicsGridPacket
import org.bread_experts_group.breadmod.network.payloadType
import kotlin.math.abs
import kotlin.math.max

data class EncapsulateUseItemOnPhysicsGridPacket(
	val id: Long,
	val encapsulate: ServerboundUseItemOnPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulateUseItemOnPhysicsGridPacket> = payloadType("use_itm_on_phys_grid")
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulateUseItemOnPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulateUseItemOnPhysicsGridPacket::id,
				ServerboundUseItemOnPacket.STREAM_CODEC, EncapsulateUseItemOnPhysicsGridPacket::encapsulate,
				::EncapsulateUseItemOnPhysicsGridPacket
			)

		private fun wasBlockPlacementAttempt(player: ServerPlayer, stack: ItemStack): Boolean {
			if (stack.isEmpty) return false
			val item: Item = stack.item
			return (item is BlockItem || item is BucketItem) && !player.cooldowns.isOnCooldown(item)
		}

		fun canInteractWithBlock(
			player: ServerPlayer,
			x: Double, y: Double, z: Double,
			distance: Double
		): Boolean {
			val interactionDistance: Double = player.blockInteractionRange() + distance
			val eyePosition = player.eyePosition
			return Mth.lengthSquared(
				max(max(x - eyePosition.x, eyePosition.x - (x + 1)), 0.0),
				max(max(y - eyePosition.y, eyePosition.y - (y + 1)), 0.0),
				max(max(z - eyePosition.z, eyePosition.z - (z + 1)), 0.0)
			) < interactionDistance * interactionDistance
		}

		fun handleServerboundPacket(data: EncapsulateUseItemOnPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = PhysicsGrid.Companion.serverGrids[data.id] ?: return@enqueueWork
				val hand: InteractionHand = data.encapsulate.hand
				val player = context.player() as ServerPlayer
				val handStack: ItemStack = player.getItemInHand(hand)
				if (handStack.isItemEnabled(grid.microLevel.enabledFeatures())) {
					val hitResult: BlockHitResult = data.encapsulate.hitResult
					val vec3 = hitResult.getLocation()
					val blockPos = hitResult.blockPos
					if (
						this.canInteractWithBlock(
							player,
							blockPos.x + grid.pos.x,
							blockPos.y + grid.pos.y,
							blockPos.z + grid.pos.z,
							1.0
						)
					) {
						val vec31 = vec3.subtract(Vec3.atCenterOf(blockPos))
						val d0 = 1.0000001
						if (abs(vec31.x()) < d0 && abs(vec31.y()) < d0 && abs(vec31.z()) < d0) {
							val direction = hitResult.direction
							player.resetLastActionTime()
							val maxBuildHeight: Int = grid.microLevel.maxBuildHeight
							if (blockPos.y < maxBuildHeight) {
								if (grid.microLevel.mayInteract(player, blockPos)) {
									val interactionResult: InteractionResult = player
										.gameMode
										.useItemOn(player, grid.microLevel, handStack, hand, hitResult)
									if (interactionResult.consumesAction()) {
										CriteriaTriggers.ANY_BLOCK_USE.trigger(
											player,
											hitResult.blockPos,
											handStack.copy()
										)
									}

									if (
										direction == Direction.UP &&
										!interactionResult.consumesAction()
										&& blockPos.y >= maxBuildHeight - 1
										&& this.wasBlockPlacementAttempt(
											player,
											handStack
										)
									) player.sendSystemMessage(
										Component.translatable(
											"build.tooHigh",
											maxBuildHeight - 1
										).withStyle(ChatFormatting.RED),
										true
									) else if (interactionResult.shouldSwing()) player.swing(hand, true)
								}
							} else player.sendSystemMessage(
								Component.translatable(
									"build.tooHigh",
									maxBuildHeight - 1
								).withStyle(ChatFormatting.RED),
								true
							)

							PacketDistributor.sendToPlayer(
								player,
								EncapsulateBlockUpdatePhysicsGridPacket(
									grid.id,
									ClientboundBlockUpdatePacket(grid.microLevel, blockPos)
								),
								EncapsulateBlockUpdatePhysicsGridPacket(
									grid.id,
									ClientboundBlockUpdatePacket(grid.microLevel, blockPos.relative(direction))
								)
							)
						} else {
							LogManager.getLogger().warn(
								"Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.",
								player.gameProfile.name,
								vec3,
								blockPos
							)
						}
					}
				}
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToServer(
			this.TYPE, this.STREAM_CODEC,
			this::handleServerboundPacket
		)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}