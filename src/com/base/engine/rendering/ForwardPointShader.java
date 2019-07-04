package com.base.engine.rendering;

import com.base.engine.components.BaseLight;
import com.base.engine.components.PointLight;
import com.base.engine.core.Transform;

public class ForwardPointShader extends Shader {

	private static final ForwardPointShader instance = new ForwardPointShader();

	public static ForwardPointShader getInstance() {
		return instance;
	}

	public ForwardPointShader() {
		super();

		super.addVertexShaderFromFile("forward-point.vs");
		super.addFragmentShaderFromFile("forward-point.fs");

		super.setAttribLocation("position", 0);
		super.setAttribLocation("texCoord", 1);
		super.setAttribLocation("normal", 2);

		// After Setting Attributes And Loading Shaders
		super.compileShader();

		super.addUniform("model");
		super.addUniform("MVP");

		super.addUniform("specularIntensity");
		super.addUniform("specularPower");
		super.addUniform("eyePos");

		super.addUniform("pointLight.base.color");
		super.addUniform("pointLight.base.intensity");
		super.addUniform("pointLight.atten.constant");
		super.addUniform("pointLight.atten.linear");
		super.addUniform("pointLight.atten.exponent");
		super.addUniform("pointLight.position");
		super.addUniform("pointLight.range");

	}

	@Override
	public void updateUniform(Transform transform, Material mat) {

		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = super.getRenderingEngine().getMainCamera().getViewProjection().mul(worldMatrix);
		mat.getTexture().bind();

		super.setUniformMatrix4f("model", worldMatrix);
		super.setUniformMatrix4f("MVP", projectedMatrix);
		super.setUniformf("specularIntensity", mat.getSpecularIntensity());
		super.setUniformf("specularPower", mat.getSpecularPower());

		super.setUniform3f("eyePos", super.getRenderingEngine().getMainCamera().getPosition());

		setUniformPointLight("pointLight", (PointLight) super.getRenderingEngine().getActiveLight());

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
		setUniform3f(uniformName + ".position", pointLight.getTransform().getPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());

	}

}
