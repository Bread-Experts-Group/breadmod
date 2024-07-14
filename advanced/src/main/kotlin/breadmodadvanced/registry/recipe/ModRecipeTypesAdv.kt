package breadmodadvanced.registry.recipe

import breadmodadvanced.ModMainAdv
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModRecipeTypesAdv {
    internal val deferredRegister: DeferredRegister<RecipeType<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ModMainAdv.ID)
}