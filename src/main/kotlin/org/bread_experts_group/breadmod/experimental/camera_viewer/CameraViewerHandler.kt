package org.bread_experts_group.breadmod.experimental.camera_viewer

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.DiscardableHandler
import org.bread_experts_group.breadmod.registry.block.handler.ParentedHandler
import org.bread_experts_group.breadmod.util.getBlockPos
import org.bread_experts_group.breadmod.util.putBlockPos

class CameraViewerHandler(
//	var isMaster: Boolean = false
) : ParentedHandler<BreadModBlockEntity>, DiscardableHandler, INBTSerializable<CompoundTag> {
	companion object {
		val BLOCK_VOID: BlockCapability<CameraViewerHandler, Void?> = BlockCapability.createVoid(
			modLocation("camera_state_handler"),
			CameraViewerHandler::class.java
		)
	}

	override lateinit var parent: BreadModBlockEntity
	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
	var boundPos: BlockPos = BlockPos.ZERO
	// todo multiple screens joined together as one big screen
/*	var masterBlock: BlockPos = BlockPos.ZERO
	var width: Int = 1
	var height: Int = 1

	override fun onParentReady() {
		val level = this.parent.level ?: return
		val pos = this.parent.blockPos
		val facing = this.parent.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		val directionsToScan = when (facing) {
			Direction.NORTH -> listOf(Direction.UP, Direction.DOWN)
			else -> listOf()
		}

		directionsToScan.forEach { direction ->
			val relative = pos.relative(direction)
			val neighbour = level.getBlockState(relative)
			if (neighbour.`is`(ModBlocks.CAMERA_VIEWER)) {
				val handler = level.getViewerEntity(relative).getHandler()
				if (handler.isMaster) {
					this.masterBlock = relative
				} else {
					this.masterBlock = pos
					this.isMaster = true
				}
			}
		}
	}*/

//	private fun Level.getViewerEntity(pos: BlockPos): BreadModBlockEntity =
//		this.getBlockEntity(pos) as BreadModBlockEntity
//
//	private fun BreadModBlockEntity.getHandler(): CameraViewerHandler =
//		this.getCapability(CameraViewerHandler.BLOCK_VOID)

	override fun discard() {
		val level = this.parent.level ?: throw NullPointerException("Level should not be null.")
		if (level.isClientSide && this.boundPos != BlockPos.ZERO) CameraTexture.textures.remove(this.boundPos)?.close()
	}

	override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
		val tag = CompoundTag()
		if (this.boundPos != BlockPos.ZERO) tag.putBlockPos("boundPos", this.boundPos)
//		if (this.masterBlock != BlockPos.ZERO) tag.putBlockPos("masterBlock", this.masterBlock)
		return tag
	}

	override fun deserializeNBT(
		provider: HolderLookup.Provider,
		nbt: CompoundTag
	) {
		if (nbt.contains("boundPos")) this.boundPos = nbt.getBlockPos("boundPos")
//		if (nbt.contains("masterBlock")) this.masterBlock = nbt.getBlockPos("masterBlock")
	}
}