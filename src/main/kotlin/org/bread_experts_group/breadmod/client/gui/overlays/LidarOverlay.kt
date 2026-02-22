package org.bread_experts_group.breadmod.client.gui.overlays

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlot
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.blitWithColor
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.pushPop
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.registry.KeyMappings
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.util.Color

class LidarOverlay : LayeredDraw.Layer {
	private val bstScanLoc: ResourceLocation = modLocation("textures", "gui", "hud", "burst_scan.png")
	private val mapLoc: ResourceLocation = modLocation("textures", "gui", "hud", "map.png")

	override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
		val player = localClient.player ?: return
		if (!(player.getItemBySlot(EquipmentSlot.HEAD)
				.`is`(ModItems.LIDAR_HELMET) && player.isHolding(ModItems.LIDAR_GUN.get()))
		) return
		val pose = guiGraphics.pose()
		val color = Color.color(255, 250)
		val height = localClient.window.guiScaledHeight
		val bstKey = KeyMappings.lidarBurstScan.key.displayName
		val mapKey = KeyMappings.lidarMap.key.displayName

		guiGraphics.blitWithColor(this.bstScanLoc, 48, 16, 5, height - 20, color)
		pose.pushPop { poseStack ->
			poseStack.translate(10f, height - 16f, 0f)
			poseStack.scaleFlat(1.1f)
			guiGraphics.drawString(localClient.font, bstKey, 0, 0, color)
		}
		guiGraphics.blitWithColor(this.mapLoc, 48, 16, 60, height - 20, color)
		pose.pushPop { poseStack ->
			poseStack.translate(65f, height - 17f, 0f)
			poseStack.scaleFlat(1.4f)
			guiGraphics.drawString(localClient.font, mapKey, 0, 0, Color.BLACK, false)
		}
	}
}