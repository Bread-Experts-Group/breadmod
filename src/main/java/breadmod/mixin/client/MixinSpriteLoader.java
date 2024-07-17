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
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(SpriteLoader.class)
abstract class MixinSpriteLoader {
    @Unique
    private final static Logger breadmod$LOGGER = ModMain.INSTANCE.getLOGGER();

    @Unique
    private static ResourceLocation breadmod$stripExtension(ResourceLocation pLocation) {
        String path = pLocation.getPath();
        return pLocation.withPath(path.substring(0, path.lastIndexOf('.')));
    }

    @Inject(
            method = "loadSprite",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
                    ordinal = 1
            ),
            cancellable = true)
    private static void loadSprite(ResourceLocation pLocation, Resource pResource, CallbackInfoReturnable<SpriteContents> cir) {
        if(pLocation.getPath().endsWith(".asc")) {
            AtomicReference<InputStream> resourceStream = new AtomicReference<>();
            ProcessBuilder pb = new ProcessBuilder("gpg");

            try {
                Process process = pb.start();
                Thread inputThread = new Thread(() -> {
                    try {
                        resourceStream.set(pResource.open());
                        BufferedInputStream fileInput = new BufferedInputStream(resourceStream.get());
                        BufferedOutputStream processInput = new BufferedOutputStream(process.getOutputStream());
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileInput.read(buffer)) != -1) {
                            processInput.write(buffer, 0, bytesRead);
                        }
                        processInput.flush();
                    } catch (IOException e) {
                        breadmod$LOGGER.error("Failed to read ASC/GPG sprite: {}", pLocation, e);
                    }
                });

                Thread outputThread = new Thread(() -> {
                    try (InputStream processOutput = process.getInputStream()) {
                        ByteArrayOutputStream fileContents = new ByteArrayOutputStream();
                        int data;
                        while ((data = processOutput.read()) != -1) fileContents.write(data);

                        BufferedImage readImage = ImageIO.read(new ByteArrayInputStream(fileContents.toByteArray()));
                        if(readImage != null) {
                            NativeImage img = new NativeImage(readImage.getWidth(), readImage.getHeight(), true);
                            for (int x = 0; x < readImage.getWidth(); x++) {
                                for (int y = 0; y < readImage.getHeight(); y++) {
                                    int rgb = readImage.getRGB(x, y);
                                    int b = (rgb)&0xFF;
                                    int g = (rgb>>8)&0xFF;
                                    int r = (rgb>>16)&0xFF;
                                    img.setPixelRGBA(x, y, 0xFF000000 | b << 16 | g << 8 | r);
                                }
                            }

                            ResourceLocation stripped = breadmod$stripExtension(pLocation);
                            breadmod$LOGGER.info("Decrypted and loaded ASC/GPG sprite: {}", stripped);
                            cir.setReturnValue(new SpriteContents(
                                    stripped,
                                    new FrameSize(readImage.getWidth(), readImage.getHeight()),
                                    img,
                                    AnimationMetadataSection.EMPTY,
                                    null
                            ));
                        } else throw new IOException("Image data corrupt");
                    } catch (IOException e) {
                        breadmod$LOGGER.error("Failed to decrypt ASC/GPG sprite: {}", pLocation, e);
                    }
                });

                inputThread.start();
                outputThread.start();

                inputThread.join(3000);
                process.getOutputStream().close();
                outputThread.join(3000);
                process.waitFor(3000, TimeUnit.MILLISECONDS);
            } catch (IOException | InterruptedException e) {
                breadmod$LOGGER.error("Failed to process ASC/GPG sprite: {}", pLocation, e);
            }

            IOUtils.closeQuietly(resourceStream.get());
            cir.setReturnValue(null);
        }
        // Allow assets that can't be decoded by default to use the missing texture.
        // JPG/APNG/GIF support...?
    }
}
