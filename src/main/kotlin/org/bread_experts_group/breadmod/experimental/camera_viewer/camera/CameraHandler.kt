package org.bread_experts_group.breadmod.experimental.camera_viewer.camera

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.neoforged.neoforge.capabilities.BaseCapability
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraTexture
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraViewerHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.DiscardableHandler
import org.bread_experts_group.breadmod.registry.block.handler.ParentedHandler

class CameraHandler : ParentedHandler<BreadModBlockEntity>, INBTSerializable<ListTag>, DiscardableHandler {
	companion object {
		val BLOCK_VOID: BaseCapability<CameraHandler, Void?> =
			BlockCapability.createVoid(modLocation("camera_handler"), CameraHandler::class.java)
	}

	override lateinit var parent: BreadModBlockEntity
	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
	var boundViewers: MutableList<BlockPos> = mutableListOf()

	override fun serializeNBT(provider: HolderLookup.Provider): ListTag {
		val tag = ListTag()
		if (this.boundViewers.isNotEmpty()) {
			this.boundViewers.forEach { tag.add(BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, it).orThrow) }
		}
		return tag
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, nbt: ListTag) {
		this.boundViewers = nbt.map { BlockPos.CODEC.decode(NbtOps.INSTANCE, it).orThrow.first }.toMutableList()
	}

	override fun discard() {
		val level = this.parent.level ?: return
		if (level.isClientSide) {
			this.boundViewers.forEach { pos ->
				val entity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return@forEach
				val handler = entity.getCapability(CameraViewerHandler.BLOCK_VOID)
				CameraTexture.textures.remove(pos)?.close()
				handler.boundPos = BlockPos.ZERO
				entity.setChanged()
			}
		}
	}
}