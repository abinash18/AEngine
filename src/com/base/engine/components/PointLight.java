package com.base.engine.components;

import com.base.engine.math.Vector3f;
import com.base.engine.rendering.Shader;

/**
 * @author abinash
 *
 */
public class PointLight extends BaseLight {

	private static final int COLOR_DEPTH = 256;

	private Vector3f attenuation;
	private float range, constant, linear, exponent;

	// TODO: Find a better way or add baselight back to parameter.
	public PointLight(Vector3f color, float intensity, float constant, float linear, float exponent) {

		super(color, intensity);

		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;

		float a = exponent, b = linear, c = constant - COLOR_DEPTH * getIntensity() * getColor().max();

		this.range = (float) (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);// 1000.0f; // TODO: Try To Calculate This.
//		super.setShader(ForwardPointShader.getInstance());
		super.setShader(new Shader("forward-point"));
		// System.out.println(range);
	}

	/**
	 * 
	 * @param color
	 * @param intensity
	 * @param attenuation x is constant, y is linear, z is exponent in a Vector3f
	 * @param position
	 * @param range
	 */
	public PointLight(Vector3f color, float intensity, Vector3f attenuation) {
		super(color, intensity);
		this.attenuation = attenuation;
		this.constant = attenuation.getX();
		this.linear = attenuation.getY();
		this.exponent = attenuation.getZ();
		float a = exponent, b = linear, c = constant - COLOR_DEPTH * getIntensity() * getColor().max();

		this.range = (float) (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);// 1000.0f; // TODO: Try To Calculate This.
		super.setShader(new Shader("forward-point"));
		// System.out.println(range);
	}

	public float getConstant() {
		return constant;
	}

	public void setConstant(float constant) {
		this.constant = constant;
		this.attenuation.setX(constant);
	}

	public float getLinear() {
		return linear;
	}

	public void setLinear(float linear) {
		this.linear = linear;
		this.attenuation.setY(linear);
	}

	public float getExponent() {
		return exponent;
	}

	public void setExponent(float exponent) {
		this.exponent = exponent;
		this.attenuation.setZ(exponent);
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Vector3f attenuation) {
		this.attenuation = attenuation;
		this.constant = attenuation.getX();
		this.linear = attenuation.getY();
		this.exponent = attenuation.getZ();
	}

}
