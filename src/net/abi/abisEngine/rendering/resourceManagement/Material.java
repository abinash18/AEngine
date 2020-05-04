package net.abi.abisEngine.rendering.resourceManagement;

import java.util.HashMap;
import java.util.Map;

import net.abi.abisEngine.rendering.shaderManagement.Shader;
import net.abi.abisEngine.util.Color;

public class Material extends MappedValues {

	public static final String DEFAULT_TEXTURE = "defaultModelTexture.png";
	public static final Material DEFAULT_MATERIAL = new Material().addTexture("default", new Texture(DEFAULT_TEXTURE));
	private Map<String, Texture> textureBinds;
	private Map<String, Color> colorBinds;

	private Map<String, Shader> shaders;

	public Material() {
		super();
		textureBinds = new HashMap<String, Texture>();
		colorBinds = new HashMap<String, Color>();
		// textureBinds.put("normal_map", new Texture("default_normal.jpg"));
	}

	public void addColor(String name, Color value) {
		colorBinds.put(name, value);
	}

	public Color getColor(String name) {
		Color result = colorBinds.get(name);
		if (result != null) {
			return result;
		}

		return Color.DEFAULT_COLOR;
	}

	public Material addTexture(String name, Texture texture) {
		textureBinds.put(name, texture);
		return this;
	}

	/**
	 * Returns a texture if present and mapped to the name provided. Note: This can
	 * return a null value.
	 * 
	 * @param name
	 * @return
	 */
	public Texture getTexture(String name) {
		Texture result = textureBinds.get(name);
//		if (result != null) {
//			return result;
//		} else {
//			this.addTexture(name, new Texture(DEFAULT_TEXTURE));
//		}
		return result;
	}

	public Shader getShader(String nameOrPassTag) {

		Shader result = shaders.get(nameOrPassTag);

		if (result == null) {
			shaders.put(nameOrPassTag, Shader.DEFAULT_SHADER);
		}

		return shaders.get(nameOrPassTag);
	}

	public void addShader(String nameOrPassTag, String shaderSource) {
		// shaders.put(nameOrPassTag, new Shader());
	}

}
