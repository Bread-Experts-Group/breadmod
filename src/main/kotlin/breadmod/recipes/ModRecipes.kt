package breadmod.recipes


import breadmod.BreadMod
import breadmod.item.ModItems.BREAD_BLOCK_ITEM
import breadmod.item.ModItems.BREAD_SHIELD
import breadmod.item.armor.ArmorPotionRecipe
import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.*
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.SimpleRecipeSerializer
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject
import java.util.function.Consumer

object ModRecipes {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BreadMod.ID)

    val ARMOR_POTIONS by REGISTRY.registerObject("crafting_special_armorpotion") { SimpleRecipeSerializer { ArmorPotionRecipe(it) } }

    class Generator(generator: DataGenerator) : RecipeProvider(generator) {
        override fun buildCraftingRecipes(pFinishedRecipeConsumer: Consumer<FinishedRecipe>) {
            SpecialRecipeBuilder.special(ARMOR_POTIONS).save(pFinishedRecipeConsumer, "${BreadMod.ID}:armor_potion")

            ShapelessRecipeBuilder
                .shapeless { BREAD_BLOCK_ITEM }
                .requires({ Items.BREAD }, 9)
                .unlockedBy("has_item", has(Items.BREAD))
                .save(pFinishedRecipeConsumer)

            ShapedRecipeBuilder
                .shaped { BREAD_SHIELD }
                .unlockedBy("has_item", has(BREAD_BLOCK_ITEM))
                .define('B', Ingredient.of(BREAD_BLOCK_ITEM))
                .define('I', Ingredient.of(Items.IRON_INGOT))
                .pattern("BIB").pattern("BBB").pattern(" B ")
                .save(pFinishedRecipeConsumer)
        }
    }
}