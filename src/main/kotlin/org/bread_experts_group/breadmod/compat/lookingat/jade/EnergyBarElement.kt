package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawBorder
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawDirectionCube
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawScrollingStringBM
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.fixedLengthScrollingComponent
import org.bread_experts_group.breadmod.util.handlers.ExpansibleEnergyHandler
import snownee.jade.api.fluid.JadeFluidObject
import snownee.jade.api.ui.Element
import snownee.jade.overlay.DisplayHelper
import java.awt.Color

class EnergyBarElement(
	private val cell: ExpansibleEnergyHandler.ExpansibleCell,
	private val direction: Direction?
) : Element() {
	override fun getSize(): Vec2 = this.size ?: Vec2(150f, 14f)

	override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
		val poseStack = guiGraphics.pose()
		poseStack.pushPose()
		// Fluid Box (TODO!)
		guiGraphics.drawBorder(
			x, y,
			x + 80, y + 14
		)
		DisplayHelper.INSTANCE.drawFluid(
			guiGraphics,
			x + 1,
			y + 1,
			JadeFluidObject.of(BuiltInRegistries.FLUID.first()),
			this.cell.capacity?.let { ((this.cell.amount / it).toFloat() * 78) } ?: 78f,
			12f,
			JadeFluidObject.bucketVolume()
		)
		// Direction Sprite
		guiGraphics.drawDirectionCube(x, y, this.direction)
		// Cell Amount
		guiGraphics.drawScrollingStringBM(
			localClient.font,
			fixedLengthScrollingComponent(this.cell.amount, this.cell.capacity, "RF"),
			(x + 2).toInt(),
			(x + 78).toInt(),
			y.toInt() + 3,
			Color.WHITE.rgb
		)
		poseStack.popPose()
	}
}