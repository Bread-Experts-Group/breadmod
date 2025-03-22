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
	fun <T : BreadModRecipeBlockEntity<I, R, T>, I : FluidEnergyInput,  R : FluidEnergyRecipe<I>> renderDebug(
		guiGraphics: GuiGraphics,
		partialTick: Float,
		mouseX: Int,
		mouseY: Int,
		blockEntity: T
	) {
		guiGraphics.borderedFillPositioned(0, 0, 150, 150, Color.RED, Color.WHITE)
		blockEntity.currentRecipe.getOrNull().let { recipe ->
			guiGraphics.string(recipe, 2, 2)
		}
	}

	private fun GuiGraphics.string(text: Any?, x: Int, y: Int) =
		this.drawString(localClient.font, text.toString(), x, y, 0x00000000, false)
}