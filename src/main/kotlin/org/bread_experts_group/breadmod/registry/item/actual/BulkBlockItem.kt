package org.bread_experts_group.breadmod.registry.item.actual

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.ModelBlockRenderer.AmbientOcclusionFace
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.ClientHooks
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask.NullRandom
import org.bread_experts_group.breadmod.client.render.buffer.render.BulkBlockBufferTask.modelData
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import java.util.BitSet

class BulkBlockItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IMouseItem {
	private var firstPos: BlockPos? = null
	private var secondPos: BlockPos? = null
	private val blockMap: MutableMap<Vec3, BlockData> = mutableMapOf()
	private val fluidMap: MutableMap<Vec3, FluidData> = mutableMapOf()
	private var clearFlag = false

	companion object {
		var blockData: BulkBlockData? = null
	}

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
			this.fluidMap.clear()
			this.firstPos = null
			this.secondPos = null
			this.clearFlag = false
			Companion.blockData = null
			player.sendSystemMessage(Component.literal("data cleared"))
		} else if (this.firstPos != null && this.secondPos != null && !player.isShiftKeyDown && !this.clearFlag) {
			val first = this.firstPos!!
			val aabb = AABB(first.toVec3(), this.secondPos!!.toVec3())
			BlockPos.betweenClosedStream(aabb).forEach { blockPos ->
				val state = level.getBlockState(blockPos)
				val offset = (blockPos - first).toVec3()
				if (state.renderShape == RenderShape.INVISIBLE) {
					if (!state.fluidState.isEmpty)
						this.fluidMap[offset] = FluidData(
							state.fluidState,
							state,
							IClientFluidTypeExtensions.of(state.fluidState)
						)
					return@forEach
				}
				this.blockMap[offset] = BlockData(
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
			player.sendSystemMessage(Component.literal("block map created"))
			val center = this.firstPos!!.toVec3().div(2.0) - this.secondPos!!.toVec3().div(2.0)
			Companion.blockData = BulkBlockData(this.blockMap, this.fluidMap, center, level)
			this.clearFlag = true
		}
		return InteractionResultHolder.sidedSuccess(getStackInPlayerHand(player), level.isClientSide)
	}

	override fun onMouseInputPost(mouseEvent: Post, heldStack: ItemStack, player: Player) {
		if (mouseEvent.action == InputConstants.PRESS) {
			if (mouseEvent.button == InputConstants.MOUSE_BUTTON_MIDDLE) {
				BulkBlockBufferTask.create(player.position(), Companion.blockData ?: return)
				player.sendSystemMessage(Component.literal("renderer created (${Companion.blockData!!.blocks.size})"))
			}
		}
	}

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
}