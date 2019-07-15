package com.base.engine.rendering;

import java.util.HashMap;

import com.base.engine.math.Vector3f;

public class Material {

	public static final String DEFAULT_TEXTURE = "defaultModelTexture.png";
	
	private HashMap<String, Texture> textureBinds;
	private HashMap<String, Float> floatBinds;
	private HashMap<String, Vector3f> vec3fBinds;

	public Material() {
		textureBinds = new HashMap<String, Texture>();
		floatBinds = new HashMap<String, Float>();
		vec3fBinds = new HashMap<String, Vector3f>();
	}

	public void addTexture(String name, Texture texture) {
		textureBinds.put(name, texture);
	}

	public void addFloat(String name, float value) {
		floatBinds.put(name, value);
	}

	public void addVector3f(String name, Vector3f value) {
		vec3fBinds.put(name, value);
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

	public float getFloat(String name) {
		Float result = floatBinds.get(name);
		if (result != null) {
			return result;
		}

		return 0;

	}

	public Vector3f getVector3f(String name) {
		Vector3f result = vec3fBinds.get(name);
		if (result != null) {
			return result;
		}

		return new Vector3f(0, 0, 0);

	}

	public HashMap<String, Texture> getTextureBinds() {
		return textureBinds;
	}

	public void setTextureBinds(HashMap<String, Texture> textureBinds) {
		this.textureBinds = textureBinds;
	}

	public HashMap<String, Float> getFloatBinds() {
		return floatBinds;
	}

	public void setFloatBinds(HashMap<String, Float> floatBinds) {
		this.floatBinds = floatBinds;
	}

	public HashMap<String, Vector3f> getVec3fBinds() {
		return vec3fBinds;
	}

	public void setVec3fBinds(HashMap<String, Vector3f> vec3fBinds) {
		this.vec3fBinds = vec3fBinds;
	}

}
