package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.network.BreadModCodecs.BLOCK_MAP_STREAM_CODEC

@Suppress("unused")
object ModEntityDataSerializers {
	val ENTITY_DATA_SERIALIZER_REGISTRY: DeferredRegister<EntityDataSerializer<*>> =
		DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, BreadMod.ID)
	val BLOCK_MAP: DeferredHolder<EntityDataSerializer<*>, EntityDataSerializer<Map<BlockPos, BlockState>>> =
		this.ENTITY_DATA_SERIALIZER_REGISTRY.register("block_map") { ->
			EntityDataSerializer.forValueType(BLOCK_MAP_STREAM_CODEC)
		}
}