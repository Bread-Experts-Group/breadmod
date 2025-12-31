package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import java.util.function.Supplier

// todo remember to adjust bytecode changes in the agent if you modify <init>!
/**
 * The super constructor in this class is replaced at runtime with a no-args constructor via the breadmod agent.
 */
class ServerMicroLevel(
	val blocks: MutableMap<BlockPos, BlockState>,
	val blockEntities: MutableMap<BlockPos, BlockEntity>
) : ServerLevel(null, null, null, null, null, null, null, false, 0, null, true, null) {
	private val logger: Logger = LogManager.getLogger("PhysicsGrid")
	val shapes: MutableList<Pair<BlockPos, VoxelShape>> = this.blocks.map { (pos, state) ->
		pos to state.getShape(this, pos)
	}.toMutableList()

	fun moveShapes(offset: Vec3) {
		repeat(this.shapes.size) { index ->
			val (pos, shape) = this.shapes[index]
			val (x, y, z) = offset/*.add(pos.toVec3())*/
			this.shapes[index] = pos to shape.move(x, y, z)
		}
	}

	override fun setBlock(pos: BlockPos, state: BlockState, flags: Int, recursionLeft: Int): Boolean {
		this.blocks[pos] = state
		this.logger.fatal("nuclear bomb")
		return false
	}

	override fun getProfilerSupplier(): Supplier<ProfilerFiller> = { localClient.profiler }

	override fun getBlockState(pos: BlockPos): BlockState =
		this.blocks[pos] ?: Blocks.AIR.defaultBlockState()

	override fun getBlockEntity(pos: BlockPos): BlockEntity? =
		this.blockEntities[pos]

	override fun getFluidState(pos: BlockPos): FluidState = Fluids.EMPTY.defaultFluidState()

	// todo grid clipping isn't accurate (shapes are in their proper spot according to the renderer), probably because of the limited nature of HitResult
	//  most likely need to use our own ray casting to properly target the grid blocks, maybe a custom HitResult class?
	override fun clip(context: ClipContext): BlockHitResult {
		val result = this.shapes.firstNotNullOfOrNull { (pos, shape) -> shape.clip(context.from, context.to, pos) }
		return result ?: BlockHitResult.miss(context.to, Direction.NORTH, BlockPos.ZERO)
	}

	override fun toString(): String = "ServerMicroLevel[blocks=${this.blocks.size}]"
}