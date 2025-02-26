package org.bread_experts_group.breadmod.client.screen

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.renderFluid
import org.bread_experts_group.breadmod.compat.lookingat.jade.JadeDrawingCommon
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.EnergyBearingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.AbstractModContainerMenu
import java.math.BigDecimal

abstract class AbstractModContainerScreen<T : AbstractModContainerMenu<BE>, BE : BreadModBlockEntity<BE>>(
	menu: T,
	inventory: Inventory,
	title: Component
) : AbstractContainerScreen<T>(menu, inventory, title) {
	companion object {
		// TODO: Isolate this to just a power meter
		val baseTexture: ResourceLocation = modLocation("textures", "gui", "container", "dough_machine.png")

		@DataGenerateLanguage("en_us", "Energy")
		val energyLabel: MutableComponent = modTranslatable(path = arrayOf("energy"))
	}

	protected fun GuiGraphics.renderEnergyMeter(x: Int, y: Int, w: Int, h: Int, cell: Int? = null) {
		val energyHandler = (this@AbstractModContainerScreen.menu.parent as EnergyBearingBlockEntity).energyHandler
		val sap = if (cell == null) energyHandler else energyHandler.getUnit(cell)
		val scaled = sap.capacity?.let { ((sap.amount.divide(it)).toFloat() * h).toInt() } ?: 0
		this.blit(
			Companion.baseTexture,
			this@AbstractModContainerScreen.leftPos + x,
			this@AbstractModContainerScreen.topPos + y + h - scaled,
			176,
			64 - scaled,
			w,
			h
		)
	}

	protected fun GuiGraphics.renderEnergyTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		cell: Int? = null
	) {
		if (this@AbstractModContainerScreen.isHovering(x, y, w, h, mouseX, mouseY)) {
			val energyHandler = (this@AbstractModContainerScreen.menu.parent as EnergyBearingBlockEntity).energyHandler
			val sap = if (cell == null) energyHandler else energyHandler.getUnit(cell)
			this.renderComponentTooltip(
				this@AbstractModContainerScreen.font,
				listOf(
					Companion.energyLabel
						.withStyle(ChatFormatting.RED)
						.withStyle(ChatFormatting.ITALIC),
					JadeDrawingCommon.fixedLengthScrollingComponent(
						sap.amount,
						sap.capacity,
						"FE",
						tint = ChatFormatting.RED.color ?: return
					)
				),
				mouseX.toInt(), mouseY.toInt()
			)
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
				this@AbstractModContainerScreen.leftPos + x,
				this@AbstractModContainerScreen.topPos + y,
				this@AbstractModContainerScreen.leftPos + x + w,
				this@AbstractModContainerScreen.topPos + y + h,
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
		this.renderEnergyMeter(x, y, w, h, cell)
		this.renderEnergyTooltip(x, y, w, h, mouseX, mouseY, cell)
	}

	protected fun GuiGraphics.renderFluidMeter(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		tank: Int,
		flowing: Boolean = false
	) {
		val fluidHandler = (this@AbstractModContainerScreen.menu.parent as FluidBearingBlockEntity).fluidHandler
		val tank = fluidHandler.getUnit(tank)
		if (tank.amount > BigDecimal.ZERO) {
			this.renderFluid(
				this@AbstractModContainerScreen.leftPos + x.toFloat(),
				(this@AbstractModContainerScreen.topPos + y.toFloat()),
				w,
				h,
				tank,
				flowing
			)
		}
	}

	protected fun GuiGraphics.renderFluidTooltip(
		x: Int,
		y: Int,
		w: Int,
		h: Int,
		mouseX: Double,
		mouseY: Double,
		tank: Int
	) {
		if (this@AbstractModContainerScreen.isHovering(x, y, w, h, mouseX, mouseY)) {
			val fluidHandler = (this@AbstractModContainerScreen.menu.parent as FluidBearingBlockEntity).fluidHandler
			val tank = fluidHandler.getUnit(tank)
			val tint = IClientFluidTypeExtensions.of(tank.fluid).tintColor
			this.renderComponentTooltip(
				this@AbstractModContainerScreen.font,
				listOf(
					Component.translatable(tank.fluidType.descriptionId)
						.withStyle(Style.EMPTY.withColor(tint))
						.withStyle(ChatFormatting.ITALIC),
					JadeDrawingCommon.fixedLengthScrollingComponent(
						tank.amount, tank.capacity,
						"B", -1,
						tint
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
		this.drawFillBox(x, y, w, h)
		this.renderFluidMeter(x, y, w, h, tank)
		this.renderFluidTooltip(x, y, w, h, mouseX, mouseY, tank)
	}
}