package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyInput
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe
import java.awt.Color
import kotlin.jvm.optionals.getOrNull

// todo work on adding debug info
object BMRecipeDebugger {
	fun <T : BreadModRecipeBlockEntity<I, R, T>, I : FluidEnergyInput, R : FluidEnergyRecipe<I>> renderDebug(
		guiGraphics: GuiGraphics,
		partialTick: Float,
		mouseX: Int,
		mouseY: Int,
		blockEntity: T
	) {
		var y = 40
		guiGraphics.borderedFillPositioned(0, y, 150, 150, Color.RED, Color.WHITE)
		blockEntity.currentRecipe.getOrNull()?.let { recipe ->
			y += 2
			guiGraphics.string(recipe.type, 2, y)
			y += 8
			guiGraphics.string("time: ${recipe.rTime}", 2, y)
			y += 8
			guiGraphics.string("energy: ${recipe.rEnergy}", 2, y)
			y += 8
			guiGraphics.string("inputs:", 2, y)
			y += 8
			recipe.rItemInputs.forEachIndexed { index, sizedIngredient ->
				val stack = sizedIngredient.ingredient().items.first()
				y += (index * 8)
				guiGraphics.string("${stack.count}x ${stack.item}", 2, y)
			}
			y += 8
			guiGraphics.string("outputs:", 2, y)
			y += 8
			recipe.rItemOutputs.forEachIndexed { index, itemStack ->
				y += (index * 8)
				guiGraphics.string("${itemStack.count}x ${itemStack.item}", 2, y)
			}
		}
	}

	private fun GuiGraphics.string(text: Any?, x: Int, y: Int) =
		this.drawString(localClient.font, text.toString(), x, y, 0x00000000, false)
}