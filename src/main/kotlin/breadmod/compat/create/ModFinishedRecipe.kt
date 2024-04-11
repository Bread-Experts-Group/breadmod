package breadmod.compat.create

import com.google.gson.JsonObject
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeSerializer

open class ModFinishedRecipe : FinishedRecipe {

    override fun serializeRecipeData(pJson: JsonObject) {
    }

    override fun getId(): ResourceLocation {
        return id
    }

    override fun getType(): RecipeSerializer<*> {
        return type
    }

    override fun serializeAdvancement(): JsonObject? {
        return null
    }

    override fun getAdvancementId(): ResourceLocation? {
        return null
    }
}
