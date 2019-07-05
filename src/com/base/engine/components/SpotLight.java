package com.base.engine.components;

import com.base.engine.core.Vector3f;
import com.base.engine.rendering.ForwardSpotShader;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;

public class SpotLight extends PointLight {

	private float cutoff;
	private Vector3f direction;

	// TODO: Find A better way of doing this or change back to PointLight in
	// parameter.
	/**
	 * 
	 * @param color
	 * @param intensity
	 * @param attenuation x is constant, y is linear, z is exponent in a Vector3f.
	 * @param position
	 * @param range
	 * @param direction
	 * @param cutoff
	 */
//	public SpotLight(Vector3f color, float intensity, Vector3f attenuation, Vector3f direction, float cutoff) {
//
//		super(color, intensity, attenuation);
//		this.direction = direction.normalize();
//		this.cutoff = cutoff;
//
//		super.setShader(ForwardSpotShader.getInstance());
//
//	}

	public SpotLight(Vector3f color, float intensity, Vector3f attenuation, float cutoff) {

		super(color, intensity, attenuation);
		this.cutoff = cutoff;

		super.setShader(ForwardSpotShader.getInstance());

	}

	public float getCutoff() {
		return cutoff;

	}

	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}

	public Vector3f getDirection() {
		return getTransform().getRotation().getForward();
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction.normalize();
	}

}
