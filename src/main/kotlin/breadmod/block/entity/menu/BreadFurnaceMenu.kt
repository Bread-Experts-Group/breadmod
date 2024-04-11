package breadmod.block.entity.menu

import breadmod.registry.recipe.ModRecipeTypes
import breadmod.registry.screen.ModMenuTypes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractFurnaceMenu
import net.minecraft.world.inventory.RecipeBookType

class BreadFurnaceMenu(pContainerId: Int, pInventory: Inventory): AbstractFurnaceMenu(
    ModMenuTypes.BREAD_FURNACE.get(), ModRecipeTypes.BREAD_REFINEMENT, RecipeBookType.FURNACE, pContainerId, pInventory
)