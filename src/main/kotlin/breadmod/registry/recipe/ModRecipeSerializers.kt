package breadmod.registry.recipe

import breadmod.BreadMod
import breadmod.recipe.ArmorPotionRecipe
import breadmod.recipe.BreadSliceRecipe
import breadmod.recipe.DopedBreadRecipe
import breadmod.recipe.FlourToDoughMachineRecipe
import breadmod.recipe.serializers.SimpleFluidEnergyRecipeSerializer
import net.minecraft.world.item.crafting.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModRecipeSerializers {
    val deferredRegister: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BreadMod.ID)

    val ARMOR_POTION: RegistryObject<SimpleCraftingRecipeSerializer<ArmorPotionRecipe>> = deferredRegister.register("bread_potion_crafting") {
        SimpleCraftingRecipeSerializer { pId, pCategory -> ArmorPotionRecipe(pId, pCategory) } }
    val DOPED_BREAD: RegistryObject<SimpleCraftingRecipeSerializer<DopedBreadRecipe>> = deferredRegister.register("doped_bread_crafting") {
        SimpleCraftingRecipeSerializer { pId, pCategory -> DopedBreadRecipe(pId, pCategory) } }
    val BREAD_SLICE: RegistryObject<SimpleCraftingRecipeSerializer<BreadSliceRecipe>> = deferredRegister.register("bread_slice_crafting") {
        SimpleCraftingRecipeSerializer { pId, pCategory -> BreadSliceRecipe(pId, pCategory) } }

    val FLOUR_TO_DOUGH: RegistryObject<SimpleFluidEnergyRecipeSerializer<FlourToDoughMachineRecipe>> = deferredRegister.register("energy_fluid_item") {
        SimpleFluidEnergyRecipeSerializer { cId, _, _, _, _, _, _, _, _ -> FlourToDoughMachineRecipe(cId) } }

    /*val BREAD_REFINEMENT = REGISTRY.register("bread_refinement") {
        SimpleCookingSerializer({
            pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime ->
            BreadRefinementRecipe(pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime)
        }, 150)
    }*/
}