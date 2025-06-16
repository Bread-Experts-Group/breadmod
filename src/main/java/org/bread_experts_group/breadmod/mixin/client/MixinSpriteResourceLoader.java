package org.bread_experts_group.breadmod.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.bread_experts_group.breadmod.mixinutil.General;
import org.bread_experts_group.image.apng.APNGReaderSpi;
import org.bread_experts_group.image.gif.GIFReaderSpi;
import org.bread_experts_group.stream.FailQuickInputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Mixin(SpriteResourceLoader.class)
interface MixinSpriteResourceLoader {
	@Unique
	private static ResourceLocation breadmod$stripExtension(final ResourceLocation pLocation) {
		final String path = pLocation.getPath();
		final int endIndex = path.lastIndexOf((int) '.');
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
				final int pABGRColor = breadmod$getABGRColor(blue, green, red);
				img.setPixelRGBA(x, y, pABGRColor);
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

	@SuppressWarnings("LongLine")
	@Inject(
			method = "loadSprite(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/Resource;)Lnet/minecraft/client/renderer/texture/SpriteContents;",
			at = @At("HEAD"),
			cancellable = true)
	private void loadSprite(
			final ResourceLocation pLocation,
			final Resource pResource,
			final CallbackInfoReturnable<? super SpriteContents> cir
	) {
		final SpriteContents[] result = new SpriteContents[1];
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
						General.logger.error("Failed to read GPG sprite: {}", pLocation, e);
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
							General.logger.info("Decrypted and loaded GPG sprite: {}", stripped);

							final int width = readImage.getWidth();
							final int height = readImage.getHeight();
							final NativeImage pOriginalImage = breadmod$bufferedToNativeImage(readImage);

							result[0] = new SpriteContents(
									stripped,
									new FrameSize(width, height),
									pOriginalImage,
									ResourceMetadata.EMPTY
							);
						} else General.logger.error("Failed to load GPG sprite: {}, corrupt data", pLocation);
					} catch (final IOException e) {
						General.logger.error("Failed to decrypt GPG sprite: {}", pLocation, e);
					}
				});

				inputThread.start();
				outputThread.start();

				inputThread.join(3000L);
				outputStream.close();
				outputThread.join(3000L);
				process.waitFor(3000L, TimeUnit.MILLISECONDS);
			} catch (final IOException | InterruptedException e) {
				General.logger.error("Failed to process GPG sprite: {}", pLocation, e);
			}

			cir.setReturnValue(result[0]);
		} else if (path.endsWith(".gif") || path.endsWith(".apng")) {
			try {
				final InputStream resourceStream = pResource.open();
				ImageReader reader;
				if (path.endsWith(".gif")) reader = new GIFReaderSpi().createReaderInstance();
				else reader = new APNGReaderSpi().createReaderInstance();
				reader.setInput(new FailQuickInputStream(resourceStream));

				List<IIOImage> frames = new ArrayList<>();

				try {
					var i = 0;
					while (true) frames.add(reader.readAll(i++, null));
				} catch (IndexOutOfBoundsException ignored) {
				}

				BufferedImage concatenated = null;

				final int frameCount = frames.size();
				final ArrayList<AnimationFrame> animationFrames = new ArrayList<>(frameCount);
				for (int i = 0; i < frameCount; i++) {
					final BufferedImage frame = reader.read(i);
					animationFrames.add(i, new AnimationFrame(i, 1));
					if (concatenated == null) concatenated = frame;
					else concatenated = breadmod$mergeImages(concatenated, frame);
				}
				if (concatenated == null) return;

				final int width = reader.read(0).getWidth();
				final int height = reader.read(0).getHeight();
				final FrameSize frameSize = new FrameSize(width, height);

				final ResourceLocation stripped = breadmod$stripExtension(pLocation);
				final int concatenatedWidth = concatenated.getWidth();
				final int concatenatedHeight = concatenated.getHeight();

				General.logger.info(
						"Parsed and loaded animated sprite: {} ({} frames, stitch: {} x {})",
						stripped, frames.size(), concatenatedWidth, concatenatedHeight
				);

				final NativeImage pOriginalImage = breadmod$bufferedToNativeImage(concatenated);
				final int frameWidth = frameSize.width();
				final int frameHeight = frameSize.height();

				cir.setReturnValue(new SpriteContents(
						stripped,
						frameSize,
						pOriginalImage,
						new ResourceMetadata.Builder().put(AnimationMetadataSection.SERIALIZER, new AnimationMetadataSection(
								animationFrames,
								frameWidth, frameHeight,
								1 /*baseFrame.delay*/,
								false
						)).build()
				));
			} catch (final IOException e) {
				General.logger.error("Failed to process animated sprite: {}", pLocation, e);
			}
		}

		// Allow assets that can't be decoded by default to use the missing texture.
		// JPG support?
	}
}