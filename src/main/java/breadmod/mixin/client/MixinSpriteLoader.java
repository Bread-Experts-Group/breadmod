package breadmod.mixin.client;

import breadmod.mixutil.General;
import breadmod.mixutil.ImageFrame;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
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

@OnlyIn(Dist.CLIENT)
@Mixin(SpriteLoader.class)
abstract class MixinSpriteLoader {
    @Unique
    private static ResourceLocation breadmod$stripExtension(final ResourceLocation pLocation) {
        final String path = pLocation.getPath();
        final int endIndex = path.lastIndexOf('.');
        final String substring = path.substring(0, endIndex);
        return pLocation.withPath(substring);
    }

    @Unique
    private static NativeImage breadmod$bufferedToNativeImage(final BufferedImage readImage) {
        final int width = readImage.getWidth();
        final int height = readImage.getHeight();
        final NativeImage img = new NativeImage(width, height, true);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int rgb = readImage.getRGB(x, y);
                final int blue = (rgb) & 0xFF;
                final int green = (rgb >> 8) & 0xFF;
                final int red = (rgb >> 16) & 0xFF;
                final int pAbgrColor = breadmod$getABGRColor(blue, green, red);
                img.setPixelRGBA(x, y, pAbgrColor);
            }
        }
        return img;
    }

    @Unique
    private static int breadmod$getABGRColor(final int blue, final int green, final int red) {
        return 0xFF000000 | blue << 16 | green << 8 | red;
    }

    @Unique
    private static BufferedImage breadmod$mergeImages(final BufferedImage imgA, final BufferedImage imgB) {
        final int width = imgA.getWidth();
        if (width != imgB.getWidth())
            throw new IllegalArgumentException("Both images width must be equal");

        final int aHeight = imgA.getHeight();
        final int bHeight = imgB.getHeight();
        final BufferedImage result = new BufferedImage(width, aHeight + bHeight, BufferedImage.TYPE_INT_ARGB);

        final Graphics g = result.getGraphics();
        g.drawImage(imgA, 0, 0, null);
        g.drawImage(imgB, 0, aHeight, null);
        g.dispose();

        return result;
    }

    @Unique
    @Nullable
    private static volatile SpriteContents breadmod$result;

    @Inject(
            method = "loadSprite",
            at = @At("HEAD"),
            cancellable = true)
    private static void loadSprite(
            final ResourceLocation pLocation,
            final Resource pResource,
            final CallbackInfoReturnable<? super SpriteContents> cir
    ) {
        breadmod$result = null;
        final String path = pLocation.getPath();

        if (path.endsWith(".asc")) {
            @SuppressWarnings("UseOfProcessBuilder") final ProcessBuilder pb = new ProcessBuilder("gpg");

            try {
                final Process process = pb.start();
                final OutputStream outputStream = process.getOutputStream();
                final Thread inputThread = new Thread(() -> {
                    try (
                            final InputStream pResourceStream = pResource.open();
                            final BufferedInputStream fileInput = new BufferedInputStream(pResourceStream);
                            final BufferedOutputStream processInput = new BufferedOutputStream(outputStream)
                    ) {
                        final byte[] buffer = new byte[4096];
                        int bytesRead;
                        while (-1 != (bytesRead = fileInput.read(buffer)))
                            processInput.write(buffer, 0, bytesRead);
                        processInput.flush();
                    } catch (final IOException e) {
                        General.breadmod$LOGGER.error("Failed to read GPG sprite: {}", pLocation, e);
                    }
                });

                final Thread outputThread = new Thread(() -> {
                    try (final InputStream processOutput = process.getInputStream()) {
                        final ByteArrayOutputStream fileContents = new ByteArrayOutputStream();
                        int data;
                        while (-1 != (data = processOutput.read())) fileContents.write(data);

                        final byte[] byteArray = fileContents.toByteArray();
                        final BufferedImage readImage = ImageIO.read(new ByteArrayInputStream(byteArray));

                        if (readImage != null) {
                            final ResourceLocation stripped = breadmod$stripExtension(pLocation);
                            General.breadmod$LOGGER.info("Decrypted and loaded GPG sprite: {}", stripped);

                            final int width = readImage.getWidth();
                            final int height = readImage.getHeight();
                            final NativeImage pOriginalImage = breadmod$bufferedToNativeImage(readImage);

                            breadmod$result = new SpriteContents(
                                    stripped,
                                    new FrameSize(width, height),
                                    pOriginalImage,
                                    AnimationMetadataSection.EMPTY,
                                    null
                            );
                        } else General.breadmod$LOGGER.error("Failed to load GPG sprite: {}, corrupt data", pLocation);
                    } catch (final IOException e) {
                        General.breadmod$LOGGER.error("Failed to decrypt GPG sprite: {}", pLocation, e);
                    }
                });

                inputThread.start();
                outputThread.start();

                inputThread.join(3000);
                outputStream.close();
                outputThread.join(3000);
                process.waitFor(3000, TimeUnit.MILLISECONDS);
            } catch (final IOException | InterruptedException e) {
                General.breadmod$LOGGER.error("Failed to process GPG sprite: {}", pLocation, e);
            }

            cir.setReturnValue(breadmod$result);
        } else if (path.endsWith(".gif") || path.endsWith(".apng")) {
            try {
                final InputStream resourceStream = pResource.open();
                final ImageFrame[] frames;

                if (path.endsWith(".gif")) frames = ImageFrame.readGIF(resourceStream);
                else frames = ImageFrame.readAPNG(resourceStream);

                final ImageFrame baseFrame = frames[0];
                BufferedImage concatenated = null;

                final int frameCount = frames.length;
                final List<AnimationFrame> animationFrames = new java.util.ArrayList<>(frameCount);

                for (int i = 0; i < frameCount; i++) {
                    final ImageFrame frame = frames[i];

                    //double breadmod$tickTime = (double) 1 / 20;
                    animationFrames.add(new AnimationFrame(
                            i,
                            1 /*(int) Math.round(((double) frame.delay / 100) / breadmod$tickTime)*/)
                    );

                    if (concatenated == null) concatenated = frame.image;
                    else concatenated = breadmod$mergeImages(concatenated, frame.image);
                }

                final int width = baseFrame.getWidth();
                final int height = baseFrame.getHeight();
                final FrameSize frameSize = new FrameSize(width, height);

                final ResourceLocation stripped = breadmod$stripExtension(pLocation);
                final int concatenatedWidth = concatenated.getWidth();
                final int concatenatedHeight = concatenated.getHeight();

                General.breadmod$LOGGER.info(
                        "Parsed and loaded animated sprite: {} ({} frames, stitch: {} x {})",
                        stripped, frames.length, concatenatedWidth, concatenatedHeight
                );

                final NativeImage pOriginalImage = breadmod$bufferedToNativeImage(concatenated);
                final int frameWidth = frameSize.width();
                final int frameHeight = frameSize.height();

                cir.setReturnValue(new SpriteContents(
                        stripped,
                        frameSize,
                        pOriginalImage,
                        new AnimationMetadataSection(
                                animationFrames,
                                frameWidth, frameHeight,
                                1 /*baseFrame.delay*/,
                                false
                        ),
                        null
                ));
            } catch (final IOException e) {
                General.breadmod$LOGGER.error("Failed to process animated sprite: {}", pLocation, e);
            }
        }

        // Allow assets that can't be decoded by default to use the missing texture.
        // JPG/APNG support?
    }
}
