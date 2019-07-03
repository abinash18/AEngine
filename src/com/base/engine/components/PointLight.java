package com.base.engine.components;

import com.base.engine.core.Vector3f;
import com.base.engine.rendering.ForwardPointShader;

/**
 * @author abinash
 *
 */
public class PointLight extends BaseLight {

	private Vector3f position, attenuation;
	private float range, constant, linear, exponent;

	// TODO: Find a better way or add baselight back to parameter.
	public PointLight(Vector3f color, float intensity, float constant, float linear, float exponent, Vector3f position,
			float range) {

		super(color, intensity);

		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
		this.position = position;
		this.range = range;
		super.setShader(ForwardPointShader.getInstance());

	}

	/**
	 * 
	 * @param color
	 * @param intensity
	 * @param attenuation x is constant, y is linear, z is exponent in a Vector3f
	 * @param position
	 * @param range
	 */
	public PointLight(Vector3f color, float intensity, Vector3f attenuation, Vector3f position, float range) {
		super(color, intensity);
		this.position = position;
		this.attenuation = attenuation;
		this.constant = attenuation.getX();
		this.linear = attenuation.getY();
		this.exponent = attenuation.getZ();
		this.range = range;
		super.setShader(ForwardPointShader.getInstance());
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

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
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
