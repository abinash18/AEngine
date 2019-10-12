package com.base.engine.rendering.shaders;

import com.base.engine.math.Transform;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.resourceManagement.Material;

@Deprecated
public class ForwardDirectionalShader extends Shader {

	private static final ForwardDirectionalShader instance = new ForwardDirectionalShader();

	public static ForwardDirectionalShader getInstance() {
		return instance;
	}

	public ForwardDirectionalShader() {
		super("forward-directional");
	}

	@Override
	public void updateUniforms(Transform transform, Material mat, RenderingEngine engine) {

		super.updateUniforms(transform, mat, engine);

//		Matrix4f worldMatrix = transform.getTransformation(),
//				projectedMatrix = engine.getMainCamera().getViewProjection().mul(worldMatrix);
//		mat.getTexture("diffuse").bind();
//
//		super.setUniformMatrix4f("model", worldMatrix);
//		super.setUniformMatrix4f("MVP", projectedMatrix);
//		super.setUniformf("specularIntensity", mat.getFloat("specularIntensity"));
//		super.setUniformf("specularPower", mat.getFloat("specularPower"));
//
//		super.setUniform3f("eyePos", engine.getMainCamera().getTransform().getTransformedPosition());
//		super.setUniformDirectionalLight("directionalLight", (DirectionalLight) engine.getActiveLight());

	}

}
