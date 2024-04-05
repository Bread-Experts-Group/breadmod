package breadmod.screens

import breadmod.BreadMod
import breadmod.block.BreadFurnaceBlock
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
class BreadFurnaceScreen(
    pMenu: BreadFurnaceBlock.Menu,
    pPlayerInventory: Inventory,
    pTitle: Component
): AbstractFurnaceScreen<BreadFurnaceBlock.Menu>(
    pMenu,
    SmeltingRecipeBookComponent(),
    pPlayerInventory,
    pTitle,
    ResourceLocation(BreadMod.ID, "textures/gui/container/bread_furnace.png")
)