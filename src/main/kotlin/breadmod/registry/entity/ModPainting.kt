package breadmod.registry.entity

import breadmod.ModMain
import net.minecraft.world.entity.decoration.PaintingVariant
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModPainting {
    internal val deferredRegister: DeferredRegister<PaintingVariant> =
        DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, ModMain.ID)

    val PAINTING_TEST: RegistryObject<PaintingVariant> = deferredRegister.register("painting_test") {
        PaintingVariant(64, 64)
    }

    val DEVIL_PUPP: RegistryObject<PaintingVariant> = deferredRegister.register("devil_pupp") {
        PaintingVariant(64, 64)
    }

    // Specials
    val FISH: RegistryObject<PaintingVariant> = deferredRegister.register("fish") {
        PaintingVariant(360, 247)
    }

    val ELEPHANT: RegistryObject<PaintingVariant> = deferredRegister.register("elephant") {
        PaintingVariant(480, 400)
    }

    val CLASSIFIED: RegistryObject<PaintingVariant> = deferredRegister.register("meow") {
        PaintingVariant(409, 656)
    }
}