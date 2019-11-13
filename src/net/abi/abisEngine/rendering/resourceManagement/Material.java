package net.abi.abisEngine.rendering.resourceManagement;

import java.util.HashMap;
import java.util.Map;

import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.util.Color;

public class Material extends MappedValues {

	public static final String DEFAULT_TEXTURE = "defaultModelTexture.png";

	private Map<String, Texture> textureBinds;
	private Map<String, Color> colorBinds;

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

	public void addTexture(String name, Texture texture) {
		textureBinds.put(name, texture);
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

}
