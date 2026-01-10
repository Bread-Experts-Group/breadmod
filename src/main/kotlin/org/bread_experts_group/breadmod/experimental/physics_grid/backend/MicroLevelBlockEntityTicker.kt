package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.TickingBlockEntity
import net.minecraft.world.level.chunk.ChunkAccess

class MicroLevelBlockEntityTicker<T : BlockEntity, L : Level>(
	private val parent: L,
	private val access: ChunkAccess,
	private val blockEntity: T,
	private val ticker: BlockEntityTicker<T>
) : TickingBlockEntity {
	override fun tick() {
		if (this.blockEntity.isRemoved || !this.blockEntity.hasLevel()) return
		// TODO: isTicking
		val state = this.access.getBlockState(this.blockEntity.blockPos)
		if (this.blockEntity.type.isValid(state)) {
			this.ticker.tick(this.parent, this.blockEntity.blockPos, state, this.blockEntity)
		}// else {
		// TODO: invalid block state
		//}
	}

	override fun isRemoved(): Boolean = this.blockEntity.isRemoved
	override fun getPos(): BlockPos = this.blockEntity.blockPos
	override fun getType(): String = BlockEntityType.getKey(this.blockEntity.type).toString()
}