package com.base.engine.components;

import com.base.engine.math.Vector3f;
import com.base.engine.rendering.shaders.Shader;
import com.base.engine.util.Attenuation;

/**
 * @author abinash
 *
 */
public class PointLight extends BaseLight {

	private static final int COLOR_DEPTH = 256;

	private Attenuation attenuation;
	private float range;

	// TODO: Find a better way or add baselight back to parameter.
	@Deprecated
	public PointLight(Vector3f color, float intensity, float constant, float linear, float exponent) {

		super(color, intensity);

		/*
		 * this.constant = constant; this.linear = linear; this.exponent = exponent;
		 */

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
	public PointLight(Vector3f color, float intensity, Attenuation attenuation) {
		super(color, intensity);
		this.attenuation = attenuation;
		float a = attenuation.getExponent(), b = attenuation.getLinear(),
				c = attenuation.getConstant() - COLOR_DEPTH * getIntensity() * getColor().max();

		this.range = (float) (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);// 1000.0f; // TODO: Try To Calculate This.
		super.setShader(new Shader("forward-point"));
		// System.out.println(range);
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

}
