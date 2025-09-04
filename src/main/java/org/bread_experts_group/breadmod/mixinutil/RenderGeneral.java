package org.bread_experts_group.breadmod.mixinutil;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.bread_experts_group.breadmod.registry.shader.ModPostChains;

public enum RenderGeneral {
	;

	public static void renderBlend(DeltaTracker deltaTracker) {
		var bloom = ModPostChains.INSTANCE.getBloom();
		if (ModPostChains.INSTANCE.getReady()) {
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
			RenderSystem.resetTextureMatrix();
			ModPostChains.INSTANCE.getBloom().process(deltaTracker.getGameTimeDeltaTicks());
			var target = bloom.getTempTarget("emissive");
			target.clear(true);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
		}
	}
}
