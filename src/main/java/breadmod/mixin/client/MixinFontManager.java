package breadmod.mixin.client;

import breadmod.util.ModFonts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * This mixin is not seriously used by us at the moment.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Mixin(net.minecraft.client.gui.font.FontManager.class)
abstract class MixinFontManager {
    @Final
    @Shadow
    private Map<ResourceLocation, FontSet> fontSets;

    @Final
    @Shadow
    private FontSet missingFontSet;

    @Invoker("getActualId")
    abstract ResourceLocation iGetID(ResourceLocation pId);

    @Unique
    public boolean breadmod$useMixin = false;

    @SuppressWarnings("NewExpressionSideOnly")
    @Inject(method = "createFont", at = @At("HEAD"), cancellable = true)
    private void createFont(final CallbackInfoReturnable<? super Font> cir) {
        if (breadmod$useMixin) {
            cir.setReturnValue(new Font(
                    (location) -> {
                        final Style publicSansRegular = ModFonts.getPUBLIC_SANS_REGULAR();
                        final ResourceLocation font = publicSansRegular.getFont();
                        final ResourceLocation key = this.iGetID(font);
                        return this.fontSets.getOrDefault(key, this.missingFontSet);
                    },
                    false
            ));
        }
    }
}
