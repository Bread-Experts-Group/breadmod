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
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.ClientHooks
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
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
			this.firstPos = null
			this.secondPos = null
			this.clearFlag = false
			this.blockData = null
			player.sendSystemMessage(Component.literal("data cleared"))
		} else if (this.firstPos != null && this.secondPos != null && !player.isShiftKeyDown && !this.clearFlag) {
			val aabb = AABB(this.firstPos!!.toVec3(), this.secondPos!!.toVec3())
			BlockPos.betweenClosedStream(aabb)
				.forEach {
					val state = level.getBlockState(it)
					if (state.renderShape == RenderShape.INVISIBLE) return@forEach
					val x = it.x - (this.firstPos ?: return@forEach).x
					val y = it.y - (this.firstPos ?: return@forEach).y
					val z = it.z - (this.firstPos ?: return@forEach).z
					val offset = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
					this.blockMap[offset] = BlockData(
						state,
						LevelRenderer.getLightColor(level, it),
						level.getBlockEntity(it)?.let {
							val renderer = localClient.blockEntityRenderDispatcher.getRenderer(it)
							if (renderer != null) BlockEntityData(it, renderer) else null
						},
						buildMap {
							val model = localClient.modelManager.blockModelShaper.getBlockModel(state)
							val shape = FloatArray(Direction.entries.size * 2)
							val shapeFlags = BitSet(3)
							fun calculateForDir(direction: Direction?) {
								val map = mutableMapOf<BakedQuad, AmbientOcclusionFace>()
								model.getQuads(
									state, direction, NullRandom,
									modelData, RenderType.solid()
								).forEach { quad ->
									val face = AmbientOcclusionFace()
									localClient.blockRenderer.modelRenderer.calculateShape(
										level, state, it,
										quad.vertices, quad.direction,
										shape, shapeFlags
									)
									if (
										!ClientHooks.calculateFaceWithoutAO(
											level, state, it, quad, shapeFlags.get(0),
											face.brightness, face.lightmap
										)
									) face.calculate(
										level, state, it, quad.direction, shape, shapeFlags,
										quad.isShade
									)
									map[quad] = face
								}
								this[direction] = map
							}

							for (direction in Direction.entries) {
								val mutable = it.mutable()
								mutable.setWithOffset(it, direction)
								if (Block.shouldRenderFace(state, level, it, direction, mutable))
									calculateForDir(direction)
							}
							calculateForDir(null)
						}
					)
				}
			player.sendSystemMessage(Component.literal("block map created"))
			val center = this.firstPos!!.toVec3().div(2.0) - this.secondPos!!.toVec3().div(2.0)
			this.blockData = BulkBlockData(this.blockMap, center, level)
			this.clearFlag = true
		}
		return InteractionResultHolder.sidedSuccess(getStackInPlayerHand(player), level.isClientSide)
	}

	override fun onMouseInputPost(mouseEvent: Post, heldStack: ItemStack, player: Player) {
		if (mouseEvent.action == InputConstants.PRESS) {
			if (mouseEvent.button == InputConstants.MOUSE_BUTTON_MIDDLE) {
				BulkBlockBufferTask.create(player.position(), this.blockData ?: return)
				player.sendSystemMessage(Component.literal("renderer created (${this.blockData!!.blocks.size})"))
			}
		}
	}

	data class BlockData(
		val state: BlockState,
		val packedLight: Int,
		val entity: BlockEntityData<BlockEntity>?,
		val ao: Map<Direction?, Map<BakedQuad, AmbientOcclusionFace>>
	)

	data class BlockEntityData<T : BlockEntity>(
		val entity: T,
		val renderer: BlockEntityRenderer<T>
	)

	data class BulkBlockData(
		val blocks: Map<Vec3, BlockData>,
		val aabbCenter: Vec3,
		val level: Level
	)
}