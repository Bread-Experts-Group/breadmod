package breadmod.mixin.client;

import breadmod.mixin.accessors.client.IAccessorProgramType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mixin(com.mojang.blaze3d.shaders.Program.class)
abstract class MixinProgram {
    @SuppressWarnings("MethodWithTooManyParameters")
    @Inject(
            method = "compileShaderInternal",
            at = @At("HEAD"),
            cancellable = true)
    private static void getItemModel(
            final Program.Type pType,
            final String pName,
            final InputStream pShaderData,
            final String pSourceName,
            final GlslPreprocessor pPreprocessor,
            final CallbackInfoReturnable<? super Integer> cir
    ) throws IOException {
        final String s = IOUtils.toString(pShaderData, StandardCharsets.UTF_8);
        final String name = pType.getName();
        if (s == null) {
            final String formattedError = String.format(
                    "[BreadMod Shader Extensions] %s: no program",
                    name
            );

            throw new IOException(formattedError);
        } else {
            final IAccessorProgramType programTypeAccess = IAccessorProgramType.class.cast(pType);
            final int glType = programTypeAccess.iGetGLType();
            final int i = GlStateManager.glCreateShader(glType);
            final List<String> process = pPreprocessor.process(s);

            GlStateManager.glShaderSource(i, process);
            GlStateManager.glCompileShader(i);

            if (GlStateManager.glGetShaderi(i, 35713) == 0) {
                final String log = GlStateManager.glGetShaderInfoLog(i, 32768);
                final String trimmedLog = StringUtils.trim(log);

                final String formattedError = String.format(
                        "[BreadMod Shader Extensions] Couldn't compile %s program (%s, %s) : %s",
                        name, pSourceName, pName, trimmedLog
                );

                throw new IOException(formattedError);
            } else cir.setReturnValue(i);
        }
    }
}
