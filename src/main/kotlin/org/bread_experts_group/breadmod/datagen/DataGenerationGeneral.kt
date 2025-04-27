package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.Registry
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import org.bread_experts_group.breadmod.registry.block.ModFluids
import java.nio.file.Path

fun Any.getBlock(stage: String): Block = when (this) {
	is Block            -> this
	is DeferredBlock<*> -> this.get()
	is DeferredItem<*>  -> (this.get() as BlockItem).block
	else                -> throw UnsupportedOperationException("$stage: ${this::class.simpleName}")
}

fun Any.getLocation(stage: String): ResourceLocation = when (this) {
	is DeferredHolder<*, *>        -> this.id
	is ModFluids.FluidHolder<*, *> -> this.bucket.id
	is ResourceKey<*>              -> this.location()
	else                           -> throw UnsupportedOperationException("$stage: ${this::class.simpleName}")
}

fun ResourceKey<out Registry<*>>.path(packOutput: PackOutput, path: String): Path = packOutput
	.createRegistryTagsPathProvider(this)
	.json(ResourceLocation.parse(path))