package net.abi.abisEngine.rendering.shader;

import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.renderPipeline.RenderingEngine;

@Deprecated
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
	}

}
