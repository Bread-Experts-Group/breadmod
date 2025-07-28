package org.bread_experts_group.breadmod.compat.lookingat.jade
//class FluidBarElement(
//	private val tank: ExpansibleFluidHandler.ExpansibleTank,
//	private val direction: Direction?
//) : Element() {
//	override fun getSize(): Vec2 = this.size ?: Vec2(180f, 14f)
//
//	override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
//		val poseStack = guiGraphics.pose()
//		poseStack.pushPose()
//		// Fluid Box
//		guiGraphics.drawBorder(
//			x, y,
//			x + 80, y + 14
//		)
//		guiGraphics.renderFluid(
//			x + 1,
//			y + 1,
//			78,
//			12,
//			this.tank,
//			false
//		)
//		// Fluid Name
//		val fluidNameKey = if (!this.tank.isEmpty) this.tank.fluidType.descriptionId else "tooltip.jade.empty"
//		guiGraphics.drawScrollingStringBM(
//			localClient.font,
//			Component.translatable(fluidNameKey),
//			(x + 2).toInt(),
//			(x + 78).toInt(),
//			y.toInt() + 3,
//			Color.WHITE.rgb
//		)
//		// Direction Sprite
//		guiGraphics.drawDirectionCube(x, y, this.direction, this.tank)
//		// Fluid Amount
//		guiGraphics.drawScrollingStringBM(
//			localClient.font,
//			fixedLengthScrollingComponent(
//				this.tank.amount, this.tank.capacity,
//				"B", -1,
//				IClientFluidTypeExtensions.of(this.tank.fluid).tintColor
//			),
//			(x + 100).toInt(),
//			(x + 178).toInt(),
//			(y + 3).toInt(),
//			Color.WHITE.rgb
//		)
//		poseStack.popPose()
//	}
//}