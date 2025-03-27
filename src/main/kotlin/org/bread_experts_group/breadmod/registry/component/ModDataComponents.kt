package org.bread_experts_group.breadmod.registry.component

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.network.BreadModCodecs.EXPANSIBLE_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.EXPANSIBLE_STREAM_CODEC
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunData
import java.math.BigDecimal
import java.util.function.Supplier

object ModDataComponents {
	val DATA_COMPONENT_REGISTRY: DeferredRegister<DataComponentType<*>> =
		DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, BreadMod.ID)
	val TIME_LEFT: Supplier<DataComponentType<Long>> = this.DATA_COMPONENT_REGISTRY.register(
		"time_left", DataComponentType.builder<Long>()
			.persistent(Codec.LONG)
			.networkSynchronized(ByteBufCodecs.VAR_LONG)
			.cacheEncoding()::build
	)
	val TOOL_GUN_DATA: Supplier<DataComponentType<ToolGunData>> = this.DATA_COMPONENT_REGISTRY.register(
		"current_mode", DataComponentType.builder<ToolGunData>()
			.persistent(BreadModCodecs.TOOL_GUN_CODEC)
			.networkSynchronized(BreadModCodecs.TOOL_GUN_STREAM_CODEC)
			.cacheEncoding()::build
	)
	val EXPANSIBLE_ITEM_STACK: Supplier<DataComponentType<BigDecimal>> = this.DATA_COMPONENT_REGISTRY.register(
		"expansible_item_stack", DataComponentType.builder<BigDecimal>()
			.persistent(EXPANSIBLE_CODEC)
			.networkSynchronized(EXPANSIBLE_STREAM_CODEC)
			.cacheEncoding()::build
	)
}