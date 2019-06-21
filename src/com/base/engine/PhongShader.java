package com.base.engine;

public class PhongShader extends Shader {

	private static final PhongShader instance = new PhongShader();

	public static PhongShader getInstance() {
		return instance;
	}

	private static Vector3f ambientLight = new Vector3f(.03f, .03f, .03f);
	private static BaseLight baseLight = new BaseLight(new Vector3f(1, 1, 1), 0);
	private static DirectionalLight directionalLight = new DirectionalLight(baseLight, new Vector3f(0, 0, 0));

	public PhongShader() {
		super();

		addVertexShader(ResourceLoader.loadShader("phongVertex.vs"));
		addFragmentShader(ResourceLoader.loadShader("phongFragment.fs"));
		compileShader();

		addUniform("transform");
		addUniform("baseColor");
		addUniform("ambientLight");

		addUniform("directionalLight.base.color");
		addUniform("directionalLight.base.intensity");
		addUniform("directionalLight.direction");

		addUniform("transformProjected");
		
	}

	@Override
	public void updateUniform(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material mat) {

		if (mat.getTexture() != null) {
			mat.getTexture().bind();
		} else {
			RenderUtil.unBindTextures();
		}

		
		setUniform("transformProjected", projectedMatrix);
		setUniform("transform", worldMatrix);
		setUniform("baseColor", mat.getColor());
		setUniform("ambientLight", ambientLight);
		setUniform("directionalLight", directionalLight);

	}

	public static Vector3f getAmbientLight() {
		return ambientLight;
	}

	public static void setAmbientLight(Vector3f ambientLight) {
		PhongShader.ambientLight = ambientLight;
	}

	public void setUniform(String uniformName, BaseLight baseLight) {

		setUniform(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());

	}

	public void setUniform(String uniformName, DirectionalLight directionalLight) {

		setUniform(uniformName + ".base", directionalLight.getBase());
		setUniform(uniformName + ".direction", directionalLight.getDirection());

	}

	public static BaseLight getBaseLight() {
		return baseLight;
	}

	public static DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public static void setBaseLight(BaseLight baseLight) {
		PhongShader.baseLight = baseLight;
	}

	public static void setDirectionalLight(DirectionalLight directionalLight) {
		PhongShader.directionalLight = directionalLight;
	}

}
