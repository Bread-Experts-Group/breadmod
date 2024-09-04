package breadmod.mixin.client;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DirectoryLister.class)
interface IAccessorDirectoryLister {
    @Accessor("sourcePath")
    String getSourcePath();

    @Accessor("idPrefix")
    String getIDPrefix();
}
