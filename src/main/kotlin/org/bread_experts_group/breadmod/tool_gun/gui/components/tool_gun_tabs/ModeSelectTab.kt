package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.components.ScrollingContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.TabButton
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.network.serverbound.ToolGunModeChangePacket
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModeWidget
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen
import org.bread_experts_group.breadmod.util.Color

class ModeSelectTab(
	screen: ToolGunScreen,
	stack: ItemStack
) : ToolGunScreenTab("mode_select", Color.GRAY, screen, stack) {
	override fun getTabButton(): TabButton = TabButton(
		Component.literal("Modes"),
		Color.RED, Color.GRAY,
		this
	)

	private var currentModeWidget: ModeWidget = ModeWidget.noWidget
	private val modeWidgets: MutableList<ModeWidget> = mutableListOf()
	private val modeButton: GenericButton = GenericButton(0, 0, 80, 20, "Change Mode") { _, _ ->
		val index = Registry.toolGunModes.keys.indexOf(this.currentModeWidget.id)
		PacketDistributor.sendToServer(ToolGunModeChangePacket(this.currentModeWidget.id, index))
		this.updateModeWidgetSelection(index)
	}

	override fun initContainer() {
		val (_, _, index) = ToolGunData.get(this.stack)
		this.currentModeWidget = ModeWidget.noWidget
		Registry.toolGunModes.forEach { (_, mode) ->
			this.modeWidgets.add(mode.getCustomRenderer().getModeWidget())
		}
		this.addChild(
			"mode_scroller",
			ScrollingContainerWidget(
				this.x + 2,
				this.y + 2,
				117,
				180,
				"mode_holder",
				this.screen,
				400,
				Color.color(10, 10, 10),
				Color.DARK_GRAY
			) { container ->
				val gridList = buildList {
					repeat(5) { y ->
						repeat(3) { x ->
							this.add(container.x + 4 + x * 36 to container.y + 3 + y * 41)
						}
					}
				}
				this.modeWidgets.forEachIndexed { mIndex, modeWidget ->
					container.addChild(
						"mode_widget_$mIndex",
						modeWidget,
						gridList[mIndex].first,
						gridList[mIndex].second
					)
				}
			}
		)
		this.addChild("mode_change_button", this.modeButton, this.x + 160, this.y + 162, isActive = false)
		this.updateModeWidgetSelection(index)
	}

	/**
	 * Update the border color on the widget's mode that is currently active.
	 */
	private fun updateModeWidgetSelection(index: Int): Unit =
		this.getAllWidgets().filterIsInstance<ModeWidget>().forEach {
			it.isSelected = it.id == Registry.toolGunModes.keys.elementAt(index)
		}

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.currentModeWidget = this.screen.focused as? ModeWidget ?: ModeWidget.noWidget
		this.modeButton.active = this.currentModeWidget != ModeWidget.noWidget
		val poseStack = guiGraphics.pose()
		guiGraphics.borderedFill(this.x, this.y, this.x + 243, this.y + 185, Color.RED, Color.GRAY)
		guiGraphics.vLine(this.x + 120, this.y, this.y + 184, Color.RED)
		guiGraphics.fill(
			RenderType.gui(),
			this.x + 123,
			this.y + 5,
			this.x + 240,
			this.y + 72,
			Color.BLACK
		)
		guiGraphics.drawString(
			localClient.font,
			this.currentModeWidget.modeName,
			this.x + 123,
			this.y + 74,
			Color.BLACK,
			false
		)
		guiGraphics.hLine(this.x + 120, this.x + 241, this.y + 83, Color.RED)
		guiGraphics.drawWordWrap(
			localClient.font,
			this.currentModeWidget.modeDescription,
			this.x + 123,
			this.y + 85,
			120,
			Color.BLACK
		)
		poseStack.pushPose()
		poseStack.translate(this.x + 123.85f, this.y + 6.25f, 0f)
		poseStack.scaleFlat(0.135f)
		this.currentModeWidget.previewImage.select({
			TODO("Stretch icon")
		}, {
			it.blit(guiGraphics, 0, 0, uWidth = 854, vHeight = 480)
		})
		poseStack.popPose()
	}
}