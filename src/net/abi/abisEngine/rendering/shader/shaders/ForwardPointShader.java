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

import net.abi.abisEngine.components.Light;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.math.matrix.Matrix4f;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.shader.AEShader;

public class ForwardPointShader extends AEShader {

	public ForwardPointShader() {
		super("forward-point");
	}

	@Override
	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {
		super.updateUniforms(transform, mat, engine);
		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = engine.getActiveCamera().getViewProjection().mul(worldMatrix);
		mat.getTexture("diffuse").bind();
		// setUniformMatrix4f("model", true, worldMatrix);
		// setUniformMatrix4f("MVP", true, projectedMatrix);
		setUniform1f("specularIntensity", mat.getFloat("specularIntensity"));
		setUniform1f("specularPower", mat.getFloat("specularPower"));
		setUniform3f("eyePos", engine.getActiveCamera().getTransform().getTransformedPosition());
		setUniformPointLight("pointLight", (PointLight) engine.getActiveLight());
	}

	public void setUniformLight(String uniformName, Light baseLight) {
		setUniform3f(uniformName + ".color", baseLight.getColor());
		setUniform1f(uniformName + ".intensity", baseLight.getIntensity());
	}

	public void setUniformPointLight(String uniformName, PointLight pointLight) {
		setUniformLight(uniformName + ".base", pointLight);
		setUniform1f(uniformName + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniform1f(uniformName + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniform1f(uniformName + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform3f(uniformName + ".position", pointLight.getTransform().getTransformedPosition());
		setUniform1f(uniformName + ".range", pointLight.getRange());
	}

}
