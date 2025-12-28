package org.bread_experts_group.breadmod.compat.lookingat.jade.elements

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.client.render.fillPositioned
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.util.Color
import snownee.jade.api.ui.Element
import java.math.BigDecimal

class EnergyElement(
	private val energy: ExtendedEnergyHandler
) : Element() {
	private companion object {
		private val bigDecimal: BigDecimal = BigDecimal(100)
	}

	override fun getSize(): Vec2 = this.size ?: Vec2(180f, 20f)

	override fun render(
		guiGraphics: GuiGraphics,
		x: Float,
		y: Float,
		maxX: Float,
		maxY: Float
	) {
		val scaled = ((this.energy.bigAmount / this.energy.bigCapacity) * Companion.bigDecimal).toInt()
//		logDebugInfo(scaled)

//		guiGraphics.fillPositioned(x.toInt(), y.toInt(), 100, 20, Color.WHITE)
		guiGraphics.fillPositioned(x.toInt(), y.toInt(), scaled, 20, Color.RED)
	}
}