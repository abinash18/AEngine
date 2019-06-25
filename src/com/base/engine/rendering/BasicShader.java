package com.base.engine.rendering;

import com.base.engine.core.Material;
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
	public void updateUniform(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material mat) {

//		if (mat.getTexture() != null) {
		mat.getTexture().bind();
//		} else {
//			RenderUtil.unBindTextures();
//		}

		setUniform("transform", projectedMatrix);
		setUniform("color", mat.getColor());

	}

}
