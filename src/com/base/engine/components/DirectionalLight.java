package com.base.engine.components;

import com.base.engine.math.Vector3f;
import com.base.engine.rendering.ForwardDirectionalShader;

public class DirectionalLight extends BaseLight {

	private Vector3f direction;

	// TODO: Add Base light back into parameter.
	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);

		setShader(ForwardDirectionalShader.getInstance());

	}

	public Vector3f getDirection() {
		return super.getTransform().getTransformedRotation().getForward();
	}

}
