package breadmod.mixinUtil;

import breadmod.ModMain;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.InflaterInputStream;

public class General {
    public final static Logger breadmod$LOGGER = ModMain.INSTANCE.getLOGGER();

    public static byte[] mergeByteArrays(List<byte[]> byteArrayList) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (byte[] byteArray : byteArrayList) outputStream.write(byteArray);
        return outputStream.toByteArray();
    }

    public static void compressedWriteToImage(BufferedImage img, byte[] data) throws IOException {
        ByteArrayInputStream decompressedInputStream = getDecompressedInputStream(data);
        byte[] decompressed = decompressedInputStream.readAllBytes();

        byte[][] imgData = ((DataBufferByte)img.getRaster().getDataBuffer()).getBankData();
        int written = 0;

        for (byte[] bank : imgData) {
            int toWrite = Math.min(decompressed.length - written, bank.length);
            System.arraycopy(decompressed, written, bank, 0, toWrite);
            written += toWrite;
        }
    }

    private static @NotNull ByteArrayInputStream getDecompressedInputStream(byte[] data) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
        ByteArrayOutputStream decompressedOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inflaterInputStream.read(buffer)) != -1) decompressedOutputStream.write(buffer, 0, length);
        return new ByteArrayInputStream(decompressedOutputStream.toByteArray());
    }
}
