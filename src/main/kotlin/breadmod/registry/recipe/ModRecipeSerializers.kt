package breadmod.registry.recipe

import breadmod.ModMain
import breadmod.recipe.*
import breadmod.recipe.serializers.SimpleFluidEnergyRecipeSerializer
import breadmod.recipe.serializers.SimpleItemEnergyRecipeSerializer
import net.minecraft.world.item.crafting.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModRecipeSerializers {
    val deferredRegister: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModMain.ID)

    val ARMOR_POTION: RegistryObject<SimpleCraftingRecipeSerializer<ArmorPotionRecipe>> = deferredRegister.register("bread_potion_crafting") {
        SimpleCraftingRecipeSerializer { pId, pCategory -> ArmorPotionRecipe(pId, pCategory) } }
    val DOPED_BREAD: RegistryObject<SimpleCraftingRecipeSerializer<DopedBreadRecipe>> = deferredRegister.register("doped_bread_crafting") {
        SimpleCraftingRecipeSerializer { pId, pCategory -> DopedBreadRecipe(pId, pCategory) } }
    val BREAD_SLICE: RegistryObject<SimpleCraftingRecipeSerializer<BreadSliceRecipe>> = deferredRegister.register("bread_slice_crafting") {
        SimpleCraftingRecipeSerializer { pId, pCategory -> BreadSliceRecipe(pId, pCategory) } }

    val FLUID_ENERGY: RegistryObject<SimpleFluidEnergyRecipeSerializer<FluidEnergyRecipe>> = deferredRegister.register("fluid_energy") {
        SimpleFluidEnergyRecipeSerializer { pId, pTime, pEnergy, pFluids, pFluidsTagged, pItems, pItemsTagged, pFluidsOut, pItemsOut -> FluidEnergyRecipe(pId, pTime, pEnergy, pFluids, pFluidsTagged, pItems, pItemsTagged, pFluidsOut, pItemsOut) }
    }
    val WHEAT_CRUSHING: RegistryObject<SimpleItemEnergyRecipeSerializer<WheatCrusherRecipe>> = deferredRegister.register("wheat_crushing") {
        SimpleItemEnergyRecipeSerializer { pId, pTime, pEnergy, pItems, pItemsTagged, pItemsOut -> WheatCrusherRecipe(pId,pTime,pEnergy,pItems,pItemsTagged,pItemsOut)}
    }

    /*val BREAD_REFINEMENT = REGISTRY.register("bread_refinement") {
        SimpleCookingSerializer({
            pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime ->
            BreadRefinementRecipe(pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime)
        }, 150)
    }*/
}