package breadmod.mixin.client;

import breadmod.mixin.accessors.client.IAccessorDirectoryLister;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectoryLister.class)
abstract class MixinDirectoryLister implements IAccessorDirectoryLister {
    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void run(ResourceManager pResourceManager, SpriteSource.Output pOutput, CallbackInfo ci) {
        final String prefix = "textures/" + this.getSourcePath();
        pResourceManager.listResources(prefix, (location) -> true).forEach((location, resource) -> {
            final String path = location.getPath();
            ResourceLocation translated = location.withPath(
                    path.substring(
                            prefix.length() + 1,
                            // Allow for default processing of PNGs, don't strip extensions of
                            // unknown resources for our purposes
                            path.endsWith(".png") ? path.lastIndexOf('.') : path.length()
                    )
            );

            if(location.getNamespace().equals("breadmod")) System.out.println(translated);
            translated = translated.withPrefix(this.getIDPrefix());
            pOutput.add(translated, resource);
        });
        ci.cancel();
    }
}
