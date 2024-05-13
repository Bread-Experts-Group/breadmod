package breadmod.compat.jei.vanillaExtension

import breadmod.recipe.ArmorPotionRecipe
import breadmod.registry.item.ModItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraftforge.registries.ForgeRegistries

class JEIArmorPotionCraftingExtension(val recipe: ArmorPotionRecipe): ICraftingCategoryExtension {
    override fun getWidth(): Int = 2
    override fun getHeight(): Int = 1

    // TODO: Naive Impl. See if there's any better way to do this. ~ Miko
    private val armorSet = listOf(
        ModItems.BREAD_HELMET.get(),
        ModItems.BREAD_CHESTPLATE.get(),
        ModItems.BREAD_LEGGINGS.get(),
        ModItems.BREAD_BOOTS.get()
    ).map { it.defaultInstance }

    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        craftingGridHelper: ICraftingGridHelper,
        focuses: IFocusGroup,
    ) {
        val potion = PotionUtils.setPotion(
            Items.POTION.defaultInstance,
            ForgeRegistries.POTIONS.values.filter {
                !it.hasInstantEffects() && it.effects.size == 1
            }.random()
        )

        craftingGridHelper.createAndSetInputs(
            builder,
            listOf(
                armorSet,
                listOf(potion)
            ), width, height
        )
        craftingGridHelper.createAndSetOutputs(
            builder,
            armorSet.map { recipe.applyPotionForItem(PotionUtils.getMobEffects(potion), it) }
        )
    }
}