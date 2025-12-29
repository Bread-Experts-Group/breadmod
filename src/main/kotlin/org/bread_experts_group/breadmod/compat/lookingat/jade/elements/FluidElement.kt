package org.bread_experts_group.breadmod.compat.lookingat.jade.elements

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec2
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderFluid
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawBorder
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawDirectionCube
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon.drawScrollingStringBM
import org.bread_experts_group.breadmod.util.Color
import snownee.jade.api.ui.Element
import java.math.BigDecimal

class FluidElement(
	private val fluid: Fluid,
	private val stored: BigDecimal,
	private val capacity: BigDecimal,
	private val direction: Direction?
) : Element() {
	override fun getSize(): Vec2 = this.size ?: Vec2(180f, 14f)

	override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
		val poseStack = guiGraphics.pose()
		poseStack.pushPose()
		// Fluid Box
		guiGraphics.drawBorder(
			x, y,
			x + 80, y + 14
		)
		guiGraphics.renderFluid(
			x + 1,
			y + 1,
			78,
			12,
			this.fluid,
			this.stored,
			this.capacity,
			false
		)
		// Fluid Name
		val fluidNameKey = if (this.fluid != Fluids.EMPTY) this.fluid.fluidType.descriptionId else "tooltip.jade.empty"
		guiGraphics.drawScrollingStringBM(
			localClient.font,
			Component.translatable(fluidNameKey),
			(x + 2).toInt(),
			(x + 78).toInt(),
			y.toInt() + 3,
			Color.WHITE
		)
		// Direction Sprite
		guiGraphics.drawDirectionCube(x, y, this.direction, /*this.tank*/)
		// Fluid Amount
		guiGraphics.drawScrollingStringBM(
			localClient.font,
			JadeDrawingCommon.fixedLengthScrollingComponent(
				this.stored,
				this.capacity,
				"B",
				IClientFluidTypeExtensions.of(this.fluid).tintColor
			),
			(x + 100).toInt(),
			(x + 178).toInt(),
			(y + 3).toInt(),
			Color.WHITE
		)
		poseStack.popPose()
	}
}