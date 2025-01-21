package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawBorder
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawDirectionCube
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawScrollingStringBM
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.fixedLengthScrollingComponent
import org.bread_experts_group.breadmod.util.handlers.ExpansibleFluidHandler
import snownee.jade.api.fluid.JadeFluidObject
import snownee.jade.api.ui.Element
import snownee.jade.overlay.DisplayHelper
import java.awt.Color

class FluidBarElement(
	private val tank: ExpansibleFluidHandler.ExpansibleTank,
	private val direction: Direction?
) : Element() {
	override fun getSize(): Vec2 = this.size ?: Vec2(200f, 14f)

	override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
		val poseStack = guiGraphics.pose()
		poseStack.pushPose()
		// Fluid Box
		guiGraphics.drawBorder(
			x, y,
			x + 80, y + 14
		)
		DisplayHelper.INSTANCE.drawFluid(
			guiGraphics,
			x + 1,
			y + 1,
			JadeFluidObject.of(this.tank.fluid),
			((this.tank.amount / this.tank.capacity).toFloat() * 78),
			12f,
			JadeFluidObject.bucketVolume()
		)
		// Fluid Name
		val fluidNameKey = if (!this.tank.isEmpty) this.tank.fluidType.descriptionId else "tooltip.jade.empty"
		guiGraphics.drawScrollingStringBM(
			localClient.font,
			Component.translatable(fluidNameKey),
			(x + 2).toInt(),
			(x + 78).toInt(),
			y.toInt() + 3,
			Color.WHITE.rgb
		)
		// Direction Sprite
		guiGraphics.drawDirectionCube(x, y, this.direction)
		// Fluid Amount
		guiGraphics.drawScrollingStringBM(
			localClient.font,
			fixedLengthScrollingComponent(this.tank.amount, this.tank.capacity, "B", -1),
			(x + 100).toInt(),
			(x + 178).toInt(),
			(y + 3).toInt(),
			Color.WHITE.rgb
		)
		poseStack.popPose()
	}
}