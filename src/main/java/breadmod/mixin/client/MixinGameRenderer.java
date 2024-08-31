package breadmod.mixin.client;

import breadmod.util.render.PostProcessingRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.renderer.GameRenderer.class)
abstract class MixinGameRenderer {
    @SuppressWarnings({"MethodMayBeStatic", "ChainedMethodCall", "NestedMethodCall"})
    @Inject(
            method = "render", at = @At(value = "INVOKE",
            target = "net.minecraft.client.renderer.LevelRenderer.doEntityOutline()V")
    )
    private void processRegistryShaders(
            final float pPartialTicks,
            final long pNanoTime,
            final boolean pRenderLevel,
            final CallbackInfo ci
    ) {
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();

        PostProcessingRegistry.INSTANCE.getVisibleShaders().forEach(
                (module, shaders) -> shaders.forEach(
                        (name, chain) -> chain.process(pPartialTicks)
                )
        );
    }
}