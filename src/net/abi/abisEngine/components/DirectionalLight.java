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

public class DirectionalLight extends Light {

	// private Vector3f direction;

	// TODO: Add Base light back into parameter.
	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);

		// super.setShader(ForwardDirectionalShader.getInstance());
		super.setShader(new Shader("forward-directional"));
	}

	public Vector3f getDirection() {
		return super.getTransform().getTransformedRotation().getForward();
	}

}
