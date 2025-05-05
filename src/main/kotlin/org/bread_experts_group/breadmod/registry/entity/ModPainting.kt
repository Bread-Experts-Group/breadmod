package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.decoration.PaintingVariant
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.datagen.tag.DataGenerateTagPainting

object ModPainting {
	@DataGenerateTagPainting("minecraft:placeable")
	@DataGenerateLanguage("en_us", prefix = "painting.", suffix = ".title")
	@DataGenerateLanguage("en_us", "logan mclean", "painting.", ".author")
	val PAINTING_TEST: ResourceKey<PaintingVariant> = this.create("painting_test")

	@DataGenerateTagPainting("minecraft:placeable")
	@DataGenerateLanguage("en_us", "marrroww", "painting.", ".title")
	@DataGenerateLanguage("en_us", "https://x.com/aluminumoxy/media", "painting.", ".author")
	val DEVIL_PUPP: ResourceKey<PaintingVariant> = this.create("devil_pupp")

	// Specials
	@DataGenerateTagPainting("minecraft:placeable")
	@DataGenerateLanguage("en_us", "experimental giffy fish", "painting.", ".title")
	@DataGenerateLanguage("en_us", "N/A", "painting.", ".author")
	val FISH: ResourceKey<PaintingVariant> = this.create("fish")

	@DataGenerateTagPainting("minecraft:placeable")
	@DataGenerateLanguage("en_us", "experimental apngy elephant", "painting.", ".title")
	@DataGenerateLanguage("en_us", "N/A", "painting.", ".author")
	val ELEPHANT: ResourceKey<PaintingVariant> = this.create("elephant")

	@DataGenerateTagPainting("minecraft:placeable")
	@DataGenerateLanguage("en_us", "CLASSIFIED", "painting.", ".title")
	@DataGenerateLanguage("en_us", "N/A", "painting.", ".author")
	val CLASSIFIED: ResourceKey<PaintingVariant> = this.create("meow")
	fun bootstrap(context: BootstrapContext<PaintingVariant>) {
		this.register(context, this.PAINTING_TEST, 4, 4)
		this.register(context, this.DEVIL_PUPP, 4, 4)
		this.register(context, this.FISH, 2, 2)
		this.register(context, this.ELEPHANT, 2, 2)
		this.register(context, this.CLASSIFIED, 1, 2)
	}

	private fun register(
		context: BootstrapContext<PaintingVariant>,
		key: ResourceKey<PaintingVariant>,
		width: Int,
		height: Int
	): Holder.Reference<PaintingVariant> = context.register(key, PaintingVariant(width, height, key.location()))

	private fun create(name: String): ResourceKey<PaintingVariant> =
		ResourceKey.create(Registries.PAINTING_VARIANT, modLocation(name))
}