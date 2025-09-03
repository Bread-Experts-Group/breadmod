package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.data

import net.minecraft.client.gui.GuiGraphics
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.BlockTab
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.Color

class CapabilityDataWidget<T>(
	screen: CreatorScreen,
	private val container: BlockTab,
	x: Int,
	y: Int,
	private val handler: T
) : ContainerWidget<CreatorScreen>(
	x,
	y,
	140,
	40,
	"capability_widget",
	screen
) {
	private val handlerName: String
		get() = when (this.handler) {
			is IEnergyStorage -> "energy handler"
			is IFluidHandler  -> "fluid handler"
			is IItemHandler   -> "item handler"
			else              -> throw UnsupportedOperationException()
		}

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.LIGHT_GRAY, Color.GRAY)
		guiGraphics.drawString(
			localClient.font,
			this.handlerName,
			this.x + 2,
			this.y + 2,
			Color.WHITE
		)
		when (this.handler) {
			is IEnergyStorage -> {
				val handler = this.container.getCapability(Capabilities.EnergyStorage.BLOCK) ?: return
				guiGraphics.drawString(
					localClient.font,
					handler.energyStored.toString(),
					this.x + 2,
					this.y + 12,
					Color.WHITE
				)
			}
			is IFluidHandler  -> {
				val handler = this.container.getCapability(Capabilities.FluidHandler.BLOCK) ?: return
			}
			is IItemHandler   -> {
				val handler = this.container.getCapability(Capabilities.ItemHandler.BLOCK) ?: return
			}
			else              -> throw UnsupportedOperationException()
		}
	}

	override fun initContainer() {
		when (this.handler) {
			is IEnergyStorage -> {
				val handler = this.container.getCapability(Capabilities.EnergyStorage.BLOCK) ?: return
				this.addChild(
					"energy_button",
					GenericButton(this.x + 2, this.y + 20, 10, 10, "+") { _, _ ->
						handler.receiveEnergy(10000, false)
					}
				)
			}
			is IFluidHandler  -> {}
			is IItemHandler   -> {}
			else              -> throw UnsupportedOperationException()
		}
	}
}