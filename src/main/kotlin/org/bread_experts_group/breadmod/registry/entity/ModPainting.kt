package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.decoration.PaintingVariant
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

object ModPainting {
    val PAINTING_TEST: ResourceKey<PaintingVariant> = create("painting_test")
    val DEVIL_PUPP: ResourceKey<PaintingVariant> = create("devil_pupp")

    // Specials
    val FISH: ResourceKey<PaintingVariant> = create("fish")
    val ELEPHANT: ResourceKey<PaintingVariant> = create("elephant")
    val CLASSIFIED: ResourceKey<PaintingVariant> = create("meow")

    fun bootstrap(context: BootstrapContext<PaintingVariant>) {
        register(context, PAINTING_TEST, 4, 4)
        register(context, DEVIL_PUPP, 4, 4)
        register(context, FISH, 2, 2)
        register(context, ELEPHANT, 2, 2)
        register(context, CLASSIFIED, 1, 2)
    }

    private fun register(
        context: BootstrapContext<PaintingVariant>,
        key: ResourceKey<PaintingVariant>,
        width: Int,
        height: Int
    ) = context.register(key, PaintingVariant(width, height, key.location()))

    private fun create(name: String): ResourceKey<PaintingVariant> =
        ResourceKey.create(Registries.PAINTING_VARIANT, modLocation(name))
}