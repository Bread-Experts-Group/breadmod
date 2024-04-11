package breadmod.registry.entity

import breadmod.BreadMod
import net.minecraft.world.entity.decoration.PaintingVariant
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModPainting {
    val deferredRegister: DeferredRegister<PaintingVariant> = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, BreadMod.ID)

    val PAINTING_TEST: RegistryObject<PaintingVariant> = deferredRegister.register("painting_test") {
        PaintingVariant(64,64)
    }

    val DEVIL_PUPP: RegistryObject<PaintingVariant> = deferredRegister.register("devil_pupp") {
        PaintingVariant(1028,1028)
    }
}