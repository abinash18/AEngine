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
package net.abi.abisEngine.rendering.shader.shaders;

import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.shader.legacy.Shader;

@Deprecated
public class ForwardDirectionalShader extends Shader {

	private static final ForwardDirectionalShader instance = new ForwardDirectionalShader();

	public static ForwardDirectionalShader getInstance() {
		return instance;
	}

	public ForwardDirectionalShader() {
		super("forward-directional");
	}

	@Override
	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {

		super.updateUniforms(transform, mat, engine);

//		Matrix4f worldMatrix = transform.getTransformation(),
//				projectedMatrix = engine.getMainCamera().getViewProjection().mul(worldMatrix);
//		mat.getTexture("diffuse").bind();
//
//		super.setUniformMatrix4f("model", worldMatrix);
//		super.setUniformMatrix4f("MVP", projectedMatrix);
//		super.setUniformf("specularIntensity", mat.getFloat("specularIntensity"));
//		super.setUniformf("specularPower", mat.getFloat("specularPower"));
//
//		super.setUniform3f("eyePos", engine.getMainCamera().getTransform().getTransformedPosition());
//		super.setUniformDirectionalLight("directionalLight", (DirectionalLight) engine.getActiveLight());

	}

}
