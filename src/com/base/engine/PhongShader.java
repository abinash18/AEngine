package com.base.engine;

public class PhongShader extends Shader {

	private static final PhongShader instance = new PhongShader();

	public static PhongShader getInstance() {
		return instance;
	}

	private static Vector3f ambientLight;

	public PhongShader() {
		super();

		addVertexShader(ResourceLoader.loadShader("phongVertex.vs"));
		addFragmentShader(ResourceLoader.loadShader("phongFragment.fs"));
		compileShader();

		addUniform("transform");
		addUniform("baseColor");
		addUniform("ambientLight");

	}

	@Override
	public void updateUniform(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material mat) {

		if (mat.getTexture() != null) {
			mat.getTexture().bind();
		} else {
			RenderUtil.unBindTextures();
		}

		setUniform("transform", projectedMatrix);
		setUniform("baseColor", mat.getColor());
		setUniform("ambientLight", ambientLight);

	}

	public static Vector3f getAmbientLight() {
		return ambientLight;
	}

	public static void setAmbientLight(Vector3f ambientLight) {
		PhongShader.ambientLight = ambientLight;
	}
}
