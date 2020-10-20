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
package net.abi.abisEngine.rendering.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.image.PixelMap;
import net.abi.abisEngine.util.Expendable;
import net.abi.abisEngine.util.Util;

/**
 * TODO: ADD Cache to this class.
 * @author abinash
 *
 */
public class Texture implements Expendable {
	public static final String TEXTURES_DIR = "./res/textures/";
	private static Logger logger = LogManager.getLogger(Texture.class.getName());

	public class TextureData {
		private PixelMap data;
	}

	// private static HashMap<String, TextureResource> loadedTextures = new
	// HashMap<String, TextureResource>();
	private String fileName;
	private int id, refCount;
	private TextureData data;

	public Texture(String fileName) {
		this.fileName = fileName;
		this.id = -1;
		// This is added after loadTexture is executed since if it fails then there is
		// no reference.
		this.refCount = 0;
	}

	public Texture load() {
		if (id == -1) {
			id = loadTexture(fileName);
			this.refCount = 1;
		}
		return this;
	}

	public void addReference() {
		refCount++;
	}

	public boolean removeRefrence() {
		refCount--;
		return refCount == 0;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void bind() {
		this.bind(0);
	}

	public void bind(int samplerSlot) {
		assert (samplerSlot >= 0 && samplerSlot <= 31);
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + samplerSlot);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getId());
	}

	private static int loadTexture(String fileName) {

		// String[] splitArray = fileName.split("\\.");

		// String extenstion = splitArray[splitArray.length - 1];

		try {

			BufferedImage image = ImageIO.read(new File(TEXTURES_DIR + fileName));

			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

			ByteBuffer pixelByteBuffer = Util.createByteBuffer(image.getHeight() * image.getWidth() * 4);

			boolean hasAlpha = image.getColorModel().hasAlpha();

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {

					int pixel = pixels[y * image.getWidth() + x];

					// Alpha is in the first highest 8 bits then red then green then blue

					pixelByteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // Red

					pixelByteBuffer.put((byte) ((pixel >> 8) & 0xFF)); // Green

					pixelByteBuffer.put((byte) ((pixel) & 0xFF)); // Blue

					if (hasAlpha) { // If the pixel contains data for alpha then put it in the highest 8 bits or
									// byte of the buffer
						pixelByteBuffer.put((byte) ((pixel >> 24) & 0xFF));
					} else { // else fill the highest byte with 0xFF or a value of 255
						pixelByteBuffer.put((byte) (0xFF));
					}

				}
			}

			// Flips the buffer making it possible to read.
			pixelByteBuffer.flip();

			int _id = GL11.glGenTextures();

			// TODO: Sampler slots using bind();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, _id);

			// System.out.println(id);

			// If the current tex coord is greater than max or less than min then start
			// over (repeat) the texture.
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT); // Tiling
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

			// Linear Filtering
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); // Min Filter
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); // Max

			// Sends the pixel byte buffer to opengl for rendering or binding the texture.
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelByteBuffer);

			return _id;

		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Unable to load texture." + fileName, e);
			// logger.info("Exiting...");
			// System.exit(1);
		}

		return 0;

	}

	@Override
	public void dispose() {
		if (refCount <= 0) {
			GL15.glDeleteBuffers(id);
		}
	}

}
