package breadmod.mixin.client;

import breadmod.util.render.RenderGeneralKt;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// todo red sky hue shifting back to normal colors, repeating (probably use sin for this)
@Mixin(ClientLevel.class)
class ClientLevelMixin {

    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void getSkyColor(Vec3 pPos, float pPartialTick, CallbackInfoReturnable<Vec3> cir) {
        if (RenderGeneralKt.getSkyColorMixinActive()) {
            cir.setReturnValue(new Vec3(1f, 0f, 0f));
        }
    }

    @Inject(method = "getSkyDarken", at = @At("HEAD"), cancellable = true)
    private void getSkyDarken(float pPartialTick, CallbackInfoReturnable<Float> cir) {
        if (RenderGeneralKt.getSkyColorMixinActive()) {
            cir.setReturnValue(1f);
        }
    }
}
