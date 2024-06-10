package breadmod.block.entity.screen

import breadmod.ModMain.modLocation
import breadmod.block.entity.menu.CoalGeneratorMenu
import breadmod.recipe.fluidEnergy.generators.CoalGeneratorRecipe
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class CoalGeneratorScreen(
    pMenu: CoalGeneratorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractPowerGeneratorScreen<CoalGeneratorRecipe, CoalGeneratorMenu>(
    pMenu, pPlayerInventory, pTitle, modLocation("textures", "gui", "container", "coal_generator.png"))