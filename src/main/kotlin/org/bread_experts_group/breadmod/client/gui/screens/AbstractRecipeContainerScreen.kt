package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.level.block.entity.BlockEntity
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.menu.actual.BMContainerMenu
import org.bread_experts_group.breadmod.registry.menu.actual.LambdaSlotItemHandler

abstract class AbstractRecipeContainerScreen<T : BMContainerMenu.Entity<BE>, BE : BlockEntity>(
	menu: T,
	inventory: Inventory,
	title: Component
) : AbstractContainerScreen<T>(menu, inventory, title) {
	companion object {
		@DataGenerateLanguage(name = "Energy")
		val ENERGY_LABEL: MutableComponent = modTranslatable(path = arrayOf("energy"))
	}

	private fun GuiGraphics.renderEnergyMeter(x: Int, y: Int, h: Int, cell: Int? = null) {
//		val energyHandler = (this@AbstractRecipeContainerScreen.menu.parent as EnergyBearingBlockEntity).energyHandler
//		val sap = if (cell == null) energyHandler else energyHandler.getUnit(cell)
//		val scaled = sap.capacity?.let { ((sap.amount.divide(it)).toFloat() * h).toInt() } ?: 0
		ModGuiElements.SLOT.blitScaled(
			this,
			this@AbstractRecipeContainerScreen.leftPos + x,
			this@AbstractRecipeContainerScreen.topPos + y,
			18, 49
		)
		TODO("Extensible Tank")
//		ModGuiElements.ENERGY_METER.blit(
//			this,
//			this@AbstractRecipeContainerScreen.leftPos + x + 1,
//			this@AbstractRecipeContainerScreen.topPos + y + 1 + h - scaled,
//			vOffset = 47f - scaled,
//			vHeight = scaled
//		)
	}

	private fun GuiGraphics.renderEnergyTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		cell: Int? = null
	) {
		if (this@AbstractRecipeContainerScreen.isHovering(x, y, w, h, mouseX, mouseY)) {
			TODO("Extensible Tank")
//			val sap = if (cell == null) energyHandler else energyHandler.getUnit(cell)
//			this.renderComponentTooltip(
//				this@AbstractRecipeContainerScreen.font,
//				listOf(
//					Companion.ENERGY_LABEL
//						.withStyle(ChatFormatting.RED)
//						.withStyle(ChatFormatting.ITALIC),
//					JadeDrawingCommon.fixedLengthScrollingComponent(
//						sap.amount,
//						sap.capacity,
//						"FE",
//						tint = ChatFormatting.RED.color ?: return
//					)
//				),
//				mouseX.toInt(), mouseY.toInt()
//			)
		}
	}

	private fun GuiGraphics.drawFillBox(
		x: Int,
		y: Int,
		w: Int,
		h: Int
	) {
		if (localClient.options.advancedItemTooltips) {
			this.fill(
				this@AbstractRecipeContainerScreen.leftPos + x,
				this@AbstractRecipeContainerScreen.topPos + y,
				this@AbstractRecipeContainerScreen.leftPos + x + w,
				this@AbstractRecipeContainerScreen.topPos + y + h,
				0x7F000080
			)
		}
	}

	protected fun GuiGraphics.renderEnergyWithTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		cell: Int? = null
	) {
		this.drawFillBox(x, y, w, h)
		this.renderEnergyMeter(x, y, h, cell)
		this.renderEnergyTooltip(x, y, w, h, mouseX, mouseY, cell)
	}

	private fun GuiGraphics.renderFluidMeter(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		tank: Int,
		flowing: Boolean = false
	) {
		ModGuiElements.SLOT.blitScaled(
			this,
			this@AbstractRecipeContainerScreen.leftPos + x,
			this@AbstractRecipeContainerScreen.topPos + y,
			w + 2,
			h + 2
		)
		TODO("Extensible Tank")
//		val fluidHandler = (this@AbstractRecipeContainerScreen.menu.parent as FluidBearingBlockEntity).fluidHandler
//		val expansibleTank = fluidHandler.getUnit(tank)
//		if (expansibleTank.amount > BigDecimal.ZERO) {
//			this.renderFluid(
//				(this@AbstractRecipeContainerScreen.leftPos + x.toFloat()) + 1,
//				(this@AbstractRecipeContainerScreen.topPos + y.toFloat()) + 1,
//				w,
//				h,
//				expansibleTank,
//				flowing
//			)
//		}
	}

	fun renderSlots(guiGraphics: GuiGraphics) {
		this.menu.slots.forEach { slot ->
			when (slot) {
				is LambdaSlotItemHandler -> slot.jadeGraphic.blit(
					guiGraphics,
					this.leftPos + slot.x - 1,
					this.topPos + slot.y - 1
				)
				else -> ModGuiElements.SLOT.blit(
					guiGraphics,
					this.leftPos + slot.x - 1,
					this.topPos + slot.y - 1
				)
			}
		}
	}

	private fun GuiGraphics.renderFluidTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		tank: Int
	) {
		if (this@AbstractRecipeContainerScreen.isHovering(x, y, w, h, mouseX, mouseY)) {
			TODO("Extensible Tank")
//			val fluidHandler = (this@AbstractRecipeContainerScreen.menu.parent as FluidBearingBlockEntity).fluidHandler
//			val expansibleTank = fluidHandler.getUnit(tank)
//			val tint = IClientFluidTypeExtensions.of(expansibleTank.fluid).tintColor
//			this.renderComponentTooltip(
//				this@AbstractRecipeContainerScreen.font,
//				listOf(
//					Component.translatable(expansibleTank.fluidType.descriptionId)
//						.withStyle(Style.EMPTY.withColor(tint))
//						.withStyle(ChatFormatting.ITALIC),
//					JadeDrawingCommon.fixedLengthScrollingComponent(
//						expansibleTank.amount, expansibleTank.capacity,
//						"B", -1,
//						tint
//					)
//				),
//				mouseX.toInt(), mouseY.toInt()
//			)
		}
	}

	protected fun GuiGraphics.renderFluidWithTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		tank: Int
	) {
		this.drawFillBox(x, y, w, h)
		this.renderFluidMeter(x, y, w, h, tank)
		this.renderFluidTooltip(x, y, w, h, mouseX, mouseY, tank)
	}
}