package com.base.engine.rendering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.base.engine.core.Util;
import com.base.engine.rendering.resourceManagement.TextureResource;

public class Texture {

	private static HashMap<String, TextureResource> loadedTextures = new HashMap<String, TextureResource>();
	private TextureResource resource;
	private String fileName;

	public Texture(String fileName) {
		this.resource = new TextureResource(loadTexture(fileName));

		TextureResource oldResource = loadedTextures.get(fileName);

		this.fileName = fileName;

		if (oldResource != null) {
			this.resource = oldResource;
			this.resource.addReference();
		} else {
			loadTexture(fileName);
			loadedTextures.put(fileName, resource);
		}

	}

	public static HashMap<String, TextureResource> getLoadedTextures() {
		return loadedTextures;
	}

	public static void setLoadedTextures(HashMap<String, TextureResource> loadedTextures) {
		Texture.loadedTextures = loadedTextures;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, resource.getId());
	}

	public TextureResource getResource() {
		return resource;
	}

	public int getId() {
		return resource.getId();
	}

	public void setResource(TextureResource resource) {
		this.resource = resource;
	}

	private static int loadTexture(String fileName) {

		String[] splitArray = fileName.split("\\.");

		String extenstion = splitArray[splitArray.length - 1];

		try {

			BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName));

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

			int id = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

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

			return id;

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return 0;

	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (resource.removeRefrence() && fileName.isEmpty()) {
			Texture.loadedTextures.remove(fileName);
		}
	}

}
