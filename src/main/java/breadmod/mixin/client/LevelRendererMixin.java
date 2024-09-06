package breadmod.mixin.client;

import breadmod.ClientForgeEventBus;
import breadmod.util.render.RenderGeneralKt;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
    @Redirect(method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 changeSkyColor(ClientLevel instance, Vec3 f7, float f10) {
        final Vec3 skyColor = instance.getSkyColor(f7, f10);
        final float redness = ClientForgeEventBus.INSTANCE.getRedness();
        final float rednessClamped = Mth.clamp(redness, 0f, 1f);
        if (RenderGeneralKt.getSkyColorMixinActive()) {
            return new Vec3(
                    skyColor.x + rednessClamped,
                    skyColor.y - rednessClamped,
                    skyColor.z - rednessClamped
            );
        } else return instance.getSkyColor(f7, f10);
    }
}
