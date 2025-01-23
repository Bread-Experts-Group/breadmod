package org.bread_experts_group.breadmod.registry.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.api.IToolGunMode
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
	val TOOL_GUN_DATA: Supplier<DataComponentType<IToolGunMode>> = this.DATA_COMPONENT_REGISTRY.register(
		"current_mode", DataComponentType.builder<IToolGunMode>()
			.persistent(IToolGunMode.CODEC)
			.networkSynchronized(IToolGunMode.STREAM_CODEC)
			.cacheEncoding()::build
	)
	val EXPANSIBLE_CODEC: Codec<BigDecimal> =
		RecordCodecBuilder.create<BigDecimal> { instance: RecordCodecBuilder.Instance<BigDecimal> ->
			instance.group(
				Codec.STRING.fieldOf("value").forGetter(BigDecimal::toEngineeringString)
			).apply(instance) { str -> BigDecimal(str) }
		}
	val EXPANSIBLE_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BigDecimal> = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, BigDecimal::toEngineeringString
	) { BigDecimal(it) }
	val EXPANSIBLE_ITEM_STACK: Supplier<DataComponentType<BigDecimal>> = this.DATA_COMPONENT_REGISTRY.register(
		"expansible_item_stack", DataComponentType.builder<BigDecimal>()
			.persistent(this.EXPANSIBLE_CODEC)
			.networkSynchronized(this.EXPANSIBLE_STREAM_CODEC)
			.cacheEncoding()::build
	)
}