package breadmodadvanced.registry.recipe

import breadmod.recipe.serializer.SimpleFluidEnergyRecipeSerializer
import breadmodadvanced.ModMainAdv
import breadmodadvanced.recipe.fluidEnergy.generators.DieselGeneratorRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModRecipeSerializersAdv {
    val deferredRegister: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModMainAdv.ID)

    val DIESEL_GENERATOR: RegistryObject<SimpleFluidEnergyRecipeSerializer<DieselGeneratorRecipe>> = deferredRegister.register("deisel_generator") {
        SimpleFluidEnergyRecipeSerializer { pId, pTime, pEnergy, pFluids, pFluidsTagged, pItems, pItemsTagged, _, _ -> DieselGeneratorRecipe(pId, pTime, pEnergy, pFluids, pFluidsTagged, pItems, pItemsTagged)}
    }
}