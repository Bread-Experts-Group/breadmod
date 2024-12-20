package org.bread_experts_group.breadmod.experimental.recipe.block.multi.fluid

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen

class MultiFluidScreen(
	menu : MultiFluidRecipeMenu,
	inventory : Inventory,
	title : Component
) : AbstractRecipeScreen<MultiFluidRecipeMenu>(menu, inventory, title) {
	override fun renderBg(guiGraphics : GuiGraphics, partialTick : Float, mouseX : Int, mouseY : Int) {
		super.renderBg(guiGraphics, partialTick, mouseX, mouseY)
//        val parent = menu.parent as MultiFluidRecipeBlockEntity
//        guiGraphics.drawString(
//            rgMinecraft.font,
//            "tank 1: ${parent.sidedTest.getFluidType(0)}, ${parent.sidedTest.getFluidInTank(0).amount}",
//            leftPos + 10,
//            topPos + 30,
//            Color.WHITE.rgb
//        )
//        guiGraphics.drawString(
//            rgMinecraft.font,
//            "tank 2: ${parent.sidedTest.getFluidType(1)}, ${parent.sidedTest.getFluidInTank(1).amount}",
//            leftPos + 10,
//            topPos + 40,
//            Color.WHITE.rgb
//        )
//        guiGraphics.drawString(
//            rgMinecraft.font,
//            "tank 3: ${parent.sidedTest.getFluidType(2)}, ${parent.sidedTest.getFluidInTank(2).amount}",
//            leftPos + 10,
//            topPos + 50,
//            Color.WHITE.rgb
//        )
//        guiGraphics.drawString(
//            rgMinecraft.font,
//            "tank 4: ${parent.sidedTest.getFluidType(3)}, ${parent.sidedTest.getFluidInTank(3).amount}",
//            leftPos + 10,
//            topPos + 60,
//            Color.WHITE.rgb
//        )
	}
}