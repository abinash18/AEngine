package com.base.engine.components;

import com.base.engine.core.Vector3f;
import com.base.engine.rendering.ForwardPointShader;

public class PointLight extends BaseLight {

	private Vector3f position;
	private float range, constant, linear, exponent;

	public PointLight(Vector3f color, float intensity, float constant, float linear, float exponent, Vector3f position,
			float range) {

		super(color, intensity);

		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
		this.position = position;
		this.range = range;

		setShader(ForwardPointShader.getInstance());

	}

	public float getConstant() {
		return constant;
	}

	public void setConstant(float constant) {
		this.constant = constant;
	}

	public float getLinear() {
		return linear;
	}

	public void setLinear(float linear) {
		this.linear = linear;
	}

	public float getExponent() {
		return exponent;
	}

	public void setExponent(float exponent) {
		this.exponent = exponent;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

}
