package org.bread_experts_group.breadmod.registry.component

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.data_holders.common.MachSpeedData
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.experimental.particle.ClosedSystem
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DECIMAL_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DECIMAL_STREAM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.CLOSED_SYSTEM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.CLOSED_SYSTEM_STREAM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.TOOL_GUN_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.TOOL_GUN_STREAM_CODEC
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.item.coffee.CoffeeContents
import org.bread_experts_group.breadmod.util.toList
import java.math.BigDecimal
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
			.persistent(TOOL_GUN_CODEC)
			.networkSynchronized(TOOL_GUN_STREAM_CODEC)
			.cacheEncoding()::build
	)
	val CLOSED_SYSTEM: Supplier<DataComponentType<ClosedSystem>> = this.registry.register(
		"closed_system", DataComponentType.builder<ClosedSystem>()
			.persistent(CLOSED_SYSTEM_CODEC)
			.networkSynchronized(CLOSED_SYSTEM_STREAM_CODEC)
			.cacheEncoding()::build
	)
	val MACH_SPEED: Supplier<DataComponentType<MachSpeedData>> = this.registry.register(
		"mach_speed", DataComponentType.builder<MachSpeedData>()
			.networkSynchronized(MachSpeedData.STREAM_CODEC)
			.cacheEncoding()::build
	)
	val COLOR: Supplier<DataComponentType<Int>> = this.registry.register(
		"color", DataComponentType.builder<Int>()
			.networkSynchronized(ByteBufCodecs.INT)
		::build
	)
	val ENERGY: Supplier<DataComponentType<BigDecimal>> = this.registry.register(
		"energy", DataComponentType.builder<BigDecimal>()
			.networkSynchronized(BIG_DECIMAL_STREAM_CODEC)
			.persistent(BIG_DECIMAL_CODEC)
		::build
	)
	val ENERGY_CAPACITY: Supplier<DataComponentType<BigDecimal>> = this.registry.register(
		"energy_capacity", DataComponentType.builder<BigDecimal>()
			.networkSynchronized(BIG_DECIMAL_STREAM_CODEC)
			.persistent(BIG_DECIMAL_CODEC)
		::build
	)
	val TANKS: Supplier<DataComponentType<List<ExtendedFluidHandler.Tank>>> = this.registry.register(
		"tanks", DataComponentType.builder<List<ExtendedFluidHandler.Tank>>()
			.networkSynchronized(ExtendedFluidHandler.Tank.STREAM_CODEC.toList())
			.persistent(ExtendedFluidHandler.Tank.CODEC.listOf())
		::build
	)
	val COFFEE_CONTENTS: Supplier<DataComponentType<CoffeeContents>> = this.registry.register(
		"coffee_contents", DataComponentType.builder<CoffeeContents>()
			.networkSynchronized(CoffeeContents.STREAM_CODEC)
			.persistent(CoffeeContents.CODEC)
			.cacheEncoding()::build
	)
	val BLOCK_ENTITY_HANDLER_INFORMATION: Supplier<DataComponentType<List<Component>>> = this.registry.register(
		"beg_be_handler_info", DataComponentType.builder<List<Component>>()
			.networkSynchronized(ComponentSerialization.STREAM_CODEC.toList())
			.persistent(ComponentSerialization.CODEC.listOf())
			.cacheEncoding()::build
	)
}