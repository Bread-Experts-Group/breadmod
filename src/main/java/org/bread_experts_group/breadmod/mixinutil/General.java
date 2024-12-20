package org.bread_experts_group.breadmod.mixinutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.InflaterInputStream;

public class General {
	public static final Logger logger = LogManager.getLogger();

	public static byte[] mergeByteArrays(final List<byte[]> byteArrayList) throws IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for (final byte[] byteArray : byteArrayList) outputStream.write(byteArray);
		return outputStream.toByteArray();
	}

	public static void compressedWriteToImage(final BufferedImage img, final byte[] data) throws IOException {
		final ByteArrayInputStream decompressedInputStream = getDecompressedInputStream(data);
		final byte[] decompressed = decompressedInputStream.readAllBytes();

		final WritableRaster raster = img.getRaster();
		final byte[][] imgData = ((DataBufferByte) raster.getDataBuffer()).getBankData();
		int written = 0;

		for (final byte[] bank : imgData) {
			final int toWrite = Math.min(decompressed.length - written, bank.length);
			System.arraycopy(decompressed, written, bank, 0, toWrite);
			written += toWrite;
		}
	}

	private static @NotNull ByteArrayInputStream getDecompressedInputStream(final byte[] data) throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		final InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
		final ByteArrayOutputStream decompressedOutputStream = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		int length;

		while ((length = inflaterInputStream.read(buffer)) != -1) decompressedOutputStream.write(buffer, 0, length);
		return new ByteArrayInputStream(decompressedOutputStream.toByteArray());
	}
}
