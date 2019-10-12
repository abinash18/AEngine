package com.base.engine.rendering;

import java.util.HashMap;

import com.base.engine.rendering.resourceManagement.MappedValues;

public class Material extends MappedValues {

	public static final String DEFAULT_TEXTURE = "defaultModelTexture.png";

	private HashMap<String, Texture> textureBinds;

	public Material() {
		super();
		textureBinds = new HashMap<String, Texture>();
	}

	public void addTexture(String name, Texture texture) {
		textureBinds.put(name, texture);
	}

	public Texture getTexture(String name) {
		Texture result = textureBinds.get(name);
		if (result != null) {
			return result;
		} else {
			this.addTexture(name, new Texture(DEFAULT_TEXTURE));
		}

		return this.getTexture(name);

	}

	public HashMap<String, Texture> getTextureBinds() {
		return textureBinds;
	}

	public void setTextureBinds(HashMap<String, Texture> textureBinds) {
		this.textureBinds = textureBinds;
	}

}
