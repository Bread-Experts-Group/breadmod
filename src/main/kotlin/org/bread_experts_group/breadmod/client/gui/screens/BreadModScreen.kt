package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderFluid
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.registry.menu.actual.LambdaSlotItemHandler
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.floatRoundEven
import java.math.BigDecimal
import kotlin.math.roundToInt

abstract class BreadModScreen(
	val menu: BreadModMenu,
	title: Component
) : AbstractContainerScreen<BreadModMenu>(menu, menu.inventory, title), MenuAccess<BreadModMenu> {
	companion object {
		@DataGenerateLanguage(name = "Energy")
		val ENERGY_LABEL: MutableComponent = modTranslatable(path = arrayOf("energy"))
	}

	private fun GuiGraphics.drawFillBox(
		x: Int,
		y: Int,
		w: Int,
		h: Int
	) {
		if (localClient.options.advancedItemTooltips) {
			this.fill(
				this@BreadModScreen.leftPos + x,
				this@BreadModScreen.topPos + y,
				this@BreadModScreen.leftPos + x + w,
				this@BreadModScreen.topPos + y + h,
				0x7F000080
			)
		}
	}

	private fun GuiGraphics.renderEnergyMeter(x: Int, y: Int, h: Int, handler: ExtendedEnergyHandler) {
		val scaled = (handler.bigAmount.divide(handler.bigCapacity, floatRoundEven).toFloat() * h)
			.roundToInt()
		ModGuiElements.SLOT.blitScaled(
			this,
			this@BreadModScreen.leftPos + x,
			this@BreadModScreen.topPos + y,
			18, 49
		)
		ModGuiElements.ENERGY_METER.blit(
			this,
			this@BreadModScreen.leftPos + x + 1,
			this@BreadModScreen.topPos + y + 1 + h - scaled,
			vOffset = 47f - scaled,
			vHeight = scaled
		)
	}

	private fun GuiGraphics.renderEnergyTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		handler: ExtendedEnergyHandler
	) {
		if (this@BreadModScreen.isHovering(x, y, w, h, mouseX, mouseY)) this.renderComponentTooltip(
			this@BreadModScreen.font,
			listOf(
				Companion.ENERGY_LABEL
					.withStyle(ChatFormatting.RED)
					.withStyle(ChatFormatting.ITALIC),
				JadeDrawingCommon.fixedLengthScrollingComponent(
					handler.bigAmount,
					handler.bigCapacity,
					"FE",
					Color.RED
				)
			),
			mouseX.toInt(), mouseY.toInt()
		)
	}

	protected fun GuiGraphics.renderEnergyWithTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double
	) {
		val energyHandler = this@BreadModScreen.menu.entity.getCapability(Capabilities.EnergyStorage.BLOCK)
				as ExtendedEnergyHandler
		this.drawFillBox(x, y, w, h)
		this.renderEnergyMeter(x, y, h, energyHandler)
		this.renderEnergyTooltip(x, y, w, h, mouseX, mouseY, energyHandler)
	}

	private fun GuiGraphics.renderFluidMeter(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		handler: ExtendedFluidHandler,
		tank: Int,
		flowing: Boolean = false
	) {
		ModGuiElements.SLOT.blitScaled(
			this,
			this@BreadModScreen.leftPos + x,
			this@BreadModScreen.topPos + y,
			w + 2,
			h + 2
		)
		val tank = handler.tanks[tank]
		if (tank != null && tank.amount > BigDecimal.ZERO) {
			this.renderFluid(
				(this@BreadModScreen.leftPos + x.toFloat()) + 1,
				(this@BreadModScreen.topPos + y.toFloat()) + 1,
				w,
				h,
				tank,
				flowing
			)
		}
	}

	val big1000: BigDecimal = BigDecimal.valueOf(1000)
	private fun GuiGraphics.renderFluidTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		handler: ExtendedFluidHandler,
		tank: Int
	) {
		val tank = handler.tanks[tank]
		if (tank != null && this@BreadModScreen.isHovering(x, y, w, h, mouseX, mouseY)) {
			val tint = IClientFluidTypeExtensions.of(tank.fluid).tintColor
			this.renderComponentTooltip(
				this@BreadModScreen.font,
				listOf(
					Component.translatable(tank.fluid.fluidType.descriptionId)
						.withColor(tint)
						.withStyle(ChatFormatting.ITALIC),
					JadeDrawingCommon.fixedLengthScrollingComponent(
						tank.amount.divide(this@BreadModScreen.big1000),
						tank.capacity.divide(this@BreadModScreen.big1000),
						"B", tint
					)
				),
				mouseX.toInt(), mouseY.toInt()
			)
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
		val fluidHandler = this@BreadModScreen.menu.entity.getCapability(Capabilities.FluidHandler.BLOCK)
				as ExtendedFluidHandler
		this.drawFillBox(x, y, w, h)
		this.renderFluidMeter(x, y, w, h, fluidHandler, tank)
		this.renderFluidTooltip(x, y, w, h, mouseX, mouseY, fluidHandler, tank)
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
}