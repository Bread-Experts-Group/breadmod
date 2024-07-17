package breadmod.mixin.client;

import breadmod.ModMain;
import breadmod.mixinUtil.ImageFrame;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Mixin(SpriteLoader.class)
abstract class MixinSpriteLoader {
    @Unique
    private final static Logger breadmod$LOGGER = ModMain.INSTANCE.getLOGGER();

    @Unique
    private static ResourceLocation breadmod$stripExtension(ResourceLocation pLocation) {
        String path = pLocation.getPath();
        return pLocation.withPath(path.substring(0, path.lastIndexOf('.')));
    }

    @Unique
    private static NativeImage breadmod$bufferedToNativeImage(BufferedImage readImage) {
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
        return img;
    }

    @Unique
    private static BufferedImage breadmod$mergeImages(BufferedImage a, BufferedImage b) {
        if(a.getWidth() != b.getWidth() )
            throw new IllegalArgumentException("A/B width must be equal");
        BufferedImage result = new BufferedImage(a.getWidth(), a.getHeight() + b.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();
        g.drawImage(a, 0, 0, null);
        g.drawImage(b, 0, a.getHeight(), null);
        g.dispose();
        return result;
    }

    @Unique
    private static volatile SpriteContents breadmod$result = null;

    @Inject(
            method = "loadSprite",
            at = @At("HEAD"),
            cancellable = true)
    private static void loadSprite(ResourceLocation pLocation, Resource pResource, CallbackInfoReturnable<SpriteContents> cir) {
        if(pLocation.getPath().endsWith(".asc")) {
            ProcessBuilder pb = new ProcessBuilder("gpg");

            try {
                Process process = pb.start();
                Thread inputThread = new Thread(() -> {
                    try (InputStream pResourceStream = pResource.open()) {
                        BufferedInputStream fileInput = new BufferedInputStream(pResourceStream);
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
                            ResourceLocation stripped = breadmod$stripExtension(pLocation);
                            breadmod$LOGGER.info("Decrypted and loaded ASC/GPG sprite: {}", stripped);
                            breadmod$result = new SpriteContents(
                                    stripped,
                                    new FrameSize(readImage.getWidth(), readImage.getHeight()),
                                    breadmod$bufferedToNativeImage(readImage),
                                    AnimationMetadataSection.EMPTY,
                                    null
                            );
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

            cir.setReturnValue(breadmod$result);
            return;
        }

        if (pLocation.getPath().endsWith(".gif")) {
            try {
                ImageFrame[] frames = ImageFrame.readGif(pResource.open());
                ImageFrame baseFrame = frames[0];
                FrameSize frameSize = new FrameSize(baseFrame.getWidth(), baseFrame.getHeight());
                BufferedImage concatenated = null;

                List<AnimationFrame> animationFrames = new java.util.ArrayList<>(frames.length);
                for (int i = 0; i < frames.length; i++) {
                    ImageFrame frame = frames[i];

                    double breadmod$tickTime = (double) 1 / 20;
                    animationFrames.add(new AnimationFrame(i, (int) Math.round(((double) frame.getDelay() / 100) / breadmod$tickTime)));

                    if(concatenated == null) concatenated = frame.getImage();
                    else concatenated = breadmod$mergeImages(concatenated, frame.getImage());
                }

                ResourceLocation stripped = breadmod$stripExtension(pLocation);
                breadmod$LOGGER.info("Parsed and loaded GIF sprite: {}", stripped);
                cir.setReturnValue(new SpriteContents(
                        stripped,
                        frameSize,
                        breadmod$bufferedToNativeImage(concatenated),
                        new AnimationMetadataSection(
                                animationFrames,
                                frameSize.width(), frameSize.height(),
                                baseFrame.getDelay(),
                                false
                        ),
                        null
                ));
            } catch (IOException e) {
                breadmod$LOGGER.error("Failed to process GIF sprite: {}", pLocation, e);
            }
        }

        // Allow assets that can't be decoded by default to use the missing texture.
        // JPG/APNG support...?
    }
}
