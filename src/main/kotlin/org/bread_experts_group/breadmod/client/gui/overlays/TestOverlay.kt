package org.bread_experts_group.breadmod.client.gui.overlays

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.joml.Vector3f
import java.awt.Color

class TestOverlay : LayeredDraw.Layer {
//	private val camera = Camera()
//	private val target = TestTarget()
	companion object {
		var vector3f = Vector3f()
		var vector3f1 = Vector3f()
		var vector3f2 = Vector3f()
		var vector3f3 = Vector3f()
	}

	override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
		val player = localClient.player ?: return
		if (!player.isHolding(ModItems.WRENCH.asItem())) return
		val vector1 = Companion.vector3f
		val vector2 = Companion.vector3f1
		val vector3 = Companion.vector3f2
		val vector4 = Companion.vector3f3
		guiGraphics.drawString(
			localClient.font,
			"vertex 1: ${vector1.x}, ${vector1.y}, ${vector1.z}",
			0,
			0,
			Color.WHITE.rgb
		)
		guiGraphics.drawString(
			localClient.font,
			"vertex 2: ${vector2.x}, ${vector2.y}, ${vector2.z}",
			0,
			8,
			Color.WHITE.rgb
		)
		guiGraphics.drawString(
			localClient.font,
			"vertex 3: ${vector3.x}, ${vector3.y}, ${vector3.z}",
			0,
			16,
			Color.WHITE.rgb
		)
		guiGraphics.drawString(
			localClient.font,
			"vertex 4: ${vector4.x}, ${vector4.y}, ${vector4.z}",
			0,
			24,
			Color.WHITE.rgb
		)
//		val player = localClient.player ?: return
//		val levelRenderer = localClient.levelRenderer
//		if (player.isHolding(ModItems.WRENCH.asItem())) {
//			val level = localClient.level ?: return
//			val forklift = level.getEntity(3) ?: return
//			this.camera.setup(
//				level,
//				forklift,
//				true,
//				false,
//				deltaTracker.gameTimeDeltaTicks
//			)
//			val gameRenderer = localClient.gameRenderer
//			val matrix4f = gameRenderer.getProjectionMatrix(70.0)
//			val matrix4f1 = Matrix4f().rotate(this.camera.rotation().conjugate(Quaternionf()))
//			levelRenderer.prepareCullFrustum(
//				this.camera.position,
//				matrix4f1,
//				gameRenderer.getProjectionMatrix(70.0)
//			)
//			RenderSystem.viewport(0, 0, 1500, 1000)
//			levelRenderer.renderLevel(
//				deltaTracker,
//				false,
//				this.camera,
//				gameRenderer,
//				gameRenderer.lightTexture(),
//				matrix4f1,
//				matrix4f
//			)
//
//			this.target.bindWrite(true)
//		}
	}

//	class TestTarget : MainTarget(1500, 1000)
}