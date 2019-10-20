package com.base.engine.rendering.shaders;

import com.base.engine.components.Light;
import com.base.engine.components.PointLight;
import com.base.engine.math.Transform;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.resourceManagement.Material;

@Deprecated
public class ForwardPointShader extends Shader {

	private static final ForwardPointShader instance = new ForwardPointShader();

	public static ForwardPointShader getInstance() {
		return instance;
	}

	public ForwardPointShader() {
		super("forward-point");
	}

	@Override
	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {

		super.updateUniforms(transform, mat, engine);

		/*
		 * Matrix4f worldMatrix = transform.getTransformation(), projectedMatrix =
		 * engine.getMainCamera().getViewProjection().mul(worldMatrix);
		 * mat.getTexture("diffuse").bind();
		 * 
		 * super.setUniformMatrix4f("model", worldMatrix);
		 * super.setUniformMatrix4f("MVP", projectedMatrix);
		 * super.setUniformf("specularIntensity", mat.getFloat("specularIntensity"));
		 * super.setUniformf("specularPower", mat.getFloat("specularPower"));
		 * 
		 * super.setUniform3f("eyePos",
		 * engine.getMainCamera().getTransform().getTransformedPosition());
		 * 
		 * setUniformPointLight("pointLight", (PointLight) engine.getActiveLight());
		 */
	}

	public void setUniformLight(String uniformName, Light baseLight) {

		setUniform3f(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());

	}

	public void setUniformPointLight(String uniformName, PointLight pointLight) {

		setUniformLight(uniformName + ".base", pointLight);
		setUniformf(uniformName + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform3f(uniformName + ".position", pointLight.getTransform().getTransformedPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());

	}

}
