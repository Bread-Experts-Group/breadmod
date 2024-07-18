package breadmod.mixinUtil;

import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

/* https://stackoverflow.com/a/17269591/7693129 */
public class ImageFrame {

    public int delay;
    public DisposeOperation disposal;
    public BufferedImage image;

    public ImageFrame(BufferedImage img, int delay, DisposeOperation disposal) {
        this.delay = delay;
        this.disposal = disposal;
        this.image = img;
    }

    /* https://www.w3.org/TR/png-3/#fcTL-chunk */
    public enum DisposeOperation {
        DISPOSE_OP_NONE(0),
        DISPOSE_OP_BACKGROUND(1),
        DISPOSE_OP_PREVIOUS(2);

        final int value;

        DisposeOperation(int value) {
            this.value = value;
        }

        static DisposeOperation fromValue(int value) {
            for (DisposeOperation operation : values())
                if (operation.value == value) return operation;
            return DISPOSE_OP_NONE;
        }
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public static ImageFrame[] readGIF(InputStream stream) throws IOException {
        ArrayList<ImageFrame> frames = new ArrayList<>(2);

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(stream));

        int lastX = 0;
        int lastY = 0;

        int width = -1;
        int height = -1;

        IIOMetadata metadata = reader.getStreamMetadata();

        Color backgroundColor = null;

        if (metadata != null) {
            IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

            NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
            NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

            if (globalScreeDescriptor.getLength() > 0) {
                IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

                if (screenDescriptor != null) {
                    width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }

            if (globalColorTable.getLength() > 0) {
                IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

                if (colorTable != null) {
                    String bgIndex = colorTable.getAttribute("backgroundColorIndex");

                    IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
                    while (colorEntry != null) {
                        if (colorEntry.getAttribute("index").equals(bgIndex)) {
                            int red = Integer.parseInt(colorEntry.getAttribute("red"));
                            int green = Integer.parseInt(colorEntry.getAttribute("green"));
                            int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

                            backgroundColor = new Color(red, green, blue);
                            break;
                        }

                        colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
                    }
                }
            }
        }

        BufferedImage master = null;
        boolean hasBackground = false;

        for (int frameIndex = 0; ; frameIndex++) {
            BufferedImage image;
            try {
                image = reader.read(frameIndex);
            } catch (IndexOutOfBoundsException io) {
                break;
            }

            if (width == -1 || height == -1) {
                width = image.getWidth();
                height = image.getHeight();
            }

            IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
            NodeList children = root.getChildNodes();

            int delay = Integer.parseInt(gce.getAttribute("delayTime"));

            String disposal = gce.getAttribute("disposalMethod");
            DisposeOperation disposeOperation = DisposeOperation.DISPOSE_OP_NONE;

            if (master == null) {
                master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                master.createGraphics().setColor(backgroundColor);
                master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

                hasBackground = image.getWidth() == width && image.getHeight() == height;

                master.createGraphics().drawImage(image, 0, 0, null);
            } else {
                int x = 0;
                int y = 0;

                for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
                    Node nodeItem = children.item(nodeIndex);

                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        NamedNodeMap map = nodeItem.getAttributes();

                        x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
                        y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
                    }
                }

                if (disposal.equals("restoreToPrevious")) {
                    disposeOperation = DisposeOperation.DISPOSE_OP_PREVIOUS;

                    BufferedImage from = null;
                    for (int i = frameIndex - 1; i >= 0; i--) {
                        if (frames.get(i).disposal != DisposeOperation.DISPOSE_OP_PREVIOUS || frameIndex == 0) {
                            from = frames.get(i).image;
                            break;
                        }
                    }

                    {
                        assert from != null;
                        ColorModel model = from.getColorModel();
                        boolean alpha = from.isAlphaPremultiplied();
                        WritableRaster raster = from.copyData(null);
                        master = new BufferedImage(model, raster, alpha, null);
                    }
                } else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null) {
                    disposeOperation = DisposeOperation.DISPOSE_OP_BACKGROUND;
                    if (!hasBackground || frameIndex > 1) {
                        master.createGraphics().fillRect(
                                lastX, lastY,
                                frames.get(frameIndex - 1).getWidth(),
                                frames.get(frameIndex - 1).getHeight()
                        );
                    }
                }
                master.createGraphics().drawImage(image, x, y, null);

                lastX = x;
                lastY = y;
            }

            {
                BufferedImage copy;

                {
                    ColorModel model = master.getColorModel();
                    boolean alpha = master.isAlphaPremultiplied();
                    WritableRaster raster = master.copyData(null);
                    copy = new BufferedImage(model, raster, alpha, null);
                }
                frames.add(new ImageFrame(copy, delay, disposeOperation));
            }

            master.flush();
        }
        reader.dispose();

        return frames.toArray(new ImageFrame[0]);
    }

    public static ImageFrame[] readAPNG(InputStream stream) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
        reader.setInput(ImageIO.createImageInputStream(stream));

        IIOMetadata metadata = reader.getImageMetadata(0);
        NodeList unknownList = ((IIOMetadataNode) metadata.getAsTree("javax_imageio_png_1.0"))
                .getElementsByTagName("UnknownChunks").item(0).getChildNodes();

        ArrayList<ImageFrame> frames = null;
        ArrayList<Pair<Integer, Integer>> frameOffsets = null;
        ArrayList<ArrayList<byte[]>> frameData = null;

        BufferedImage base = reader.read(0);
        int currentFrame = -1;

        for (int i = 0; i < unknownList.getLength(); i++) {
            IIOMetadataNode node = ((IIOMetadataNode) unknownList.item(i));
            ByteBuffer data = ByteBuffer.wrap((byte[]) node.getUserObject());

            switch (node.getAttribute("type")) {
                case "acTL":
                    int num_frames = data.getInt();
                    frames = new ArrayList<>(Collections.nCopies(num_frames, null));
                    frameOffsets = new ArrayList<>(Collections.nCopies(num_frames, null));
                    frameData = new ArrayList<>(Collections.nCopies(num_frames, null));
                    // max_plays
                    break;
                case "fcTL":
                    // sequence
                    data.getInt();
                    int offsetA, offsetB;
                    short delayNum, delayDen;
                    BufferedImage img = new BufferedImage(data.getInt(), data.getInt(), base.getType());
                    offsetA = data.getInt();
                    offsetB = data.getInt();
                    delayNum = data.getShort();
                    delayDen = data.getShort();
                    DisposeOperation disposeOperation = DisposeOperation.fromValue(data.get());
                    // blend op

                    int delay;
                    if(delayNum == 0) delay = 1;
                    else if(delayDen == 0) delay = delayNum;
                    else {
                        delay = (int) Math.round((double) delayNum / delayDen);
                        if(delay == 0) delay = 1;
                    }

                    assert frames != null;
                    currentFrame++;
                    frames.set(currentFrame, new ImageFrame(img, delay, disposeOperation));
                    frameOffsets.set(currentFrame, Pair.of(offsetA, offsetB));
                    break;
                case "fdAT":
                    assert frameData != null;
                    ArrayList<byte[]> dataList = frameData.get(currentFrame);

                    if(dataList == null) {
                        dataList = new ArrayList<>();
                        frameData.set(currentFrame, dataList);
                    }

                    // sequence
                    data.getInt();
                    byte[] remainder = new byte[data.limit() - 4];

                    dataList.add(remainder);
                    data.get(remainder);
                    break;
            }
        }

        assert frames != null;
        for (int i = 1; i < frames.size(); i++) {
            ImageFrame frame = frames.get(i);
            Pair<Integer, Integer> offsets = frameOffsets.get(i);

            byte[] data = General.mergeByteArrays(frameData.get(i));
            General.compressedWriteToImage(frame.image, data);

            BufferedImage core = new BufferedImage(base.getWidth(), base.getHeight(), base.getType());
            core.getRaster().setRect(offsets.getLeft(), offsets.getRight(), frame.image.getRaster());
            frame.image = core;
        }

        return frames.toArray(new ImageFrame[0]);
    }
}