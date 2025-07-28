package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.bread_experts_group.breadmod.network.BreadModCodecs.BLOCK_MAP_STREAM_CODEC
import org.bread_experts_group.breadmod.registry.RegistryProvider

@Suppress("unused")
object ModEntityDataSerializers : RegistryProvider(
	NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS
) {
	private val registry: DeferredRegister<EntityDataSerializer<*>> = this.getRegistry(
		NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS
	)
	val BLOCK_MAP: DeferredHolder<EntityDataSerializer<*>, EntityDataSerializer<Map<BlockPos, BlockState>>> =
		this.registry.register("block_map") { ->
			EntityDataSerializer.forValueType(BLOCK_MAP_STREAM_CODEC)
		}
}