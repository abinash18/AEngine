package com.base.engine.rendering;

import com.base.engine.core.Vector3f;

public class DirectionalLight {

	private BaseLight base;
	private Vector3f direction;

	public DirectionalLight(BaseLight base, Vector3f direction) {
		this.base = base;
		this.direction = direction.normalize();
	}

	public BaseLight getBase() {
		return base;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public void setBase(BaseLight base) {
		this.base = base;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

}
