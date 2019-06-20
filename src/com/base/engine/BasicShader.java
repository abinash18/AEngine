package com.base.engine;

public class BasicShader extends Shader {

	private static final BasicShader instance = new BasicShader();

	public static BasicShader getInstance()
	{
		return instance;
	}
	
	public BasicShader() {
		super();

		addVertexShader(ResourceLoader.loadShader("basicVertex.vs"));
		addFragmentShader(ResourceLoader.loadShader("basicFragment.fs"));
		compileShader();

		addUniform("transform");
		addUniform("color");

	}

	@Override
	public void updateUniform(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material mat) {

		if (mat.getTexture() != null) {
			mat.getTexture().bind();
		} else {
			RenderUtil.unBindTextures();
		}
		
		
		setUniform("transform", projectedMatrix);
		setUniform("color", mat.getColor());
		
		
	}

}
