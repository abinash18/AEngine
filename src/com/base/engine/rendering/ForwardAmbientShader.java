package com.base.engine.rendering;

import com.base.engine.math.Matrix4f;
import com.base.engine.math.Transform;

public class ForwardAmbientShader extends Shader {

	private static final ForwardAmbientShader instance = new ForwardAmbientShader();

	public static ForwardAmbientShader getInstance() {
		return instance;
	}

	public ForwardAmbientShader() {
		super();

		String vertexShaderText = super.loadShader("forward-ambient.vs");
		String fragmentShaderText = super.loadShader("forward-ambient.fs");

		super.addVertexShader(vertexShaderText);
		super.addFragmentShader(fragmentShaderText);

//		super.setAttribLocation("position", 0);
//		super.setAttribLocation("texCoord", 1);

		super.addAllAttributes(vertexShaderText);

		super.compileShader();

		super.addAllUniforms(vertexShaderText);
		super.addAllUniforms(fragmentShaderText);

//		super.addUniform("MVP");
//		super.addUniform("ambientIntensity");

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
