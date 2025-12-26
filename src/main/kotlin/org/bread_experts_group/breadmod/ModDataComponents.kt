package org.bread_experts_group.breadmod

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.crafting.RecipeHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.data_holders.common.MachSpeedData
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.experimental.particle.ClosedSystem
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedItemHandler
import org.bread_experts_group.breadmod.tool_gun.Model
import org.bread_experts_group.breadmod.util.listOf
import java.math.BigDecimal
import java.util.UUID
import java.util.function.Supplier

object ModDataComponents : RegistryProvider(Registries.DATA_COMPONENT_TYPE) {
	private val registry: DeferredRegister<DataComponentType<*>> = this.getRegistry(Registries.DATA_COMPONENT_TYPE)
	val TIME_LEFT: Supplier<DataComponentType<Long>> = this.registry.register(
		"time_left", DataComponentType.builder<Long>()
			.persistent(Codec.LONG)
			.networkSynchronized(ByteBufCodecs.VAR_LONG)
			.cacheEncoding()::build
	)
	val TOOL_GUN_DATA: Supplier<DataComponentType<ToolGunData>> = this.registry.register(
		"current_mode", DataComponentType.builder<ToolGunData>()
			.persistent(BreadModCodecs.TOOL_GUN_CODEC)
			.networkSynchronized(BreadModCodecs.TOOL_GUN_STREAM_CODEC)
			.cacheEncoding()::build
	)
	val CLOSED_SYSTEM: Supplier<DataComponentType<ClosedSystem>> = this.registry.register(
		"closed_system", DataComponentType.builder<ClosedSystem>()
			.persistent(BreadModCodecs.CLOSED_SYSTEM_CODEC)
			.networkSynchronized(BreadModCodecs.CLOSED_SYSTEM_STREAM_CODEC)
			.cacheEncoding()::build
	)
	val MACH_SPEED: Supplier<DataComponentType<MachSpeedData>> = this.registry.register(
		"mach_speed", DataComponentType.builder<MachSpeedData>()
			.networkSynchronized(MachSpeedData.Companion.STREAM_CODEC)
			.cacheEncoding()::build
	)
	val MODEL_DATA: Supplier<DataComponentType<List<Model>>> = this.registry.register(
		"model_data", DataComponentType.builder<List<Model>>()
			.persistent(Model.CODEC.listOf())
			.networkSynchronized(Model.STREAM_CODEC.listOf())
		::build
	)
	val COLOR: Supplier<DataComponentType<Int>> = this.registry.register(
		"color", DataComponentType.builder<Int>()
			.networkSynchronized(ByteBufCodecs.INT)
			.persistent(Codec.INT)
		::build
	)
	val ENERGY: Supplier<DataComponentType<BigDecimal>> = this.registry.register(
		"energy", DataComponentType.builder<BigDecimal>()
			.networkSynchronized(BreadModCodecs.BIG_DECIMAL_STREAM_CODEC)
			.persistent(BreadModCodecs.BIG_DECIMAL_CODEC)
		::build
	)
	val ENERGY_CAPACITY: Supplier<DataComponentType<BigDecimal>> = this.registry.register(
		"energy_capacity", DataComponentType.builder<BigDecimal>()
			.networkSynchronized(BreadModCodecs.BIG_DECIMAL_STREAM_CODEC)
			.persistent(BreadModCodecs.BIG_DECIMAL_CODEC)
		::build
	)
	val TANKS: Supplier<DataComponentType<List<ExtendedFluidHandler.Tank>>> = this.registry.register(
		"tanks", DataComponentType.builder<List<ExtendedFluidHandler.Tank>>()
			.networkSynchronized(ExtendedFluidHandler.Tank.STREAM_CODEC.listOf())
			.persistent(ExtendedFluidHandler.Tank.CODEC.listOf())
		::build
	)
	val SLOTS: Supplier<DataComponentType<List<ExtendedItemHandler.Slot>>> = this.registry.register(
		"slots", DataComponentType.builder<List<ExtendedItemHandler.Slot>>()
			.networkSynchronized(ExtendedItemHandler.Slot.STREAM_CODEC.listOf())
			.persistent(ExtendedItemHandler.Slot.CODEC.listOf())
		::build
	)
	val RECIPE: Supplier<DataComponentType<RecipeHolder<*>>> = this.registry.register(
		"recipe", DataComponentType.builder<RecipeHolder<*>>()
			.networkSynchronized(RecipeHolder.STREAM_CODEC)
			.persistent(BreadModCodecs.RECIPE_HOLDER_CODEC)
		::build
	)
	val RECIPE_PROGRESS: Supplier<DataComponentType<Long>> = this.registry.register(
		"recipe_progress", DataComponentType.builder<Long>()
			.persistent(Codec.LONG)
			.networkSynchronized(ByteBufCodecs.VAR_LONG)
		::build
	)
	val BLOCK_ENTITY_HANDLER_INFORMATION: Supplier<DataComponentType<List<Component>>> = this.registry.register(
		"beg_be_handler_info", DataComponentType.builder<List<Component>>()
			.networkSynchronized(ComponentSerialization.STREAM_CODEC.listOf())
			.persistent(ComponentSerialization.CODEC.listOf())
			.cacheEncoding()::build
	)
	val BLOCK_POS: Supplier<DataComponentType<BlockPos>> = this.registry.register(
		"block_pos", DataComponentType.builder<BlockPos>()
			.networkSynchronized(BlockPos.STREAM_CODEC)::build
	)
	val UUID: Supplier<DataComponentType<UUID>> = this.registry.register(
		"uuid", DataComponentType.builder<UUID>()
			.networkSynchronized(UUIDUtil.STREAM_CODEC)::build
	)
}