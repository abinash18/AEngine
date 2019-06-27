package com.base.engine.rendering;

import com.base.engine.core.Transform;
import com.base.engine.unusedclasses.RenderUtil;

public class BasicShader extends Shader {

	private static final BasicShader instance = new BasicShader();

	public static BasicShader getInstance() {
		return instance;
	}

	public BasicShader() {
		super();

		addVertexShaderFromFile("basicVertex.vs");
		addFragmentShaderFromFile("basicFragment.fs");
		compileShader();

		addUniform("transform");
		addUniform("color");

	}

	@Override
	public void updateUniform(Transform transform, Material mat) {

//		if (mat.getTexture() != null) {
		// mat.getTexture().bind();
//		} else {
//			RenderUtil.unBindTextures();
//		}

		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = getRenderingEngine().getMainCamera().getViewProjection().mul(worldMatrix);
		mat.getTexture().bind();

		setUniform("transform", projectedMatrix);
		setUniform("color", mat.getColor());

	}

}
