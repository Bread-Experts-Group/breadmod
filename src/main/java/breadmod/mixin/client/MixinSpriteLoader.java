package breadmod.mixin.client;

import breadmod.ModMain;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Mixin(SpriteLoader.class)
abstract class MixinSpriteLoader {
    @Unique
    private final static Logger breadmod$LOGGER = ModMain.INSTANCE.getLOGGER();

    @Inject(
            method = "loadSprite",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
                    ordinal = 1
            ),
            cancellable = true)
    private static void loadSprite(ResourceLocation pLocation, Resource pResource, CallbackInfoReturnable<SpriteContents> cir) {
        if(pLocation.getPath().endsWith(".gpg")) {
            InputStream resourceStream = null;
            Path encryptedPath = null;

            try {
                encryptedPath = Files.createTempFile(String.valueOf(System.currentTimeMillis()), null);

                resourceStream = pResource.open();
                resourceStream.transferTo(Files.newOutputStream(encryptedPath));

                ProcessBuilder pb = new ProcessBuilder(
                        "gpg", "--decrypt", encryptedPath.toAbsolutePath().toString());
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                Process p = pb.start();
                p.waitFor(20, TimeUnit.SECONDS);

                BufferedImage readImage = ImageIO.read(p.getInputStream());
                if(readImage != null) {
                    NativeImage img = new NativeImage(readImage.getWidth(), readImage.getHeight(), true);
                    for (int x = 0; x < readImage.getWidth(); x++) {
                        for (int y = 0; y < readImage.getHeight(); y++) {
                            int rgb = readImage.getRGB(x, y);
                            img.setPixelRGBA(
                                    x, y,
                                    0xFF000000 | ((rgb << 16) & 0xFF) | (rgb & 0xFF) | (rgb >> 16) & 0xFF
                            );
                        }
                    }

                    breadmod$LOGGER.info("Decrypted and loaded GPG sprite: {}", pLocation);
                    cir.setReturnValue(new SpriteContents(
                            new ResourceLocation("breadmod", "meow"),
                            new FrameSize(readImage.getWidth(), readImage.getHeight()),
                            img,
                            AnimationMetadataSection.EMPTY,
                            null
                    ));
                } else throw new IOException("Failed to read image");
            } catch (Exception e) {
                breadmod$LOGGER.error("Failed to load GPG sprite: {}", pLocation, e);
                cir.setReturnValue(null);
            } finally {
                IOUtils.closeQuietly(resourceStream);
                if(encryptedPath != null) encryptedPath.toFile().deleteOnExit();
            }
        }
    }
}
