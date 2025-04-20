package org.bread_experts_group.breadmod.network.clientbound

import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.ModelBlockRenderer.AmbientOcclusionFace
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.ClientHooks
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask.NullRandom
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask.modelData
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import java.util.BitSet

class PhysicsGridPacket private constructor(
	val level: String,
	val grid: Map<BlockPos, BlockState>,
	val position: Vector3f,
	val from: BlockPos
) : CustomPacketPayload {
	constructor(level: Level, grid: Map<BlockPos, BlockState>, position: Vector3f, from: BlockPos) : this(
		level.dimension().location().toString(), grid, position, from
	)

	companion object {
		data class BlockData(
			val state: BlockState,
			val packedLight: Int,
			val entity: BlockEntityData<BlockEntity>?,
			val ao: Map<Direction?, Map<RenderType, Map<BakedQuad, AmbientOcclusionFace>>>
		)

		data class FluidData(
			val fluidState: FluidState,
			val state: BlockState,
			val extensions: IClientFluidTypeExtensions
		)

		data class BlockEntityData<T : BlockEntity>(
			val entity: T,
			val renderer: BlockEntityRenderer<T>
		)

		data class BulkBlockData(
			val blocks: Map<Vec3, BlockData>,
			val fluids: Map<Vec3, FluidData>,
			val aabbCenter: Vec3,
			val level: Level
		)

		val TYPE: CustomPacketPayload.Type<PhysicsGridPacket> =
			CustomPacketPayload.Type(BreadMod.Companion.modLocation("physics_grid"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PhysicsGridPacket> = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, PhysicsGridPacket::level,
			BreadModCodecs.BLOCK_MAP_STREAM_CODEC, PhysicsGridPacket::grid,
			ByteBufCodecs.VECTOR3F, PhysicsGridPacket::position,
			BlockPos.STREAM_CODEC, PhysicsGridPacket::from,
			::PhysicsGridPacket
		)

		fun handleClientboundPacket(data: PhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				context.player().sendSystemMessage(
					Component.literal("Physics grid at ${data.position}, datum #: ${data.grid.size}")
				)
				BulkBlockBufferTask.create(
					data.position.toVec3(),
					BulkBlockData(
						this.computeFakeBlockMap(
							context.player().level(),
							data.from,
							data.grid
						),
						emptyMap(),
						context.player().position(),
						context.player().level()
					)
				)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)

		fun computeFakeBlockMap(
			level: Level,
			from: BlockPos,
			grid: Map<BlockPos, BlockState>
		): Map<Vec3, BlockData> = buildMap {
			grid.forEach { (blockPos, state) ->
				val offset = blockPos.offset(-from)
				this[offset.toVec3()] = BlockData(
					state,
					LevelRenderer.getLightColor(level, blockPos),
					level.getBlockEntity(blockPos)?.let { entity ->
						val renderer = localClient.blockEntityRenderDispatcher.getRenderer(entity)
						if (renderer != null) BlockEntityData(entity, renderer) else null
					},
					buildMap {
						val model = localClient.modelManager.blockModelShaper.getBlockModel(state)
						val shape = FloatArray(Direction.entries.size * 2)
						val shapeFlags = BitSet(3)
						fun calculateForDir(direction: Direction?) {
							val map = mutableMapOf<RenderType, MutableMap<BakedQuad, AmbientOcclusionFace>>()
							model.getRenderTypes(state, NullRandom, modelData).forEach { type ->
								val typeMap = mutableMapOf<BakedQuad, AmbientOcclusionFace>()
								model.getQuads(
									state, direction, NullRandom,
									modelData, type
								).forEach { quad ->
									val face = AmbientOcclusionFace()
									localClient.blockRenderer.modelRenderer.calculateShape(
										level, state, blockPos,
										quad.vertices, quad.direction,
										shape, shapeFlags
									)
									if (
										!ClientHooks.calculateFaceWithoutAO(
											level, state, blockPos, quad, shapeFlags.get(0),
											face.brightness, face.lightmap
										)
									) face.calculate(
										level, state, blockPos, quad.direction, shape, shapeFlags,
										quad.isShade
									)
									typeMap[quad] = face
								}
								map[type] = typeMap
							}
							this[direction] = map
						}

						for (direction in Direction.entries) {
							val mutable = blockPos.mutable()
							mutable.setWithOffset(blockPos, direction)
							if (Block.shouldRenderFace(state, level, blockPos, direction, mutable))
								calculateForDir(direction)
						}
						calculateForDir(null)
					}
				)
			}
		}
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}