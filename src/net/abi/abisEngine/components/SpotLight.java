package net.abi.abisEngine.components;

import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.shader.Shader;
import net.abi.abisEngine.util.Attenuation;

public class SpotLight extends PointLight {

	private float cutoff;

	public SpotLight(Vector3f color, float intensity, Attenuation attenuation, float cutoff) {

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
