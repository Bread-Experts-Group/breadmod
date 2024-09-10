package bread.mod.breadmod.mixin.client;

import bread.mod.breadmod.client.gui.WarOverlay;
import bread.mod.breadmod.util.render.RenderGeneralKt;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
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

@SuppressWarnings({
        "DuplicateStringLiteralInspection",
        "HardcodedFileSeparator",
        "FeatureEnvy",
        "MethodWithTooManyParameters",
        "OverlyLongMethod"
})
@Mixin(LevelRenderer.class)
class LevelRendererMixin {
    @SuppressWarnings("LongLine")
    @Redirect(method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 changeSkyColor(final ClientLevel instance, final Vec3 pos, final float partialTick) {
        final Vec3 skyColor = instance.getSkyColor(pos, partialTick);
        final float redness = RenderGeneralKt.getRedness();
        final float rednessClamped = Mth.clamp(redness, 0.0f, 1.0f);
        if (RenderGeneralKt.getSkyColorMixinActive()) {
            return new Vec3(
                    skyColor.x + (double) rednessClamped,
                    skyColor.y - (double) rednessClamped,
                    skyColor.z - (double) rednessClamped
            );
        } else return instance.getSkyColor(pos, partialTick);
    }

    @SuppressWarnings("MagicNumber")
    @Inject(method = "renderSky", at = @At("TAIL"))
    private void renderSky(final Matrix4f frustumMatrix, final Matrix4f projectionMatrix, final float partialTick,
                           final Camera camera, final boolean isFoggy, final Runnable skyFogSetup, final CallbackInfo ci
    ) {
        if (WarOverlay.Companion.getTimerActive()) {
            final PoseStack poseStack = new PoseStack();
            poseStack.mulPose(frustumMatrix);
            final Tesselator tesselator = Tesselator.getInstance();
            final BufferBuilder bufferBuilder = tesselator.begin(
                    VertexFormat.Mode.TRIANGLE_FAN,
                    DefaultVertexFormat.POSITION_COLOR
            );
            final long millis = Util.getMillis();
            final float redness = RenderGeneralKt.getRedness();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableBlend();
            poseStack.pushPose();
            final PoseStack.Pose lastPose = poseStack.last();
            final Matrix4f matrix = lastPose.pose();
            final float clampedRedness = Mth.clamp(redness - 0.2f, 0.0f, 1.0f);
            bufferBuilder
                    .addVertex(matrix, 0.0f, 100.0f, 0.0f)
                    .setColor(0.9f, 0.0f, 0.1f, clampedRedness);

            for (int i = 0; i <= 16; ++i) {
                final float f1 = (float) i * ((float) Math.PI * 2.0f) / 16.0f;
                final float f2 = (float) Math.sin((double) f1);
                final float f3 = (float) Math.cos((double) f1);
                bufferBuilder
                        .addVertex(matrix, f2, -1.0f, -f3)
                        .setColor(0.9f, 0.0f, 0.1f, clampedRedness);
            }

            final float[] shaderFogColor = RenderSystem.getShaderFogColor();
            RenderSystem.setShaderFogColor(
                    shaderFogColor[0] + redness,
                    shaderFogColor[1] - redness,
                    shaderFogColor[2] - redness,
                    1.0f
            );

            FogRenderer.setupFog(
                    camera,
                    FogRenderer.FogMode.FOG_SKY,
                    256.0f,
                    true,
                    partialTick
            );

            final float limitedDistance = Math.max(256.0f, 32.0f);
            FogRenderer.setupFog(
                    camera,
                    FogRenderer.FogMode.FOG_TERRAIN,
                    limitedDistance,
                    true,
                    partialTick
            );

            final double sin = Math.sin((double) ((float) millis / 1800.0F));
            final float rednessClamped = Math.clamp((float) (sin + 1.0) / 2.0f, 0.0f, 1.0f);
            RenderGeneralKt.setRedness(rednessClamped);
            RenderGeneralKt.setSkyColorMixinActive(true);

            final MeshData meshData = bufferBuilder.buildOrThrow();
            BufferUploader.drawWithShader(meshData);
            RenderSystem.disableBlend();
            poseStack.popPose();
        } else {
            RenderGeneralKt.setRedness(0.0f);
            RenderGeneralKt.setSkyColorMixinActive(false);
        }
    }
}
