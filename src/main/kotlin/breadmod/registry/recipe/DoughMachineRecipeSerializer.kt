package breadmod.registry.recipe

import breadmod.recipe.DoughMachineRecipe
import com.google.gson.JsonObject
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeSerializer

class DoughMachineRecipeSerializer : RecipeSerializer<DoughMachineRecipe> {
    override fun fromJson(p0: ResourceLocation, p1: JsonObject): DoughMachineRecipe {
        TODO("Not yet implemented")
    }

    override fun fromNetwork(p0: ResourceLocation, p1: FriendlyByteBuf): DoughMachineRecipe {
        TODO("Not yet implemented")
    }

    override fun toNetwork(p0: FriendlyByteBuf, p1: DoughMachineRecipe) {
        TODO("Not yet implemented")
    }
}