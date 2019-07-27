package com.base.engine.rendering.shaders;

import com.base.engine.components.BaseLight;
import com.base.engine.components.DirectionalLight;
import com.base.engine.math.Matrix4f;
import com.base.engine.math.Transform;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

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

		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = engine.getMainCamera().getViewProjection().mul(worldMatrix);
		mat.getTexture("diffuse").bind();

		super.setUniformMatrix4f("model", worldMatrix);
		super.setUniformMatrix4f("MVP", projectedMatrix);
		super.setUniformf("specularIntensity", mat.getFloat("specularIntensity"));
		super.setUniformf("specularPower", mat.getFloat("specularPower"));

		super.setUniform3f("eyePos", engine.getMainCamera().getTransform().getTransformedPosition());
		setUniformDirectionalLight("directionalLight", (DirectionalLight) engine.getActiveLight());

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
