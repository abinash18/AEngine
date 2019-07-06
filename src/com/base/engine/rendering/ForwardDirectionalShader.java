package com.base.engine.rendering;

import com.base.engine.components.BaseLight;
import com.base.engine.components.DirectionalLight;
import com.base.engine.math.Matrix4f;
import com.base.engine.math.Transform;

public class ForwardDirectionalShader extends Shader {

	private static final ForwardDirectionalShader instance = new ForwardDirectionalShader();

	public static ForwardDirectionalShader getInstance() {
		return instance;
	}

	public ForwardDirectionalShader() {
		super();

		super.addVertexShaderFromFile("forward-directional.vs");
		super.addFragmentShaderFromFile("forward-directional.fs");

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

		super.addUniform("directionalLight.base.color");
		super.addUniform("directionalLight.base.intensity");
		super.addUniform("directionalLight.direction");

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

		super.setUniform3f("eyePos",
				super.getRenderingEngine().getMainCamera().getTransform().getTransformedPosition());
		setUniformDirectionalLight("directionalLight", (DirectionalLight) super.getRenderingEngine().getActiveLight());

	}

	public void setUniformDirectionalLight(String uniformName, DirectionalLight directionalLight) {

		setUniformBaseLight(uniformName + ".base", (BaseLight) directionalLight);
		setUniform3f(uniformName + ".direction", directionalLight.getDirection());

	}

	public void setUniformBaseLight(String uniformName, BaseLight baseLight) {

		setUniform3f(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());

	}

}
