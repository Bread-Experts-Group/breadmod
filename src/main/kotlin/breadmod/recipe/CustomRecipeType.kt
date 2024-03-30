package breadmod.recipe

import breadmod.BreadMod
import breadmod.item.armor.ArmorPotionRecipe
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object CustomRecipeType {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BreadMod.ID)

//    val ARMOR_POTIONS by REGISTRY.registerObject("crafting_special_armorpotion") { SimpleCraftingRecipeSerializer { ArmorPotionRecipe(it) } } // TODO("To Be Fixed")
}