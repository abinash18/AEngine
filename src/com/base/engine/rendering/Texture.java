package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;

import org.newdawn.slick.opengl.TextureLoader;

public class Texture {

	private int id;

	public Texture(String fileName) {
		this(loadTexture(fileName));
	}

	public Texture(int id) {
		this.id = id;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private static int loadTexture(String fileName) {

		String[] splitArray = fileName.split("\\.");

		String extenstion = splitArray[splitArray.length - 1];

		try {

			int id = TextureLoader.getTexture(extenstion, new FileInputStream(new File("./res/textures/" + fileName)))
					.getTextureID();

			return id;

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return 0;

	}

}
