package bread.mod.breadmod.mixin.client;

import bread.mod.breadmod.util.render.RenderGeneralKt;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
}
