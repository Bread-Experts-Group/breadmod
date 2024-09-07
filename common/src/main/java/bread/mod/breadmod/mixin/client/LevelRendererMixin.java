package bread.mod.breadmod.mixin.client;

import bread.mod.breadmod.client.gui.WarOverlay;
import bread.mod.breadmod.util.render.RenderGeneralKt;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.platform.Platform;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Overrides the sky color when the war timer is active
 * @author Logan McLean
 * @since 1.0.0
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Redirect(method = "renderSky",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
        )
    )
    private Vec3 changeSkyColor(ClientLevel instance, Vec3 pos, float partialTick) {
        final Vec3 skyColor = instance.getSkyColor(pos, partialTick);
        final float redness = RenderGeneralKt.getRedness();
        final float rednessClamped = Mth.clamp(redness, 0f, 1f);
        if (RenderGeneralKt.getSkyColorMixinActive()) {
            return new Vec3(
                    skyColor.x + rednessClamped,
                    skyColor.y - rednessClamped,
                    skyColor.z - rednessClamped
            );
        } else return instance.getSkyColor(pos, partialTick);
    }

    @Inject(method = "renderSky", at = @At("TAIL"))
    private void renderSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera,
                           boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci
    ) {
        if (WarOverlay.Companion.getTimerActive()) {
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(frustumMatrix);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            long millis = Util.getMillis();
            float redness = RenderGeneralKt.getRedness();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.enableBlend();
            poseStack.pushPose();
            Matrix4f matrix = poseStack.last().pose();
            bufferBuilder.addVertex(matrix, 0f, 100f, 0f).setColor(0.9f, 0f, 0.1f, Mth.clamp(redness - 0.2f, 0f, 1f));

            for (int i = 0; i <= 16; ++i) {
                float f1 = i * ((float) Math.PI * 2f) / 16f;
                float f2 = (float) Math.sin(f1);
                float f3 = (float) Math.cos(f1);
                bufferBuilder.addVertex(matrix, f2, -1f, -f3).setColor(0.9f, 0f, 0.1f, Mth.clamp(redness - 0.2f, 0f, 1f));
            }

            float[] shaderFogColor = RenderSystem.getShaderFogColor();
            RenderSystem.setShaderFogColor(
                    shaderFogColor[0] + redness,
                    shaderFogColor[1] - redness,
                    shaderFogColor[2] - redness,
                    1f
            );

            FogRenderer.setupFog(
                    camera,
                    FogRenderer.FogMode.FOG_SKY,
                    256f,
                    true,
                    partialTick
            );
            FogRenderer.setupFog(
                    camera,
                    FogRenderer.FogMode.FOG_TERRAIN,
                    Math.max(256f, 32f),
                    true,
                    partialTick
            );

            RenderGeneralKt.setRedness(Math.clamp((float) (Math.sin((float) millis / 1800) + 1) / 2, 0f, 1f));
            RenderGeneralKt.setSkyColorMixinActive(true);

            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            RenderSystem.disableBlend();
            poseStack.popPose();
        } else {
            RenderGeneralKt.setRedness(0f);
            RenderGeneralKt.setSkyColorMixinActive(false);
        }
    }
}
