package org.bread_experts_group.breadmod.registry.component

import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import java.util.function.Supplier

object ModDataComponents {
    val DATA_COMPONENT_REGISTRY: DeferredRegister<DataComponentType<*>> =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Breadmod.ID)

    val TIME_LEFT: Supplier<DataComponentType<Long>> = DATA_COMPONENT_REGISTRY.register("time_left") { ->
        DataComponentType.builder<Long>()
            .persistent(Codec.LONG)
            .networkSynchronized(ByteBufCodecs.VAR_LONG)
            .cacheEncoding()
            .build()
    }
}