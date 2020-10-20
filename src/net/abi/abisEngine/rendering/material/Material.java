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
package net.abi.abisEngine.rendering.material;

import java.util.HashMap;
import java.util.Map;

import net.abi.abisEngine.rendering.texture.Texture;
import net.abi.abisEngine.util.Color;
import net.abi.abisEngine.util.MappedValues;

public class Material extends MappedValues {

	public static final String DEFAULT_TEXTURE = "defaultModelTexture.png";
	public static final Material DEFAULT_MATERIAL = new Material().addTexture("default",
			new Texture(Material.DEFAULT_TEXTURE).load());
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
		if (result == null) {
			this.addTexture(name, new Texture(DEFAULT_TEXTURE));
		}
		return result;
	}

	public Map<String, Texture> getTextureBinds() {
		return textureBinds;
	}

	public Map<String, Color> getColorBinds() {
		return colorBinds;
	}
}
