package com.base.engine.rendering;

import com.base.engine.components.BaseLight;
import com.base.engine.components.PointLight;
import com.base.engine.math.Matrix4f;
import com.base.engine.math.Transform;
import com.base.engine.math.Vector3f;

public class ForwardPointShader extends Shader {

	private static final ForwardPointShader instance = new ForwardPointShader();

	public static ForwardPointShader getInstance() {
		return instance;
	}

	public ForwardPointShader() {
		super("forward-point");
	}

	@Override
	public void updateUniform(Transform transform, Material mat, RenderingEngine engine) {

		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = engine.getMainCamera().getViewProjection().mul(worldMatrix);
		mat.getTexture("diffuse").bind();

		super.setUniformMatrix4f("model", worldMatrix);
		super.setUniformMatrix4f("MVP", projectedMatrix);
		super.setUniformf("specularIntensity", mat.getFloat("specularIntensity"));
		super.setUniformf("specularPower", mat.getFloat("specularPower"));

		super.setUniform3f("testuniforms", new Vector3f(0, 0, 0));

		super.setUniform3f("eyePos", engine.getMainCamera().getTransform().getTransformedPosition());

		setUniformPointLight("pointLight", (PointLight) engine.getActiveLight());

	}

	public void setUniformBaseLight(String uniformName, BaseLight baseLight) {

		setUniform3f(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());

	}

	public void setUniformPointLight(String uniformName, PointLight pointLight) {

		setUniformBaseLight(uniformName + ".base", pointLight);
		setUniformf(uniformName + ".atten.constant", pointLight.getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getExponent());
		setUniform3f(uniformName + ".position", pointLight.getTransform().getTransformedPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());

	}

}
