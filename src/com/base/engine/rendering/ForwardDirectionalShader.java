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

		String vertexShaderText = super.loadShader("forward-directional.vs");
		String fragmentShaderText = super.loadShader("forward-directional.fs");

		super.addVertexShader(vertexShaderText);
		super.addFragmentShader(fragmentShaderText);

		super.addAllAttributes(vertexShaderText);

		// After Setting Attributes And Loading Shaders
		super.compileShader();

		super.addAllUniforms(vertexShaderText);
		super.addAllUniforms(fragmentShaderText);
		
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
