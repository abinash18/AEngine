package com.base.engine.components;

import com.base.engine.math.Vector3f;
import com.base.engine.rendering.Shader;

public class SpotLight extends PointLight {

	
	private float cutoff;

	public SpotLight(Vector3f color, float intensity, Vector3f attenuation, float cutoff) {

		super(color, intensity, attenuation);
		this.cutoff = cutoff;

		// super.setShader(ForwardSpotShader.getInstance());
		super.setShader(new Shader("forward-spot"));
	}

	public float getCutoff() {
		return cutoff;

	}

	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}

	public Vector3f getDirection() {
		return getTransform().getTransformedRotation().getForward();
	}

}
