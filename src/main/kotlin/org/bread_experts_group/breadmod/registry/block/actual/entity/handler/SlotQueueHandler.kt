package org.bread_experts_group.breadmod.registry.block.actual.entity.handler

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.ListTag
import net.minecraft.world.Containers
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class SlotQueueHandler : ParentedHandler<BreadModBlockEntity>, INBTSerializable<ListTag>, Iterable<ItemStack> {
	companion object {
		val BLOCK_VOID: BlockCapability<SlotQueueHandler, Void?> = BlockCapability.createVoid<SlotQueueHandler>(
			modLocation("slot_queue_handler"),
			SlotQueueHandler::class.java
		)
	}

	private fun VoxelShape.scale(n: Double): VoxelShape {
		val aabbs = this.toAabbs()
		var shape = Shapes.empty()
		for (aabb in aabbs) shape = Shapes.joinUnoptimized(
			shape,
			Shapes.create(
				aabb.minX * n,
				aabb.minY * n,
				aabb.minZ * n,
				aabb.maxX * n,
				aabb.maxY * n,
				aabb.maxZ * n
			),
			BooleanOp.OR
		)
		return shape
	}

	override lateinit var parent: BreadModBlockEntity
	private val contained: ArrayDeque<ItemStack> = ArrayDeque()
	val size: Int
		get() = this.contained.size
	var shape: VoxelShape = Shapes.empty()
		private set

	private fun updateShape() {
		val level = this.parent.level ?: return
		var newShape = Shapes.empty()
		for ((index, stack) in this.contained.iterator().withIndex()) {
			var localShape = stack.item.let { item ->
				if (item is BlockItem) item.block.defaultBlockState().getShape(
					level,
					this.parent.blockPos
				)
				else Shapes.block()
			}.scale(0.5)
			localShape = when (this.contained.size) {
				1 -> localShape.move(0.25, 0.0, 0.25)
				2 -> when (index) {
					0 -> localShape.move(0.0, 0.0, 0.25)
					1 -> localShape.move(0.5, 0.0, 0.25)
					else -> throw IllegalStateException(index.toString())
				}
				3 -> when (index) {
					0 -> localShape.move(0.0, 0.0, 0.0)
					1 -> localShape.move(0.5, 0.0, 0.0)
					2 -> localShape.move(0.25, 0.0, 0.5)
					else -> throw IllegalStateException(index.toString())
				}
				4 -> when (index) {
					0 -> localShape.move(0.0, 0.0, 0.0)
					1 -> localShape.move(0.5, 0.0, 0.0)
					2 -> localShape.move(0.0, 0.0, 0.5)
					3 -> localShape.move(0.5, 0.0, 0.5)
					else -> throw IllegalStateException(index.toString())
				}
				else -> throw IllegalStateException(this.contained.size.toString())
			}
			newShape = Shapes.join(newShape, localShape, BooleanOp.OR)
		}
		this.shape = newShape
	}

	override fun iterator(): Iterator<ItemStack> = this.contained.iterator()
	operator fun get(index: Int): ItemStack = this.contained[index]

	fun append(stack: ItemStack) {
		this.contained.add(stack)
		this.updateShape()
		this.stateUpdated()
	}

	fun dropContents(pos: BlockPos, level: Level) {
		Containers.dropContents(level, pos, NonNullList.copyOf(this.contained))
		this.shape = Shapes.empty()
	}

	override fun serializeNBT(provider: HolderLookup.Provider): ListTag = ListTag().also { tag ->
		this.contained.forEach { tag.add(it.save(provider)) }
	}

	override fun deserializeNBT(
		provider: HolderLookup.Provider,
		nbt: ListTag
	) {
		this.contained.clear()
		nbt.forEach {
			ItemStack.parse(provider, it).ifPresent { stack -> this.contained.add(stack) }
		}
		this.updateShape()
		this.stateUpdated()
	}

	override fun toString(): String = "SlotQueueHandler[${this.contained}]"
}