package com.base.engine.rendering;

import com.base.engine.core.Material;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;
import com.base.engine.unusedclasses.RenderUtil;

public class PhongShader extends Shader {

	private static final PhongShader instance = new PhongShader();
	private static final int MAX_POINT_LIGHTS = 4;
	private static final int MAX_SPOT_LIGHTS = 4;

	public static PhongShader getInstance() {
		return instance;
	}

	private static PointLight[] pointLights = new PointLight[] {};
	private static SpotLight[] spotLights = new SpotLight[] {};
	private static Vector3f ambientLight = new Vector3f(.03f, .03f, .03f);
	private static BaseLight baseLight = new BaseLight(new Vector3f(1, 1, 1), 0);
	private static DirectionalLight directionalLight = new DirectionalLight(baseLight, new Vector3f(0, 0, 0));

	public PhongShader() {
		super();

		addVertexShaderFromFile("phongVertex.vs");
		addFragmentShaderFromFile("phongFragment.fs");
		compileShader();

		addUniform("transform");
		addUniform("baseColor");
		addUniform("ambientLight");

		addUniform("directionalLight.base.color");
		addUniform("directionalLight.base.intensity");
		addUniform("directionalLight.direction");

		addUniform("transformProjected");

		addUniform("specularIntensity");
		addUniform("specularPower");
		addUniform("eyePos");

		for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
			addUniform("pointLights[" + i + "].base.color");
			addUniform("pointLights[" + i + "].base.intensity");
			addUniform("pointLights[" + i + "].atten.constant");
			addUniform("pointLights[" + i + "].atten.linear");
			addUniform("pointLights[" + i + "].atten.exponent");
			addUniform("pointLights[" + i + "].position");
			addUniform("pointLights[" + i + "].range");
		}

		for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
			addUniform("spotLights[" + i + "].pointLight.base.color");
			addUniform("spotLights[" + i + "].pointLight.base.intensity");
			addUniform("spotLights[" + i + "].pointLight.atten.constant");
			addUniform("spotLights[" + i + "].pointLight.atten.linear");
			addUniform("spotLights[" + i + "].pointLight.atten.exponent");
			addUniform("spotLights[" + i + "].pointLight.position");
			addUniform("spotLights[" + i + "].pointLight.range");
			addUniform("spotLights[" + i + "].cutoff");
			addUniform("spotLights[" + i + "].direction");
		}

	}

	@Override
	public void updateUniform(Transform transform, Material mat) {

//		if (mat.getTexture() != null) {
		mat.getTexture().bind();
//		} else {
//			RenderUtil.unBindTextures();
//		}

		Matrix4f worldMatrix = transform.getTransformation(),
				projectedMatrix = getRenderingEngine().getMainCamera().getViewProjection().mul(worldMatrix);

		setUniform("transformProjected", projectedMatrix);
		setUniform("transform", worldMatrix);
		setUniform("baseColor", mat.getColor());

		setUniform("ambientLight", ambientLight);
		setUniform("directionalLight", directionalLight);
		for (int i = 0; i < pointLights.length; i++) {
			setUniform("pointLights[" + i + "]", pointLights[i]);
		}
		for (int i = 0; i < spotLights.length; i++) {
			setUniform("spotLights[" + i + "]", spotLights[i]);
		}

		setUniformf("specularIntensity", mat.getSpecularIntensity());
		setUniformf("specularPower", mat.getSpecularPower());
		setUniform("eyePos", getRenderingEngine().getMainCamera().getPos());

	}

	private void setUniform(String uniformName, SpotLight spotLight) {
		setUniform(uniformName + ".pointLight", spotLight.getPointLight());
		setUniform(uniformName + ".direction", spotLight.getDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutoff());

	}

	public static PointLight[] getPointLights() {
		return pointLights;
	}

	public static void setPointLights(PointLight[] pointLights) {
		if (pointLights.length > MAX_POINT_LIGHTS) {
			System.err.println("Error: Too Many Point Lights: " + pointLights.length);
			new Exception().printStackTrace();
			System.exit(1);
		}

		PhongShader.pointLights = pointLights;
	}

	public static void setSpotLights(SpotLight[] spotLights) {
		if (spotLights.length > MAX_SPOT_LIGHTS) {
			System.err.println("Error: Too Many Spot Lights: " + spotLights.length);
			new Exception().printStackTrace();
			System.exit(1);
		}

		PhongShader.spotLights = spotLights;
	}

	public static int getMaxPointLights() {
		return MAX_POINT_LIGHTS;
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

	public void setUniform(String uniformName, PointLight pointLight) {

		setUniform(uniformName + ".base", pointLight.getBaseLight());
		setUniformf(uniformName + ".atten.constant", pointLight.getAtten().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAtten().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAtten().getExponent());
		setUniform(uniformName + ".position", pointLight.getPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());

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

	public static int getMaxSpotLights() {
		return MAX_SPOT_LIGHTS;
	}

	public static SpotLight[] getSpotLights() {
		return spotLights;
	}

}
