package org.bread_experts_group.breadmod.compat.lookingat.jade.elements

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.client.render.fillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.util.Color
import snownee.jade.api.ui.Element
import java.math.BigDecimal
import java.math.MathContext

class EnergyElement(
	private val energyStored: BigDecimal,
	private val energyCapacity: BigDecimal,
) : Element() {
	private companion object {
		private val scaleSize: BigDecimal = BigDecimal(100)
	}

	override fun getSize(): Vec2 = this.size ?: Vec2(180f, 20f)

	override fun render(
		guiGraphics: GuiGraphics,
		x: Float,
		y: Float,
		maxX: Float,
		maxY: Float
	) {
		val divided = this.energyStored.divide(this.energyCapacity, MathContext.DECIMAL32)
		val scaled = (divided * Companion.scaleSize).toInt()

		guiGraphics.fillPositioned(x.toInt(), y.toInt(), scaled, 20, Color.RED)
		guiGraphics.drawString(localClient.font, "STORED: ${this.energyStored}", x.toInt(), y.toInt(), Color.WHITE)
	}
}