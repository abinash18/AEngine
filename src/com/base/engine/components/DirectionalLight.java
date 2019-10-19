package com.base.engine.components;

import com.base.engine.math.Vector3f;
import com.base.engine.rendering.shaders.Shader;

public class DirectionalLight extends Light {

	// private Vector3f direction;

	// TODO: Add Base light back into parameter.
	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);

		// super.setShader(ForwardDirectionalShader.getInstance());
		super.setShader(new Shader("forward-directional"));
	}

	public Vector3f getDirection() {
		return super.getTransform().getTransformedRotation().getForward();
	}

}
