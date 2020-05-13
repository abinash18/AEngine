package net.abi.abisEngine.rendering;

import java.nio.ByteBuffer;

import net.abi.abisEngine.util.Color;

public class PixelMap {

	private ByteBuffer image;
	private int width, height, format, channels;

	/**
	 * ./res/textures/ is automatically prepended.
	 * 
	 * @param file
	 */
	public PixelMap(ByteBuffer image, int width, int height, int channels, int format) {
		this.image = image;
		this.width = width;
		this.height = height;
		this.format = format;
		this.channels = channels;
	}

	public ByteBuffer getPixelsInByteBuffer() {
		return image;
	}
	
	public void setPixel(int x, int y, Color c) {
		int offset = (width * y + x) * 4;
		image.flip();
		image.put(offset + 0, (byte) c.getRed());
		image.put(offset + 1, (byte) c.getRed());
		image.put(offset + 2, (byte) c.getRed());
		image.put(offset + 3, (byte) c.getRed());
		image.flip();
	}

	public Color getPixel(int x, int y) {
		int offset = (width * y + x) * 4;
		int r = image.get(offset + 0);
		int g = image.get(offset + 1);
		int b = image.get(offset + 2);
		int a = image.get(offset + 3);
		return new Color(r, g, b, a);
	}
	
	public void setData(ByteBuffer data) {
		this.image = data;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

}
