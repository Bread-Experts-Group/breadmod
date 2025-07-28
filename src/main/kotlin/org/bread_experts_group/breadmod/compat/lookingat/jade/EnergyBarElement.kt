package org.bread_experts_group.breadmod.compat.lookingat.jade
//class EnergyBarElement(
//	private val cell: ExpansibleEnergyHandler.ExpansibleCell,
//	private val direction: Direction?
//) : Element() {
//	override fun getSize(): Vec2 = this.size ?: Vec2(150f, 14f)
//
//	override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
//		val poseStack = guiGraphics.pose()
//		poseStack.pushPose()
//		guiGraphics.drawBorder(
//			x, y,
//			x + 80, y + 14
//		)
//		DisplayHelper.INSTANCE.drawFluid(
//			guiGraphics,
//			x + 1,
//			y + 1,
//			JadeFluidObject.of(Fluids.FLOWING_WATER), // TODO; change this to blinker fluid
//			this.cell.capacity?.let { ((this.cell.amount.divide(it)).toFloat() * 78) } ?: 78f,
//			12f,
//			JadeFluidObject.bucketVolume()
//		)
//		// Direction Sprite
//		guiGraphics.drawDirectionCube(x, y, this.direction, this.cell)
//		// Cell Amount
//		guiGraphics.drawScrollingStringBM(
//			localClient.font,
//			fixedLengthScrollingComponent(
//				this.cell.amount, this.cell.capacity,
//				"RF",
//				tint = ChatFormatting.RED.color ?: return
//			),
//			(x + 2).toInt(),
//			(x + 78).toInt(),
//			y.toInt() + 3,
//			Color.WHITE.rgb
//		)
//		poseStack.popPose()
//	}
//}