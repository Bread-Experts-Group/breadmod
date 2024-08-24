package breadmod.mixin.accessors.client;

import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin accessor interface for {@link Program.Type}
 */
@FunctionalInterface
@Mixin(Program.Type.class)
public interface IAccessorProgramType {
    /**
     * @return The OpenGL type of this {@link Program.Type}
     */
    @Accessor("glType")
    int iGetGLType();
}