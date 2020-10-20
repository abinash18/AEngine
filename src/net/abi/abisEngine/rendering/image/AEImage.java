/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.image;

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
import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.util.IOUtil;
import net.abi.abisEngine.util.cacheing.GenericCache;
import net.abi.abisEngine.util.exceptions.AEIOException;
import net.abi.abisEngine.util.exceptions.AEImageManipulationException;
import net.abi.abisEngine.util.exceptions.AERuntimeException;

public class AEImage implements AssetI {
	private static final Logger logger = LogManager.getLogger(PixelMap.class);

	private static GenericCache<String, PixelMap> pixmaps = new GenericCache<String, PixelMap>(String.class,
			PixelMap.class);

	private ImageMetaData md;
	private PixelMap image;

	public AEImage(PathHandle file) {
		if ((image = pixmaps.get(file.getName())) == null) {
			try {
				pixmaps.put(file.getName(), (image = loadImage(file)));
				md = image.getImageMetaData();
			} catch (AEIOException e) {
				logger.error("Could not load image: " + file + " :" + e.getStackTrace());
			}
		}
	}

	public AEImage(PathHandle file, int w, int h, int channels, int format, ByteBuffer image) {
		this.md = new ImageMetaData(file, w, h, channels, format);
		this.image = new PixelMap(image, this.md);
	}

	public PixelMap getData() {
		return image;
	}

	public static PixelMap loadImage(PathHandle file) throws AEIOException {
		ByteBuffer _image;
		ImageMetaData _md;
		PixelMap _image_;

		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			// Decode the image
			_image = STBImage.stbi_load(file.getPath(), w, h, comp, 4);
			if (_image == null) {
				throw new AERuntimeException("Failed to load image: " + stbi_failure_reason());
			}

			logger.fine("Image width: " + w.get(0));
			logger.fine("Image height: " + h.get(0));
			logger.fine("Image components: " + comp.get(0));
			logger.fine("Image HDR: " + STBImage.stbi_is_hdr_from_memory(_image));
			_md = new ImageMetaData(file, w.get(0), h.get(0), comp.get(0), 0);
			_image_ = new PixelMap(_image, _md);

			/* If there is alpha */
			if (comp.get(0) == 4) {
				premultiplyAlpha(_image_);
			}

			STBImage.stbi_image_free(_image);
		}

		return _image_;

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
				output = MemoryUtil.memAlloc(newW * newH * src.getImageMetaData().channels);

		boolean result = STBImageResize.stbir_resize(input, src.getImageMetaData().width, /* The input data */
				src.getImageMetaData().height, src.getImageMetaData().width * src.getImageMetaData().channels,
				output, /* The out put data */
				newW, newH, newW * src.getImageMetaData().channels,
				/* The output stride can be zero. */ STBImageResize.STBIR_TYPE_UINT8, src.getImageMetaData().channels,
				/* The number of channels */ (src.getImageMetaData().channels == 4) ? 3
						: STBImageResize.STBIR_ALPHA_CHANNEL_NONE,
				0, STBImageResize.STBIR_EDGE_ZERO, STBImageResize.STBIR_EDGE_ZERO,
				STBImageResize.STBIR_FILTER_CUBICBSPLINE, STBImageResize.STBIR_FILTER_CUBICBSPLINE,
				STBImageResize.STBIR_COLORSPACE_SRGB);

		MemoryUtil.memFree(output);

		if (!result) {
			throw new AEImageManipulationException(
					"The image :" + src.getImageMetaData().file.toString() + " Failed to resize.");
		}
		AEImage returnVal = new AEImage(src.getImageMetaData().file, newW, newH, src.getImageMetaData().channels,
				src.getImageMetaData().format, output);
		return returnVal;

	}

	public static void premultiplyAlpha(PixelMap image) {
		int stride = image.getImageMetaData().width * 4;
		for (int y = 0; y < image.getImageMetaData().height; y++) {
			for (int x = 0; x < image.getImageMetaData().width; x++) {
				int i = y * stride + x * 4;

				float alpha = (image.getPixelsInByteBuffer().get(i + 3) & 0xFF) / 255.0f;
				ByteBuffer data = image.getPixelsInByteBuffer();
				data.put(i + 0, (byte) Math.round(((data.get(i + 0) & 0xFF) * alpha)));
				data.put(i + 1, (byte) Math.round(((data.get(i + 1) & 0xFF) * alpha)));
				data.put(i + 2, (byte) Math.round(((data.get(i + 2) & 0xFF) * alpha)));
			}
		}
	}

	public ImageMetaData getImageMetaData() {
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

	@Override
	public void dispose() {

	}

	@Override
	public void incRef() {

	}

	@Override
	public int incAndGetRef() {
		return 0;
	}

	@Override
	public void decRef() {

	}

	@Override
	public int decAndGetRef() {
		return 0;
	}

	@Override
	public int getRefs() {
		return 0;
	}
}
