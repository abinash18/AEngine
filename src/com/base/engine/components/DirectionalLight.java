package com.base.engine.components;

import com.base.engine.core.Vector3f;
import com.base.engine.rendering.ForwardDirectionalShader;

public class DirectionalLight extends BaseLight {

	private Vector3f direction;

	public DirectionalLight(Vector3f color, float intensity, Vector3f direction) {
		super(color, intensity);
		this.direction = direction.normalize();

		setShader(ForwardDirectionalShader.getInstance());

	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

}
