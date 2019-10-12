package com.base.engine.components;

import com.base.engine.core.CoreEngine;
import com.base.engine.math.Vector3f;
import com.base.engine.rendering.shaders.Shader;

public class BaseLight extends GameComponent {

	private Vector3f color;
	private float intensity;
	private Shader shader;

	public BaseLight(Vector3f color, float intensity) {
		this.color = color;
		this.intensity = intensity;
	}

	public Shader getShader() {
		return shader;
	}

	@Override
	public void addToEngine(CoreEngine engine) {
		engine.getRenderEngine().addLight(this);
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public Vector3f getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

}
