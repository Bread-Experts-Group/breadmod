package breadmod.recipe

import breadmod.BreadMod
import net.minecraft.world.item.crafting.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModRecipeSerializers {
    val REGISTRY: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BreadMod.ID)

    val ARMOR_POTION_CRAFTING = REGISTRY.register("crafting_bread_potion_crafting") {
            SimpleCraftingRecipeSerializer { pId, pCategory -> ArmorPotionRecipe(pId, pCategory) } }
    /*val BREAD_REFINEMENT = REGISTRY.register("bread_refinement") {
        SimpleCookingSerializer({
            pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime ->
            BreadRefinementRecipe(pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime)
        }, 150)
    }*/
}