package net.abi.abisEngine.rendering;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_is_hdr_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import net.abi.abisEngine.handlers.file.PathHandle;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.util.IOUtil;
import net.abi.abisEngine.util.exceptions.AEIOException;
import net.abi.abisEngine.util.exceptions.AEImageManipulationException;
import net.abi.abisEngine.util.exceptions.AERuntimeException;

public class AEImage {
	private static final Logger logger = LogManager.getLogger(PixelMap.class);

	public class ImageMetaData {
		public int width, height, channels, format;

		public ImageMetaData(int width, int height, int channels, int format) {
			this.width = width;
			this.height = height;
			this.channels = channels;
			this.format = format;
		}
	}

	private PathHandle file;
	private ImageMetaData md;
	private PixelMap image;

	public AEImage(PathHandle file) {
		this.file = file;
	}

	public AEImage(PathHandle file, int w, int h, int channels, int format, ByteBuffer image) {
		this.file = file;
		this.md = new ImageMetaData(w, h, channels, format);
		this.image = new PixelMap(image, w, h, channels, format);
	}

	public PixelMap getData() {
		return image;
	}

	public void loadImage() throws AEIOException {
		ByteBuffer image;

		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			// Decode the image
			image = stbi_load(file.getPath(), w, h, comp, 4);
			if (image == null) {
				throw new AERuntimeException("Failed to load image: " + stbi_failure_reason());
			}

			logger.debug("Image width: " + w.get(0));
			logger.debug("Image height: " + h.get(0));
			logger.debug("Image components: " + comp.get(0));
			logger.debug("Image HDR: " + stbi_is_hdr_from_memory(image));
			this.md = new ImageMetaData(w.get(0), h.get(0), comp.get(0), 0);
			this.image = new PixelMap(image, md.width, md.height, md.channels, md.format);

			/* If there is alpha */
			if (comp.get(0) == 4) {
				premultiplyAlpha();
			}

			STBImage.stbi_image_free(image);
		}
	}

	/**
	 * Takes a AEImage as input and then returns a AEImage instance of the resized
	 * version on src.
	 * 
	 * @param src
	 * @param newW
	 * @param newH
	 * @return
	 */
	public static AEImage resize(AEImage src, int newW, int newH) throws AEImageManipulationException {
		ByteBuffer input = src.getData().getPixelsInByteBuffer(),
				output = MemoryUtil.memAlloc(newW * newH * src.getMD().channels);

		boolean result = STBImageResize.stbir_resize(input, src.getMD().width, /* The input data */
				src.getMD().height, src.getMd().width * src.getMd().channels,
				output, /* The out put data */
				newW, newH, newW * src.getMd().channels, /* The output stride can be zero. */ STBImageResize.STBIR_TYPE_UINT8,
				src.getMD().channels,
				/* The number of channels */ (src.getMD().channels == 4) ? 3 : STBImageResize.STBIR_ALPHA_CHANNEL_NONE,
				0, STBImageResize.STBIR_EDGE_ZERO, STBImageResize.STBIR_EDGE_ZERO,
				STBImageResize.STBIR_FILTER_CUBICBSPLINE, STBImageResize.STBIR_FILTER_CUBICBSPLINE,
				STBImageResize.STBIR_COLORSPACE_SRGB);

		MemoryUtil.memFree(output);
		
		if (!result) {
			throw new AEImageManipulationException("The image :" + src.file.toString() + " Failed to resize.");
		}
		AEImage returnVal = new AEImage(src.file, newW, newH, src.getMD().channels, src.getMD().format, output);
		return returnVal;

	}

	public void premultiplyAlpha() {
		int stride = md.width * 4;
		for (int y = 0; y < md.height; y++) {
			for (int x = 0; x < md.width; x++) {
				int i = y * stride + x * 4;

				float alpha = (image.getPixelsInByteBuffer().get(i + 3) & 0xFF) / 255.0f;
				ByteBuffer data = image.getPixelsInByteBuffer();
				data.put(i + 0, (byte) Math.round(((data.get(i + 0) & 0xFF) * alpha)));
				data.put(i + 1, (byte) Math.round(((data.get(i + 1) & 0xFF) * alpha)));
				data.put(i + 2, (byte) Math.round(((data.get(i + 2) & 0xFF) * alpha)));
			}
		}
	}

	public ImageMetaData getMD() {
		return md;
	}

	public PathHandle getFile() {
		return file;
	}

	public void setFile(PathHandle file) {
		this.file = file;
	}

	public ImageMetaData getMd() {
		return md;
	}

	public void setMd(ImageMetaData md) {
		this.md = md;
	}

	public PixelMap getImage() {
		return image;
	}

	public void setImage(PixelMap image) {
		this.image = image;
	}

	public static Logger getLogger() {
		return logger;
	}
}
