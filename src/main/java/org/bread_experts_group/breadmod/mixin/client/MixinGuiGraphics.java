package org.bread_experts_group.breadmod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.FormattingKt;
import org.bread_experts_group.breadmod.registry.component.ModDataComponents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {
	@Shadow
	@Final
	private PoseStack pose;

	@Shadow
	public abstract int drawString(Font font, @Nullable String text, int x, int y, int color, boolean dropShadow);

	@Shadow
	public abstract void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int color);

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	DecimalFormat breadmod$decimalFormatter = new DecimalFormat("#,###.##");

	@SuppressWarnings("LongLine")
	@Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), cancellable = true)
	private void renderItemDecorations(
			Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci
	) {
		if (!stack.isEmpty()) {
			this.pose.pushPose();
			BigDecimal read = stack.get(ModDataComponents.INSTANCE.getEXPANSIBLE_ITEM_STACK());
			if (stack.getCount() != 1 && text == null && read != null) {
				String[] split = FormattingKt.formatMetric(read.doubleValue(), 2).split(" ");
				String s = this.breadmod$decimalFormatter.format(split[0]) + split[1];

				this.pose.translate(0.0F, 0.0F, 200.0F);
				this.drawString(
						font,
						s,
						x + 17 - font.width(s), y + 9,
						Color.CYAN.getRGB(),
						true
				);
				ci.cancel();
			} else {
				this.pose.popPose();
				return;
			}

			if (stack.isBarVisible()) {
				int j = x + 2;
				int k = y + 13;
				this.fill(
						RenderType.guiOverlay(),
						j, k,
						j + 13, k + 2,
						-16777216
				);
				this.fill(
						RenderType.guiOverlay(),
						j, k,
						j + stack.getBarWidth(), k + 1,
						stack.getBarColor() | -16777216
				);
			}

			LocalPlayer localplayer = this.minecraft.player;
			float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(
					stack.getItem(),
					this.minecraft.getTimer().getGameTimeDeltaPartialTick(true)
			);
			if (f > 0.0F) {
				int i1 = y + Mth.floor(16.0F * (1.0F - f));
				int j1 = i1 + Mth.ceil(16.0F * f);
				this.fill(RenderType.guiOverlay(), x, i1, x + 16, j1, Integer.MAX_VALUE);
			}

			this.pose.popPose();
//			ItemDecoratorHandler.of(stack).render(this, font, stack, x, y);
		}
	}
}
