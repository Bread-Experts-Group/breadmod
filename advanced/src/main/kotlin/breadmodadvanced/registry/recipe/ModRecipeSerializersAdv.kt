package breadmodadvanced.registry.recipe

import breadmodadvanced.ModMainAdv
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModRecipeSerializersAdv {
    internal val deferredRegister: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModMainAdv.ID)
}