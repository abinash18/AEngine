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

import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.shader.legacy.Shader;
import net.abi.abisEngine.util.Attenuation;

public class SpotLight extends PointLight {

	private float cutoff;

	public SpotLight(Vector3f color, float intensity, Attenuation attenuation, float cutoff) {

		super(color, intensity, attenuation);
		this.cutoff = cutoff;

		// super.setShader(ForwardSpotShader.getInstance());
		super.setShader(new Shader("forward-spot"));
	}

	public float getCutoff() {
		return cutoff;

	}

	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}

	public Vector3f getDirection() {
		return getTransform().getTransformedRotation().getForward();
	}

}
