package com.base.engine.util;

import com.base.engine.math.Vector3f;

public class Attenuation extends Vector3f {

	public Attenuation(float constant, float linear, float exponent) {
		super(constant, linear, exponent);
	}

	public float getConstant() {
		return super.getX();
	}

	public void setConstant(float constant) {
		super.setX(constant);
	}

	public float getLinear() {
		return super.getY();
	}

	public void setLinear(float linear) {
		super.setY(linear);
	}

	public float getExponent() {
		return super.getZ();
	}

	public void setExponent(float exponent) {
		super.setZ(exponent);
	}

}