package org.bread_experts_group.breadmod.registry.item.actual

import com.google.common.base.Objects
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexBuffer
import io.netty.buffer.ByteBuf
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.div
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.times
import org.bread_experts_group.breadmod.util.toVec3

class BulkBlockItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IMouseItem {
	private var posA: BlockPos = BlockPos.ZERO
	private var posB: BlockPos = BlockPos.ZERO

	override fun useOn(context: UseOnContext): InteractionResult {
		if (context.clickedPos is BlockPos.MutableBlockPos) return super.useOn(context)
		if (this.posA == BlockPos.ZERO) {
			this.posA = context.clickedPos
			context.player?.sendSystemMessage(Component.literal("A = ${this.posA}"))
			return InteractionResult.sidedSuccess(context.level.isClientSide)
		}
		if (this.posB == BlockPos.ZERO) {
			this.posB = context.clickedPos
			context.player?.sendSystemMessage(Component.literal("B = ${this.posB}"))
			return InteractionResult.sidedSuccess(context.level.isClientSide)
		}
		if (this.posA != BlockPos.ZERO && this.posB != BlockPos.ZERO) {
//			this.addThingFinderRender(context)
			PhysicsGrid.add(this.posA, this.posB, context, context.level)
		}
		this.posA = BlockPos.ZERO
		this.posB = BlockPos.ZERO
		return super.useOn(context)
	}

	override fun onMouseScroll(scrollingEvent: InputEvent.MouseScrollingEvent, heldStack: ItemStack, player: Player) {
		if (player.isCrouching) {
			Registry.grids.clear()
			PacketDistributor.sendToServer(ClearGridPacket())
			scrollingEvent.isCanceled = true
		}
	}

	fun addThingFinderRender(context: UseOnContext) {
		val list = mutableListOf<BlockPos>()
		BlockPos.betweenClosedStream(this.posA, this.posB).forEach { pos ->
			list.add(pos.immutable())
		}
		logDebugInfo(list.size)
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, pass ->
			val flag = pass[0] as Boolean
			if (!flag) {
				ThingFinder.oreBlocksList = list
				logDebugInfo(ThingFinder.oreBlocksList.size)
				logDebugInfo("generating VBO")
				ThingFinder.generateVBO(context.player ?: return@add true)
				pass[0] = true
			}
			ThingFinder.render(event, localClient.player!!)
			false
		}, mutableListOf(false))
	}

	class ClearGridPacket : CustomPacketPayload {
		companion object {
			private val TYPE: CustomPacketPayload.Type<ClearGridPacket> =
				CustomPacketPayload.Type(modLocation("clear_grid"))
			val STREAM_CODEC: StreamCodec<ByteBuf, ClearGridPacket> = StreamCodec.unit(ClearGridPacket())

			fun handleServerbound(context: IPayloadContext) {
				context.enqueueWork {
					Registry.grids.clear()
				}
			}

			fun register(registrar: PayloadRegistrar): PayloadRegistrar =
				registrar.playToServer(this.TYPE, this.STREAM_CODEC) { _, context -> this.handleServerbound(context) }
		}

		override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> = Companion.TYPE

		override fun equals(other: Any?): Boolean = other is ClearGridPacket

		override fun hashCode(): Int = Objects.hashCode(Companion.TYPE, Companion.STREAM_CODEC)
	}

	class PhysicsGrid(
		val level: Level,
		val blocks: Map<BlockPos, BlockState>,
		val pos: Vec3,
		val center: Vec3,
		val bounding: AABB
	) {
		companion object {
			fun add(posA: BlockPos, posB: BlockPos, context: UseOnContext, level: Level) {
				val targetPos = context.clickedPos.relative(context.clickedFace).toVec3()
				val a = posA
				val b = posB
				val blocks: MutableMap<BlockPos, BlockState> = mutableMapOf()
				val center = ((a.center / 2.0) - (b.center / 2.0)).minus(0.5, 0.5, 0.5)
				val bounding = AABB(
					0.0,
					0.0,
					0.0,
					a.x - b.x - 1.0,
					a.y - b.y - 1.0,
					a.z - b.z - 1.0
				).move(targetPos.minus(center.times(2.0)))
				val centerOffset = a.toVec3() + center
				logDebugInfo(bounding)
				BlockPos.betweenClosedStream(a, b).forEach { pos ->
					val immutable = pos.immutable()
					val state = level.getBlockState(immutable)
					if (state.isAir) return@forEach
					blocks[BlockPos(immutable.x - a.x, immutable.y - a.y, immutable.z - a.z)] = state
				}
				Registry.grids.add(PhysicsGrid(level, blocks, targetPos, center, bounding))
			}
		}

		val random: RandomSource = RandomSource.create(42)

		fun addNew() {
			RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
				VertexThing.generate(event, this.blocks)
				if (VertexThing.vertexBuffer.format == null) return@add true
				val poseStack = event.poseStack
				val shaderInstance = RenderSystem.getShader() ?: return@add true
				val (x, y, z) = event.camera.position
				poseStack.pushPose()
				poseStack.mulPose(event.modelViewMatrix)
				poseStack.translate(-x, -y, -z)
				poseStack.translate(this.pos)
				VertexThing.vertexBuffer.bind()
				VertexThing.vertexBuffer.drawWithShader(
					poseStack.last().pose(),
					event.projectionMatrix,
					shaderInstance
				)
				shaderInstance.clear()
				VertexBuffer.unbind()
				poseStack.popPose()
				if (!Registry.grids.contains(this)) {
					VertexThing.generated = false
					VertexThing.meshData!!.close()
					true
				} else false
			})
		}

		fun addLegacy() {
			RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
				val poseStack = event.poseStack
				val blockRenderer = localClient.blockRenderer
				val buffer = localClient.renderBuffers().bufferSource()

				poseStack.pushPose()
				poseStack.initialTranslate(event.camera)
				LevelRenderer.renderLineBox(
					poseStack,
					buffer.getBuffer(RenderType.lines()),
					this.bounding,
					1f,
					1f,
					1f,
					1f
				)
				poseStack.translate(this.pos)
				this.blocks.forEach { (offset, state) ->
					poseStack.pushPose()
					poseStack.translate(offset)
					blockRenderer.renderSingleBlock(
						state,
						poseStack,
						buffer,
						LightTexture.FULL_BRIGHT,
						OverlayTexture.NO_OVERLAY
					)
					poseStack.popPose()
				}
				poseStack.popPose()
				!Registry.grids.contains(this)
			})
		}

		init {
			logDebugInfo("blocks: ${this.blocks.size}")
			this.addNew()
		}
	}
}