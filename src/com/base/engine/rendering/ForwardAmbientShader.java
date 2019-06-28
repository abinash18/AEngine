package com.base.engine.rendering;

import com.base.engine.core.Transform;

public class ForwardAmbientShader extends Shader {

	private static final ForwardAmbientShader instance = new ForwardAmbientShader();

	public static ForwardAmbientShader getInstance() {
		return instance;
	}

	public ForwardAmbientShader() {
		super();

		addVertexShaderFromFile("forward-ambient.vs");
		addFragmentShaderFromFile("forward-ambient.fs");
		compileShader();

		addUniform("MVP");
		addUniform("ambientIntensity");

	}

	@Override
	public void updateUniform(Transform transform, Material mat) {

		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = getRenderingEngine().getMainCamera().getViewProjection().mul(worldMatrix);
		mat.getTexture().bind();

		setUniform("MVP", projectedMatrix);
		setUniform("ambientIntensity", mat.getColor());

	}

}
