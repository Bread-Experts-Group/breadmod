package org.bread_experts_group.breadmod.registry.block.handler

import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.tool_gun.Model

class ModelHandler : DataComponentSerializable, INBTSerializable<ListTag> {
	companion object {
		fun BreadModBlockEntity.getModelHandler(): ModelHandler =
			this.getCapability(this@Companion.BLOCK_VOID) as ModelHandler

		val BLOCK_VOID: BlockCapability<ModelHandler, Void?> = BlockCapability.createVoid(
			modLocation("model_handler"),
			ModelHandler::class.java
		)
	}

	var blocks: List<Model> = listOf()
	override fun serializeDataComponent(map: DataComponentMap.Builder) {
		map.set(ModDataComponents.MODEL_DATA, this.blocks)
	}

	override fun deserializeDataComponent(from: BlockEntity.DataComponentInput) {
		this.blocks = from.getOrDefault(ModDataComponents.MODEL_DATA, listOf())
	}

	override fun serializeNBT(provider: HolderLookup.Provider): ListTag {
		val listTag = ListTag()
		this.blocks.forEach { model ->
			val serialized = Model.CODEC.encodeStart(NbtOps.INSTANCE, model).orThrow
			listTag.add(serialized)
		}
		return listTag
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, nbt: ListTag) {
		this.blocks = nbt.map { Model.CODEC.decode(NbtOps.INSTANCE, it).orThrow.first }.toList()
	}

	override fun collectHoverText(tooltipComponents: MutableList<Component>) {
		tooltipComponents.add(Component.literal("blocks: ${this.blocks.size}"))
	}
}