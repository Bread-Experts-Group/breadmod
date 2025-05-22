package org.bread_experts_group.breadmod.mixinutil;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* https://stackoverflow.com/a/17269591/7693129 */
public class ImageFrame {

	public final int delay;
	public final DisposeOperation disposal;
	public BufferedImage image;

	public ImageFrame(final BufferedImage img, final int delay, final DisposeOperation disposal) {
		this.delay = delay;
		this.disposal = disposal;
		this.image = img;
	}

	public static ImageFrame[] readGIF(final InputStream stream) throws IOException {
		final ArrayList<ImageFrame> frames = new ArrayList<>(2);

		final ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
		reader.setInput(ImageIO.createImageInputStream(stream));

		int lastX = 0;
		int lastY = 0;

		int width = -1;
		int height = -1;

		final IIOMetadata metadata = reader.getStreamMetadata();

		Color backgroundColor = null;

		if (metadata != null) {
			final IIOMetadataNode globalRoot =
					(IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

			final NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
			final NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

			if (globalScreeDescriptor.getLength() > 0) {
				final IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

				if (screenDescriptor != null) {
					width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
					height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
				}
			}

			if (globalColorTable.getLength() > 0) {
				final IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

				if (colorTable != null) {
					final String bgIndex = colorTable.getAttribute("backgroundColorIndex");

					IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
					while (colorEntry != null) {
						if (colorEntry.getAttribute("index").equals(bgIndex)) {
							final int red = Integer.parseInt(colorEntry.getAttribute("red"));
							final int green = Integer.parseInt(colorEntry.getAttribute("green"));
							final int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

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
			final BufferedImage image;
			try {
				image = reader.read(frameIndex);
			} catch (final IndexOutOfBoundsException io) {
				break;
			}

			if (width == -1 || height == -1) {
				width = image.getWidth();
				height = image.getHeight();
			}

			final IIOMetadataNode root = (IIOMetadataNode) reader
					.getImageMetadata(frameIndex)
					.getAsTree("javax_imageio_gif_image_1.0");
			final IIOMetadataNode gce = (IIOMetadataNode) root
					.getElementsByTagName("GraphicControlExtension")
					.item(0);
			final NodeList children = root.getChildNodes();

			final int delay = Integer.parseInt(gce.getAttribute("delayTime"));

			final String disposal = gce.getAttribute("disposalMethod");
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
					final Node nodeItem = children.item(nodeIndex);

					if (nodeItem.getNodeName().equals("ImageDescriptor")) {
						final NamedNodeMap map = nodeItem.getAttributes();

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
						final ColorModel model = from.getColorModel();
						final boolean alpha = from.isAlphaPremultiplied();
						final WritableRaster raster = from.copyData(null);
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
				final BufferedImage copy;

				{
					final ColorModel model = master.getColorModel();
					final boolean alpha = master.isAlphaPremultiplied();
					final WritableRaster raster = master.copyData(null);
					copy = new BufferedImage(model, raster, alpha, null);
				}
				frames.add(new ImageFrame(copy, delay, disposeOperation));
			}

			master.flush();
		}
		reader.dispose();

		return frames.toArray(new ImageFrame[0]);
	}

	public static ImageFrame[] readAPNG(final InputStream stream) throws IOException {
		final ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
		reader.setInput(ImageIO.createImageInputStream(stream));

		final IIOMetadata metadata = reader.getImageMetadata(0);
		final NodeList unknownList = ((IIOMetadataNode) metadata.getAsTree("javax_imageio_png_1.0"))
				.getElementsByTagName("UnknownChunks").item(0).getChildNodes();

		ArrayList<ImageFrame> frames = null;
		ArrayList<Pair<Integer, Integer>> frameOffsets = null;
		ArrayList<ArrayList<byte[]>> frameData = null;

		final BufferedImage base = reader.read(0);
		int currentFrame = -1;

		for (int i = 0; i < unknownList.getLength(); i++) {
			final IIOMetadataNode node = ((IIOMetadataNode) unknownList.item(i));
			final ByteBuffer data = ByteBuffer.wrap((byte[]) node.getUserObject());

			switch (node.getAttribute("type")) {
				case "acTL":
					final int num_frames = data.getInt();
					frames = new ArrayList<>(Collections.nCopies(num_frames, null));
					frameOffsets = new ArrayList<>(Collections.nCopies(num_frames, null));
					frameData = new ArrayList<>(Collections.nCopies(num_frames, null));
					// max_plays
					break;
				case "fcTL":
					// sequence
					data.getInt();
					final int offsetA;
					final int offsetB;
					final short delayNum;
					final short delayDen;
					final BufferedImage img = new BufferedImage(data.getInt(), data.getInt(), base.getType());
					offsetA = data.getInt();
					offsetB = data.getInt();
					delayNum = data.getShort();
					delayDen = data.getShort();
					final DisposeOperation disposeOperation = DisposeOperation.fromValue(data.get());
					// blend op

					int delay;
					if (delayNum == 0) delay = 1;
					else if (delayDen == 0) delay = delayNum;
					else {
						delay = (int) Math.round((double) delayNum / delayDen);
						if (delay == 0) delay = 1;
					}

					assert frames != null;
					currentFrame++;
					frames.set(currentFrame, new ImageFrame(img, delay, disposeOperation));
					frameOffsets.set(currentFrame, Pair.of(offsetA, offsetB));
					break;
				case "fdAT":
					assert frameData != null;
					ArrayList<byte[]> dataList = frameData.get(currentFrame);

					if (dataList == null) {
						dataList = new ArrayList<>();
						frameData.set(currentFrame, dataList);
					}

					// sequence
					data.getInt();
					final byte[] remainder = new byte[data.limit() - 4];

					dataList.add(remainder);
					data.get(remainder);
					break;
			}
		}

		assert frames != null;
		for (int i = 1; i < frames.size(); i++) {
			final ImageFrame frame = frames.get(i);
			final Pair<Integer, Integer> offsets = frameOffsets.get(i);

			final byte[] data = ImageFrame.mergeByteArrays(frameData.get(i));
			General.compressedWriteToImage(frame.image, data);

			final BufferedImage core = new BufferedImage(
					base.getWidth(), base.getHeight(),
					BufferedImage.TYPE_4BYTE_ABGR
			);
			core.getRaster().setRect(offsets.getLeft(), offsets.getRight(), frame.image.getRaster());
			frame.image = core;
		}

		return frames.toArray(new ImageFrame[0]);
	}

	static byte[] mergeByteArrays(final List<byte[]> byteArrayList) throws IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for (final byte[] byteArray : byteArrayList) outputStream.write(byteArray);
		return outputStream.toByteArray();
	}

	public int getWidth() {
		return this.image.getWidth();
	}

	public int getHeight() {
		return this.image.getHeight();
	}

	/* https://www.w3.org/TR/png-3/#fcTL-chunk */
	public enum DisposeOperation {
		DISPOSE_OP_NONE(0),
		DISPOSE_OP_BACKGROUND(1),
		DISPOSE_OP_PREVIOUS(2);

		final int value;

		DisposeOperation(final int value) {
			this.value = value;
		}

		static DisposeOperation fromValue(final int value) {
			for (final DisposeOperation operation : DisposeOperation.values())
				if (operation.value == value) return operation;
			return DisposeOperation.DISPOSE_OP_NONE;
		}
	}
}