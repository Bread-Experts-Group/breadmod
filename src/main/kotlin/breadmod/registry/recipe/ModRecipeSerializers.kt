package breadmod.registry.recipe

import breadmod.ModMain
import breadmod.recipe.crafting.ArmorPotionRecipe
import breadmod.recipe.crafting.BreadSliceRecipe
import breadmod.recipe.crafting.DopedBreadRecipe
import breadmod.recipe.crafting.ToastedBreadSliceRecipe
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.recipe.fluidEnergy.ToasterRecipe
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.recipe.serializer.SimpleFluidEnergyRecipeSerializer
import net.minecraft.world.item.crafting.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModRecipeSerializers {
    internal val deferredRegister: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModMain.ID)

    val ARMOR_POTION: RegistryObject<SimpleCraftingRecipeSerializer<ArmorPotionRecipe>> =
        deferredRegister.register("bread_potion_crafting") {
            SimpleCraftingRecipeSerializer { pId, pCategory -> ArmorPotionRecipe(pId, pCategory) }
        }
    val DOPED_BREAD: RegistryObject<SimpleCraftingRecipeSerializer<DopedBreadRecipe>> =
        deferredRegister.register("doped_bread_crafting") {
            SimpleCraftingRecipeSerializer { pId, pCategory -> DopedBreadRecipe(pId, pCategory) }
        }
    val BREAD_SLICE: RegistryObject<SimpleCraftingRecipeSerializer<BreadSliceRecipe>> =
        deferredRegister.register("bread_slice_crafting") {
            SimpleCraftingRecipeSerializer { pId, pCategory -> BreadSliceRecipe(pId, pCategory) }
        }
    val TOAST_TO_TOAST_SLICE: RegistryObject<SimpleCraftingRecipeSerializer<ToastedBreadSliceRecipe>> =
        deferredRegister.register("toast_to_slice_crafting") {
            SimpleCraftingRecipeSerializer { pId, pCategory -> ToastedBreadSliceRecipe(pId, pCategory) }
        }

    val DOUGH_MACHINE: RegistryObject<SimpleFluidEnergyRecipeSerializer<DoughMachineRecipe>> =
        deferredRegister.register("dough_machine") {
            SimpleFluidEnergyRecipeSerializer { pId, pTime, pEnergy, pFluids, pFluidsTagged, pItems, pItemsTagged, pFluidsOut, pItemsOut ->
                DoughMachineRecipe(
                    pId, pTime, pEnergy, pFluids, pFluidsTagged, pItems, pItemsTagged, pFluidsOut, pItemsOut
                )
            }
        }
    val WHEAT_CRUSHER: RegistryObject<SimpleFluidEnergyRecipeSerializer<WheatCrushingRecipe>> =
        deferredRegister.register("wheat_crusher") {
            SimpleFluidEnergyRecipeSerializer { pId, pTime, pEnergy, _, _, pItems, pItemsTagged, _, pItemsOut ->
                WheatCrushingRecipe(
                    pId, pTime, pEnergy, pItems, pItemsTagged
                )
            }
        }
    val TOASTER: RegistryObject<SimpleFluidEnergyRecipeSerializer<ToasterRecipe>> =
        deferredRegister.register("toaster") {
            SimpleFluidEnergyRecipeSerializer { pId, pTime, _, _, _, pItems, pItemsTagged, _, pItemsOut ->
                ToasterRecipe(
                    pId,
                    pTime,
                    pItems,
                    pItemsTagged,
                    pItemsOut
                )
            }
        }

    /*val BREAD_REFINEMENT = REGISTRY.register("bread_refinement") {
        SimpleCookingSerializer({
            pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime ->
            BreadRefinementRecipe(pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime)
        }, 150)
    }*/
}