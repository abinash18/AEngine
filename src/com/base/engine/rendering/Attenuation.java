package com.base.engine.rendering;

public class Attenuation {

	private float constant, linear, exponent;

	public Attenuation(float constant, float linear, float exponent) {
		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
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

}
