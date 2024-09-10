package bread.mod.breadmod.registry.entity

import bread.mod.breadmod.ModMainCommon.MOD_ID
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.datagen.tag.DataGenerateTag
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.decoration.PaintingVariant

/**
 * Painting variants for The Bread Mod.
 * @author Logan McLean
 * @since 1.0.0
 */
@Suppress("unused")
object ModPainting {
    val PAINTING_VARIANT_REGISTRY: DeferredRegister<PaintingVariant> =
        DeferredRegister.create(MOD_ID, Registries.PAINTING_VARIANT)

    @DataGenerateTag("minecraft:painting_variant", "minecraft:placeable")
    val PAINTING_TEST: RegistrySupplier<PaintingVariant> = PAINTING_VARIANT_REGISTRY.register("painting_test") {
        PaintingVariant(64, 64, modLocation("painting", "painting_test"))
    }

    @DataGenerateTag("minecraft:painting_variant", "minecraft:placeable")
    val DEVIL_PUPP: RegistrySupplier<PaintingVariant> = PAINTING_VARIANT_REGISTRY.register("devil_pupp") {
        PaintingVariant(64, 64, modLocation("painting", "devil_pupp"))
    }

    // Specials
    @DataGenerateTag("minecraft:painting_variant", "minecraft:placeable")
    val FISH: RegistrySupplier<PaintingVariant> = PAINTING_VARIANT_REGISTRY.register("fish") {
        PaintingVariant(360, 247, modLocation("painting", "fish"))
    }

    @DataGenerateTag("minecraft:painting_variant", "minecraft:placeable")
    val ELEPHANT: RegistrySupplier<PaintingVariant> = PAINTING_VARIANT_REGISTRY.register("elephant") {
        PaintingVariant(480, 400, modLocation("painting", "elephant"))
    }

    @DataGenerateTag("minecraft:painting_variant", "minecraft:placeable")
    val CLASSIFIED: RegistrySupplier<PaintingVariant> = PAINTING_VARIANT_REGISTRY.register("meow") {
        PaintingVariant(409, 656, modLocation("painting", "meow"))
    }
}