package org.bread_experts_group.breadmod.tool_gun.gui.screen

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.screens.HoldScreen
import org.bread_experts_group.breadmod.client.render.fillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_tabs.BlockTab
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_tabs.EntityTab
import org.bread_experts_group.breadmod.tool_gun.sound.KSPSoundInstance
import org.bread_experts_group.breadmod.util.Color

class CreatorScreen(
	menu: CreatorMenu,
	inventory: Inventory,
	title: Component
) : HoldScreen<CreatorMenu>(menu, inventory, title, InputConstants.KEY_F) {
	companion object {
		val bgSound: KSPSoundInstance = KSPSoundInstance()
	}

	var lastTick: Int = 0

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
//		guiGraphics.borderedFillPositioned(this.leftPos, this.topPos, 256, 256, Color.GRAY, Color.BLACK)
		RenderSystem.enableBlend()
		guiGraphics.fillPositioned(this.leftPos, this.topPos + 12, 256, 244, Color.color(a = 100))
		RenderSystem.disableBlend()
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
	}

	override val shouldClose: Boolean
		get() = super.shouldClose

	fun getGuiTicks(): Int = localClient.gui.guiTicks
	fun getData(): ToolGunData = this.menu.data

	fun getLeftPos(): Int = this.leftPos
	fun getTopPos(): Int = this.topPos

	override fun init() {
		this.leftPos = (this.width - 256) / 2
		this.topPos = (this.height - 256) / 2
		this.addRenderableWidget(BlockTab(this, this.menu.inventory.player.level()))
		this.addRenderableWidget(EntityTab(this, this.menu.inventory.player.level()))
		this.addRenderableWidget(GenericButton(this.leftPos, this.topPos, 44, 12, "BLOCK") {
			(this.children()[0] as ContainerWidget<*>).enable()
			(this.children()[1] as ContainerWidget<*>).disable()
		})
		this.addRenderableWidget(GenericButton(this.leftPos + 44, this.topPos, 44, 12, "ENTITY") {
			(this.children()[0] as ContainerWidget<*>).disable()
			(this.children()[1] as ContainerWidget<*>).enable()
		})
		Companion.bgSound.shouldPlay = true
		if (!localClient.soundManager.isActive(Companion.bgSound)) localClient.soundManager.play(Companion.bgSound)
	}

	override fun onClose() {
		Companion.bgSound.shouldPlay = false
		super.onClose()
	}
}