package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.entity.actual.FakePlayer
import org.bread_experts_group.breadmod.registry.entity.actual.Forklift
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedHappyBlock
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedNukeBlock
import java.util.function.Supplier

object ModEntityTypes : RegistryProvider(Registries.ENTITY_TYPE) {
	private val registry: DeferredRegister<EntityType<*>> = this.getRegistry(Registries.ENTITY_TYPE)

	@DataGenerateLanguage(name = "Happy Block")
	val HAPPY_BLOCK_ENTITY: Supplier<EntityType<PrimedHappyBlock>> = this.registry.register("happy_block") { ->
		EntityType.Builder.of({ _, level -> PrimedHappyBlock(level, shouldSpread = true) }, MobCategory.MISC)
			.sized(0.98f, 0.98f)
			.clientTrackingRange(10)
			.updateInterval(10)
			.build(modLocation("happy_block").toString())
	}

	@DataGenerateLanguage(name = "Nuke Block")
	val NUKE_BLOCK_ENTITY: Supplier<EntityType<PrimedNukeBlock>> = this.registry.register("nuke_block") { ->
		EntityType.Builder.of({ _, level -> PrimedNukeBlock(level) }, MobCategory.MISC)
			.sized(0.98f, 0.98f)
			.clientTrackingRange(10)
			.updateInterval(10)
			.build(modLocation("nuke_block").toString())
	}

	@DataGenerateLanguage
	val FAKE_PLAYER: Supplier<EntityType<FakePlayer>> = this.registry.register("fake_player") { ->
		EntityType.Builder.of(::FakePlayer, MobCategory.MISC)
			.sized(0.6f, 1.8f)
			.eyeHeight(1.62f)
			.clientTrackingRange(32)
			.updateInterval(2)
			.build(modLocation("fake_player").toString())
	}

	@DataGenerateLanguage
	val FORKLIFT: Supplier<EntityType<Forklift>> = this.registry.register("forklift") { ->
		EntityType.Builder.of(::Forklift, MobCategory.MISC)
			.sized(1.5f, 2f)
			.eyeHeight(2f)
			.clientTrackingRange(10)
			.build(modLocation("forklift").toString())
	}
}