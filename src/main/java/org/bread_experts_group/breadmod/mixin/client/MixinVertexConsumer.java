package org.bread_experts_group.breadmod.mixin.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Mixin(VertexConsumer.class)
public interface MixinVertexConsumer extends InvokerIVertexConsumerExtension {
    @Invoker("addVertex")
    void invokeAddVertex(
            float x,
            float y,
            float z,
            int color,
            float u,
            float v,
            int packedOverlay,
            int packedLight,
            float normalX,
            float normalY,
            float normalZ
    );

    @SuppressWarnings("LongLine")
    @Inject(
            method = "putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    default void putBulkData(
            PoseStack.Pose pose, BakedQuad quad,
            float[] brightness,
            float red, float green, float blue, float alpha,
            int[] lightmap,
            int packedOverlay,
            boolean readAlpha,
            CallbackInfo ci
    ) {
        int[] aint = quad.getVertices();
        Vec3i vec3i = quad.getDirection().getNormal();
        Matrix4f matrix4f = pose.pose();
        Vector3f normal = pose.transformNormal(
                (float) vec3i.getX(),
                (float) vec3i.getY(),
                (float) vec3i.getZ(),
                new Vector3f()
        );
        int j = aint.length / 8;
        int k = (int) (alpha * 255.0F);

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for (int l = 0; l < j; l++) {
                intbuffer.clear();
                intbuffer.put(aint, l * 8, 8);
                float x = bytebuffer.getFloat(0);
                float y = bytebuffer.getFloat(4);
                float z = bytebuffer.getFloat(8);
                float r, g, b;
                int localLight = lightmap[l];
                float localBrightness = brightness[l];

                if (readAlpha) {
                    float f6 = (float) (bytebuffer.get(12) & 0xFF);
                    float f7 = (float) (bytebuffer.get(13) & 0xFF);
                    float f8 = (float) (bytebuffer.get(14) & 0xFF);
                    r = f6 * localBrightness * red;
                    g = f7 * localBrightness * green;
                    b = f8 * localBrightness * blue;
                } else {
                    r = localBrightness * red * 255.0F;
                    g = localBrightness * green * 255.0F;
                    b = localBrightness * blue * 255.0F;
                }

                if ((localLight & 0x80000000) != 0) {
                    r *= ((float) (localLight >> 16 & 0xFF)) / 255.0F;
                    g *= ((float) (localLight >> 8 & 0xFF)) / 255.0F;
                    b *= ((float) (localLight & 0xFF)) / 255.0F;
                    localLight = 0x00F000F0; //((int) (Math.max(1.0F, r + g + b) * 15.0F)) << 4;
                }

                int vertexAlpha = readAlpha ? (int) ((alpha * (float) (bytebuffer.get(15) & 255) / 255.0F) * 255) : k;
                int colorPacked = FastColor.ARGB32.color(vertexAlpha, (int) r, (int) g, (int) b);
                int packedLight = invokeApplyBakedLighting(localLight, bytebuffer);
                float u = bytebuffer.getFloat(16);
                float v = bytebuffer.getFloat(20);
                Vector3f position = matrix4f.transformPosition(x, y, z, new Vector3f());
                invokeApplyBakedNormals(normal, bytebuffer, pose.normal());
                invokeAddVertex(
                        position.x(), position.y(), position.z(),
                        colorPacked, u, v,
                        packedOverlay,
                        packedLight,
                        normal.x(), normal.y(), normal.z()
                );
            }
        }
        ci.cancel();
    }
}
