package bread.mod.breadmod.datagen.recipe

import bread.mod.breadmod.datagen.recipe.shaped.DataGenerateShapedRecipeThis
import bread.mod.breadmod.datagen.recipe.shapeless.DataGenerateShapelessRecipeExternal
import bread.mod.breadmod.datagen.recipe.shapeless.DataGenerateShapelessRecipeThis
import bread.mod.breadmod.datagen.recipe.special.DataGenerateToastingRecipe
import bread.mod.breadmod.recipe.toaster.ToasterRecipeBuilder
import bread.mod.breadmod.reflection.LibraryScanner
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import java.util.concurrent.CompletableFuture

class SmartRecipeProvider(
    val modID: String, forClassLoader: ClassLoader, forPackage: Package
) {
    private val scanner: LibraryScanner = LibraryScanner(forClassLoader, forPackage)

    fun getProvider(packOutput: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>): RecipeProvider {
        return object : RecipeProvider(packOutput, lookupProvider) {
            override fun buildRecipes(recipeOutput: RecipeOutput) {
                buildMap<String, MutableList<Pair<ItemLike, Annotation>>> {
                    listOf(
                        scanner.getObjectPropertiesAnnotatedWith<DataGenerateShapedRecipeThis>(),
                        scanner.getObjectPropertiesAnnotatedWith<DataGenerateShapelessRecipeThis>(),

                        // External
                        scanner.getObjectPropertiesAnnotatedWith<DataGenerateShapelessRecipeExternal>(),

                        // Special
                        scanner.getObjectPropertiesAnnotatedWith<DataGenerateToastingRecipe>()
                    ).forEach {
                        it.forEach { (property, data) ->
                            val supplier = data.first
                            if (supplier !is RegistrySupplier<*>) throw IllegalArgumentException(
                                String.format(
                                    "%s must be of type %s.",
                                    property.name, RegistrySupplier::class.qualifiedName
                                )
                            )

                            val actual = supplier.get()
                            if (actual !is ItemLike) throw IllegalArgumentException(
                                String.format(
                                    "%s must supply type %s.",
                                    property.name, ItemLike::class.qualifiedName
                                )
                            )

                            when (val annotation = data.second.first()) {
                                is DataGenerateShapedRecipeThis -> {
                                    val recipe = ShapedRecipeBuilder.shaped(annotation.category, actual, annotation.count)
                                    annotation.rows.forEach { r -> recipe.pattern(r) }
                                    annotation.definitions.forEachIndexed { i, d ->
                                        val item = BuiltInRegistries.ITEM[ResourceLocation.parse(annotation.resolution[i])]
                                        recipe.define(d, item)
                                        recipe.unlockedBy("has_item", has(item))
                                    }
                                    recipe.save(recipeOutput, ResourceLocation.fromNamespaceAndPath(modID, annotation.name))
                                }

                                is DataGenerateShapelessRecipeThis -> {
                                    val recipe = ShapelessRecipeBuilder.shapeless(annotation.category, actual, annotation.count)
                                    annotation.types.forEachIndexed { i, d ->
                                        val item = BuiltInRegistries.ITEM[ResourceLocation.parse(d)]
                                        recipe.requires(item, annotation.counts[i])
                                        recipe.unlockedBy("has_item", has(item))
                                    }
                                    recipe.save(recipeOutput, ResourceLocation.fromNamespaceAndPath(modID, annotation.name))
                                }

                                is DataGenerateShapelessRecipeExternal ->
                                    getOrPut(annotation.name) { mutableListOf() }.add(actual to annotation)

                                is DataGenerateToastingRecipe -> {
                                    val recipe = ToasterRecipeBuilder(
                                        ItemStack(BuiltInRegistries.ITEM[ResourceLocation.parse(annotation.required)], annotation.count),
                                        ItemStack(actual.asItem(), annotation.count),
                                        annotation.time
                                    )
                                    recipe.unlockedBy("has_item", has(actual))
                                        .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(modID, annotation.name))
                                }

                                else -> throw UnsupportedOperationException(annotation.annotationClass.qualifiedName)
                            }
                        }
                    }
                }.forEach { (name, data) ->
                    when (val initialAnnotation = data.first().second) {
                        is DataGenerateShapelessRecipeExternal -> {
                            val forItem = BuiltInRegistries.ITEM[ResourceLocation.parse(initialAnnotation.forItem)]
                            val recipe =
                                ShapelessRecipeBuilder.shapeless(initialAnnotation.category, forItem, initialAnnotation.count)
                            data.forEach { (actual, annotation) ->
                                recipe.requires(actual, (annotation as DataGenerateShapelessRecipeExternal).neededCount)
                                recipe.unlockedBy("has_item", has(actual))
                            }
                            recipe.save(recipeOutput, ResourceLocation.fromNamespaceAndPath(modID, name))
                        }

                        else -> throw UnsupportedOperationException(initialAnnotation.annotationClass.qualifiedName)
                    }
                }
            }
        }
    }
}