package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.entity.actual.FakePlayer
import org.bread_experts_group.breadmod.registry.entity.actual.Forklift
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedHappyBlock
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedNukeBlock
import org.bread_experts_group.breadmod.registry.entity.actual.Rocket
import java.util.function.Supplier

object ModEntityTypes {
	val ENTITY_REGISTRY: DeferredRegister<EntityType<*>> =
		DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BreadMod.ID)

	@DataGenerateLanguage("en_us", "Happy Block")
	val HAPPY_BLOCK_ENTITY: Supplier<EntityType<PrimedHappyBlock>> = this.ENTITY_REGISTRY.register("happy_block") { ->
		EntityType.Builder.of({ _, level -> PrimedHappyBlock(level, shouldSpread = true) }, MobCategory.MISC)
			.sized(0.98f, 0.98f)
			.clientTrackingRange(10)
			.updateInterval(10)
			.build(modLocation("happy_block").toString())
	}

	@DataGenerateLanguage("en_us", "Nuke Block")
	val NUKE_BLOCK_ENTITY: Supplier<EntityType<PrimedNukeBlock>> = this.ENTITY_REGISTRY.register("nuke_block") { ->
		EntityType.Builder.of({ _, level -> PrimedNukeBlock(level) }, MobCategory.MISC)
			.sized(0.98f, 0.98f)
			.clientTrackingRange(10)
			.updateInterval(10)
			.build(modLocation("nuke_block").toString())
	}

	@DataGenerateLanguage("en_us")
	val FAKE_PLAYER: Supplier<EntityType<FakePlayer>> = this.ENTITY_REGISTRY.register("fake_player") { ->
		EntityType.Builder.of(::FakePlayer, MobCategory.MISC)
			.sized(0.6f, 1.8f)
			.eyeHeight(1.62f)
			.clientTrackingRange(32)
			.updateInterval(2)
			.build(modLocation("fake_player").toString())
	}

	@DataGenerateLanguage("en_us")
	val FORKLIFT: Supplier<EntityType<Forklift>> = this.ENTITY_REGISTRY.register("forklift") { ->
		EntityType.Builder.of(::Forklift, MobCategory.MISC)
			.sized(1.5f, 2f)
			.eyeHeight(2f)
			.clientTrackingRange(10)
			.build(modLocation("forklift").toString())
	}

	@DataGenerateLanguage("en_us")
	val ROCKET: Supplier<EntityType<Rocket>> = this.ENTITY_REGISTRY.register("rocket") { ->
		EntityType.Builder.of(::Rocket, MobCategory.MISC)
			.clientTrackingRange(20)
			.build(modLocation("rocket").toString())
	}
}