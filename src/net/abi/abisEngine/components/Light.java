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
package net.abi.abisEngine.components;

import net.abi.abisEngine.math.vector.Vector3f;
import net.abi.abisEngine.rendering.shader.AEShader;

public class Light extends SceneComponent {
	private Vector3f color;
	private float intensity;
	private AEShader shader;

	public Light(Vector3f color, float intensity) {
		this.color = color;
		this.intensity = intensity;
	}

	public AEShader getShader() {
		return shader;
	}

	@Override
	public void addToScene() {
		super.getParentScene().addLight(this);
	}

	public void setShader(AEShader shader) {
		this.shader = shader;
	}

	public Vector3f getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}