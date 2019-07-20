package com.base.engine.rendering;

import com.base.engine.math.Matrix4f;
import com.base.engine.math.Transform;

public class ForwardAmbientShader extends Shader {

	private static final ForwardAmbientShader instance = new ForwardAmbientShader();

	public static ForwardAmbientShader getInstance() {
		return instance;
	}

	public ForwardAmbientShader() {
		super("forward-ambient");

	}

	@Override
	public void updateUniform(Transform transform, Material mat, RenderingEngine engine) {

		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = engine.getMainCamera().getViewProjection().mul(worldMatrix);
		mat.getTexture("diffuse").bind();

		super.setUniformMatrix4f("MVP", projectedMatrix);
		super.setUniform3f("ambientIntensity", engine.getAmbientLight());

	}

}
