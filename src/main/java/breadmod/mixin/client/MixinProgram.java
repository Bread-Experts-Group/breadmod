package breadmod.mixin.client;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import kotlin.Unit;
import kotlin.jvm.functions.Function5;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.Map;

@Mixin(com.mojang.blaze3d.shaders.Program.class)
abstract class MixinProgram {
    @Inject(
            method = "compileShaderInternal",
            at = @At(value = "INVOKE", target = "com.mojang.blaze3d.platform.GlStateManager.glCompileShader(I)V")
    )
    private static void attachBreadModProgramAdditionals(
            Program.Type pType,
            String pName,
            InputStream pShaderData,
            String pSourceName,
            GlslPreprocessor pPreprocessor,
            CallbackInfoReturnable<Integer> cir
    ) {
        final Map<String, Function5<Program.Type, String, InputStream, String, GlslPreprocessor, Unit>> precomps =
                breadmod.util.render.RenderGeneralKt.getShaderPreCompilation();
        if (precomps.containsKey(pName))
            precomps.remove(pName).invoke(pType, pName, pShaderData, pSourceName, pPreprocessor);
    }
}
