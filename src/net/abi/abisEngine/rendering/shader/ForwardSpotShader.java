package net.abi.abisEngine.rendering.shader;

import net.abi.abisEngine.components.Light;
import net.abi.abisEngine.components.PointLight;
import net.abi.abisEngine.components.SpotLight;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;

@Deprecated
public class ForwardSpotShader extends Shader {

	private static final ForwardSpotShader instance = new ForwardSpotShader();

	public static ForwardSpotShader getInstance() {
		return instance;
	}

	public ForwardSpotShader() {
		super("forward-spot");

	}

	@Override
	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {

		super.updateUniforms(transform, mat, engine);

		/*
		 * Matrix4f worldMatrix = transform.getTransformation(), projectedMatrix =
		 * engine.getMainCamera().getViewProjection().mul(worldMatrix);
		 * mat.getTexture("diffuse").bind();
		 * 
		 * super.setUniformMatrix4f("model", worldMatrix);
		 * super.setUniformMatrix4f("MVP", projectedMatrix);
		 * super.setUniformf("specularIntensity", mat.getFloat("specularIntensity"));
		 * super.setUniformf("specularPower", mat.getFloat("specularPower"));
		 * 
		 * super.setUniform3f("C_eyePos",
		 * engine.getMainCamera().getTransform().getTransformedPosition());
		 * 
		 * setUniformSpotLight("spotLight", (SpotLight) engine.getActiveLight());
		 */
	}

	public void setUniformPointLight(String uniformName, PointLight pointLight) {

		setUniformLight(uniformName + ".base", pointLight);
		setUniformf(uniformName + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform3f(uniformName + ".position", pointLight.getTransform().getPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());

	}

	public void setUniformLight(String uniformName, Light baseLight) {

		setUniform3f(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());

	}

	public void setUniformSpotLight(String uniformName, SpotLight spotLight) {
		setUniformPointLight(uniformName + ".pointLight", (PointLight) spotLight);
		setUniform3f(uniformName + ".direction", spotLight.getDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutoff());

	}

}
