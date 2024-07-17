package breadmod.mixin.accessors.client;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DirectoryLister.class)
public interface IAccessorDirectoryLister {
    @Accessor("sourcePath")
    String getSourcePath();

    @Accessor("idPrefix")
    String getIDPrefix();
}
