package net.abi.abisEngine.rendering;

import net.abi.abisEngine.handlers.file.PathHandle;

public class ImageMetaData {
	public int width, height, channels, format;

	public PathHandle file;

	public ImageMetaData(PathHandle file, int width, int height, int channels, int format) {
		this.file = file;
		this.width = width;
		this.height = height;
		this.channels = channels;
		this.format = format;
	}
}