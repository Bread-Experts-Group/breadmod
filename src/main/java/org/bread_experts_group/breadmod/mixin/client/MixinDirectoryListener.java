package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DirectoryLister.class)
abstract class MixinDirectoryListener implements IAccessorDirectoryLister {
	@Inject(method = "run", at = @At("HEAD"), cancellable = true)
	private void run(final ResourceManager resourceManager, final SpriteSource.Output output, final CallbackInfo ci) {
		final String prefix = "textures/" + this.getSourcePath();
		final Map<ResourceLocation, Resource> resourceLocationResourceMap =
				resourceManager.listResources(prefix, (location) -> true);
		resourceLocationResourceMap.forEach((location, resource) -> {
			final String path = location.getPath();
			ResourceLocation translated = location.withPath(
					path.substring(
							prefix.length() + 1,
							// Allow for default processing of PNGs, don't strip extensions of
							// unknown resources for our purposes
							path.endsWith(".png") ? path.lastIndexOf('.') : path.length()
					)
			);

			translated = translated.withPrefix(this.getIDPrefix());
			output.add(translated, resource);
		});
		ci.cancel();
	}
}
