package com.base.engine.rendering.shaders;

import com.base.engine.math.Transform;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class ForwardAmbientShader extends Shader {

	private static final ForwardAmbientShader instance = new ForwardAmbientShader();

	public static ForwardAmbientShader getInstance() {
		return instance;
	}

	public ForwardAmbientShader() {
		super("forward-ambient");

	}

	@Override
	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {

		super.updateUniforms(transform, mat, engine);

//		Matrix4f worldMatrix = transform.getTransformation(),
//				projectedMatrix = engine.getMainCamera().getViewProjection().mul(worldMatrix);
		// mat.getTexture("diffuse").bind();

		// super.setUniformMatrix4f("T_MVP", projectedMatrix);
		// super.setUniform3f("R_ambient", engine.getAmbientLight());

	}

}
