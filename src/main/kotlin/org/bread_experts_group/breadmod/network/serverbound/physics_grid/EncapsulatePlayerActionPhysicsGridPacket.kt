package org.bread_experts_group.breadmod.network.serverbound.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.block.GameMasterBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.CommonHooks
import net.neoforged.neoforge.common.util.TriState
import net.neoforged.neoforge.event.EventHooks
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateBlockUpdatePhysicsGridPacket
import org.bread_experts_group.breadmod.network.payloadType
import org.bread_experts_group.breadmod.network.serverbound.physics_grid.EncapsulateUseItemOnPhysicsGridPacket.Companion.canInteractWithBlock

data class EncapsulatePlayerActionPhysicsGridPacket(
	val id: Long,
	val encapsulate: ServerboundPlayerActionPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulatePlayerActionPhysicsGridPacket> = payloadType("plr_act_phys_grid")
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulatePlayerActionPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulatePlayerActionPhysicsGridPacket::id,
				ServerboundPlayerActionPacket.STREAM_CODEC, EncapsulatePlayerActionPhysicsGridPacket::encapsulate,
				::EncapsulatePlayerActionPhysicsGridPacket
			)

		fun handleServerboundPacket(data: EncapsulatePlayerActionPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = PhysicsGrid.Companion.serverGrids[data.id] ?: return@enqueueWork
				val player = context.player() as ServerPlayer
				val blockPos: BlockPos = data.encapsulate.pos
				player.resetLastActionTime()
				when (val action: ServerboundPlayerActionPacket.Action = data.encapsulate.action) {
					ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND -> {
						if (!player.isSpectator) {
							val event = CommonHooks.onLivingSwapHandItems(player)
							if (event.isCanceled()) return@enqueueWork
							player.setItemInHand(InteractionHand.OFF_HAND, event.itemSwappedToOffHand)
							player.setItemInHand(InteractionHand.MAIN_HAND, event.itemSwappedToMainHand)
							player.stopUsingItem()
						}

						return@enqueueWork
					}
					ServerboundPlayerActionPacket.Action.DROP_ITEM -> {
						if (!player.isSpectator) player.drop(false)
						return@enqueueWork
					}
					ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS -> {
						if (!player.isSpectator) player.drop(true)
						return@enqueueWork
					}
					ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM -> {
						player.releaseUsingItem()
						return@enqueueWork
					}
					ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
					ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK,
					ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK -> {
						val sequence = data.encapsulate.sequence
						val face = data.encapsulate.direction
						val maxBuildHeight = grid.microLevel.maxBuildHeight
						val event = CommonHooks.onLeftClickBlock(player, blockPos, face, action)
						if (event.isCanceled()) return@enqueueWork
						if (
							!canInteractWithBlock(
								player,
								blockPos.x + grid.pos.x,
								blockPos.y + grid.pos.y,
								blockPos.z + grid.pos.z,
								1.0
							)
						) {
//							debugLogging(pos, false, sequence, "too far")
							return@enqueueWork
						}
						if (blockPos.y >= maxBuildHeight) {
							PacketDistributor.sendToPlayer(
								player,
								EncapsulateBlockUpdatePhysicsGridPacket(
									grid.id,
									ClientboundBlockUpdatePacket(grid.microLevel, blockPos)
								)
							)
//							this.debugLogging(pos, false, sequence, "too high")
							return@enqueueWork
						}
						val gameMode = player.gameMode
						fun removeBlock(blockPos: BlockPos, state: BlockState, canHarvest: Boolean): Boolean {
							val removed = state.onDestroyedByPlayer(
								grid.microLevel,
								blockPos,
								player,
								canHarvest,
								grid.microLevel.getFluidState(blockPos)
							)
							if (removed) state.block.destroy(grid.microLevel, blockPos, state)
							return removed
						}

						fun destroyBlock(blockPos: BlockPos): Boolean {
							val blockstate1: BlockState = grid.microLevel.getBlockState(blockPos)
							val event = CommonHooks.fireBlockBreak(
								grid.microLevel,
								gameMode.gameModeForPlayer,
								player, blockPos, blockstate1
							)
							if (event.isCanceled()) return false
							val blockentity: BlockEntity? = grid.microLevel.getBlockEntity(blockPos)
							val block = blockstate1.block
							if (block is GameMasterBlock && !player.canUseGameMasterBlocks()) {
								grid.microLevel.sendBlockUpdated(blockPos, blockstate1, blockstate1, 3)
								return false
							}
							if (player.blockActionRestricted(grid.microLevel, blockPos, gameMode.gameModeForPlayer))
								return false
							val blockstate = block.playerWillDestroy(grid.microLevel, blockPos, blockstate1, player)

							if (gameMode.isCreative) {
								removeBlock(blockPos, blockstate, false)
								return true
							}
							val itemstack: ItemStack = player.mainHandItem
							val itemstack1 = itemstack.copy()
							val flag1 = blockstate.canHarvestBlock(
								grid.microLevel,
								blockPos,
								player
							) // previously player.hasCorrectToolForDrops(blockstate)
							itemstack.mineBlock(grid.microLevel, blockstate, blockPos, player)
							val flag: Boolean = removeBlock(blockPos, blockstate, flag1)

							if (flag1 && flag) {
								block.playerDestroy(
									grid.microLevel,
									player,
									blockPos,
									blockstate,
									blockentity,
									itemstack1
								)
							}
							// Neo: Fire the PlayerDestroyItemEvent if the tool was broken at any point during the break process
							if (itemstack.isEmpty && !itemstack1.isEmpty) {
								EventHooks.onPlayerDestroyItem(
									player,
									itemstack1,
									InteractionHand.MAIN_HAND
								)
							}

							return true
						}

						fun destroyAndAck(blockPos: BlockPos, sequence: Int, message: String?) {
							if (destroyBlock(blockPos)) {
//								gameMode.debugLogging(pos, true, sequence, message)
							} else {
								PacketDistributor.sendToPlayer(
									player,
									EncapsulateBlockUpdatePhysicsGridPacket(
										grid.id,
										ClientboundBlockUpdatePacket(grid.microLevel, blockPos)
									)
								)
//								gameMode.debugLogging(pos, false, sequence, message)
							}
						}
						when (action) {
							ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK -> {
								if (!grid.microLevel.mayInteract(player, blockPos)) {
									PacketDistributor.sendToPlayer(
										player,
										EncapsulateBlockUpdatePhysicsGridPacket(
											grid.id,
											ClientboundBlockUpdatePacket(grid.microLevel, blockPos)
										)
									)
//									this.debugLogging(pos, false, sequence, "may not interact")
									return@enqueueWork
								}

								if (gameMode.isCreative) {
									destroyAndAck(blockPos, sequence, "creative destroy")
									return@enqueueWork
								}

								if (
									player.blockActionRestricted(grid.microLevel, blockPos, gameMode.gameModeForPlayer)
								) {
									PacketDistributor.sendToPlayer(
										player,
										EncapsulateBlockUpdatePhysicsGridPacket(
											grid.id,
											ClientboundBlockUpdatePacket(grid.microLevel, blockPos)
										)
									)
//									this.debugLogging(pos, false, sequence, "block action restricted")
									return@enqueueWork
								}

								gameMode.destroyProgressStart = gameMode.gameTicks
								var f = 1.0f
								val blockState: BlockState = grid.microLevel.getBlockState(blockPos)
								if (!blockState.isAir) {
									EnchantmentHelper.onHitBlock(
										grid.microLevel as ServerLevel,
										player.mainHandItem,
										player,
										player,
										EquipmentSlot.MAINHAND,
										Vec3.atCenterOf(blockPos),
										blockState
									) { p_348149_: Item? ->
										player.onEquippedItemBroken(
											p_348149_,
											EquipmentSlot.MAINHAND
										)
									}
									if (event.useBlock != TriState.FALSE) blockState.attack(
										grid.microLevel,
										blockPos,
										player
									)
									f = blockState.getDestroyProgress(player, grid.microLevel, blockPos)
								}

								if (!blockState.isAir && f >= 1.0f) {
									destroyAndAck(blockPos, sequence, "insta mine")
								} else {
									if (gameMode.isDestroyingBlock) {
										PacketDistributor.sendToPlayer(
											player,
											EncapsulateBlockUpdatePhysicsGridPacket(
												grid.id,
												ClientboundBlockUpdatePacket(grid.microLevel, gameMode.destroyPos)
											)
										)
//										this.debugLogging(
//											pos,
//											false,
//											sequence,
//											"abort destroying since another started (client insta mine, server disagreed)"
//										)
									}

									gameMode.isDestroyingBlock = true
									gameMode.destroyPos = blockPos.immutable()
									val i = (f * 10.0f).toInt()
									grid.microLevel.destroyBlockProgress(player.id, blockPos, i)
//									this.debugLogging(pos, true, sequence, "actual start of destroying")
									gameMode.lastSentState = i
								}
							}
							ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK -> {
								if (blockPos == gameMode.destroyPos) {
									val j: Int = gameMode.gameTicks - gameMode.destroyProgressStart
									val blockstate1: BlockState = grid.microLevel.getBlockState(blockPos)
									if (!blockstate1.isAir) {
										val f1 = blockstate1.getDestroyProgress(
											player,
											grid.microLevel,
											blockPos
										) * (j + 1).toFloat()
										if (f1 >= 0.7f) {
											gameMode.isDestroyingBlock = false
											grid.microLevel.destroyBlockProgress(player.id, blockPos, -1)
											destroyAndAck(blockPos, sequence, "destroyed")
											return@enqueueWork
										}

										if (!gameMode.hasDelayedDestroy) {
											gameMode.isDestroyingBlock = false
											gameMode.hasDelayedDestroy = true
											gameMode.delayedDestroyPos = blockPos
											gameMode.delayedTickStart = gameMode.destroyProgressStart
										}
									}
								}
//								this.debugLogging(pos, true, sequence, "stopped destroying")
							}
							ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK -> {
								gameMode.isDestroyingBlock = false
								if (gameMode.destroyPos != blockPos) {
//									ServerPlayerGameMode.LOGGER.warn(
//										"Mismatch in destroy block pos: {} {}",
//										this.destroyPos,
//										pos
//									)
									grid.microLevel.destroyBlockProgress(player.id, gameMode.destroyPos, -1)
//									this.debugLogging(pos, true, sequence, "aborted mismatched destroying")
								}

								grid.microLevel.destroyBlockProgress(player.id, blockPos, -1)
//								this.debugLogging(pos, true, sequence, "aborted destroying")
							}
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