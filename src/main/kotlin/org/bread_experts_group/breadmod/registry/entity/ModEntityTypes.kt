package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.entity.actual.FakePlayer
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedHappyBlock
import java.util.function.Supplier

object ModEntityTypes {
    val ENTITY_REGISTRY: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BreadMod.ID)

    val HAPPY_BLOCK_ENTITY: Supplier<EntityType<PrimedHappyBlock>> = ENTITY_REGISTRY.register("happy_block") { ->
        EntityType.Builder.of({ _, level -> PrimedHappyBlock(level, shouldSpread = true) }, MobCategory.MISC)
            .sized(0.98f, 0.98f)
            .clientTrackingRange(10)
            .updateInterval(10)
            .build(modLocation("happy_block").toString())
    }

    val FAKE_PLAYER: Supplier<EntityType<FakePlayer>> = ENTITY_REGISTRY.register("fake_player") { ->
        EntityType.Builder.of({ type, level -> FakePlayer(type, level) }, MobCategory.MISC)
            .sized(0.6f, 1.8f)
            .eyeHeight(1.62f)
            .vehicleAttachment(Player.DEFAULT_VEHICLE_ATTACHMENT)
            .clientTrackingRange(32)
            .updateInterval(2)
            .build(modLocation("fake_player").toString())
    }
}