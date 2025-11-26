package org.bread_experts_group.breadmod.mixin.client.rendering;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractWidget.class)
abstract class MixinAbstractWidget {
	@Redirect(
			method = "renderScrollingString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIIII)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;enableScissor(IIII)V"
			)
	)
	private static void fixScissor(GuiGraphics instance, int minX, int minY, int maxX, int maxY) {
		instance.enableScissor(minX, minY, maxX, maxY + 1);
		// silly mojang cutting off the shadow text by one pixel
	}
}