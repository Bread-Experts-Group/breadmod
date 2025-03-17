package org.bread_experts_group.breadmod.client.gui.overlays

import com.mojang.blaze3d.pipeline.MainTarget
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.joml.Matrix4f
import org.joml.Quaternionf

class TestOverlay : LayeredDraw.Layer {
	private val camera = Camera()
	private val target = TestTarget()

	override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
		val player = localClient.player ?: return
		val levelRenderer = localClient.levelRenderer
		if (player.isHolding(ModItems.WRENCH.asItem())) {
			val level = localClient.level ?: return
			val forklift = level.getEntity(3) ?: return
			this.camera.setup(
				level,
				forklift,
				true,
				false,
				deltaTracker.gameTimeDeltaTicks
			)
			val gameRenderer = localClient.gameRenderer
			val matrix4f = gameRenderer.getProjectionMatrix(70.0)
			val matrix4f1 = Matrix4f().rotate(this.camera.rotation().conjugate(Quaternionf()))
			levelRenderer.prepareCullFrustum(
				this.camera.position,
				matrix4f1,
				gameRenderer.getProjectionMatrix(70.0)
			)
			RenderSystem.viewport(0, 0, 1500, 1000)
			levelRenderer.renderLevel(
				deltaTracker,
				false,
				this.camera,
				gameRenderer,
				gameRenderer.lightTexture(),
				matrix4f1,
				matrix4f
			)

			this.target.bindWrite(true)
		}
	}

	class TestTarget : MainTarget(1500, 1000)
}