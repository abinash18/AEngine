package net.abi.abisEngine.util;

import net.abi.abisEngine.math.Vector3f;

public class Attenuation extends Vector3f {

	public Attenuation(float constant, float linear, float exponent) {
		super(constant, linear, exponent);
	}

	public float getConstant() {
		return super.x();
	}

	public void setConstant(float constant) {
		super.setX(constant);
	}

	public float getLinear() {
		return super.y();
	}

	public void setLinear(float linear) {
		super.setY(linear);
	}

	public float getExponent() {
		return super.z();
	}

	public void setExponent(float exponent) {
		super.setZ(exponent);
	}

}