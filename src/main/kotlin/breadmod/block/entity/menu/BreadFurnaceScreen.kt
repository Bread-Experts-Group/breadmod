package breadmod.block.entity.menu

import breadmod.BreadMod.modLocation
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class BreadFurnaceScreen(
    pMenu: BreadFurnaceMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
): AbstractFurnaceScreen<BreadFurnaceMenu>(
    pMenu,
    SmeltingRecipeBookComponent(),
    pPlayerInventory,
    pTitle,
    modLocation("textures/gui/container/bread_furnace.png")
)