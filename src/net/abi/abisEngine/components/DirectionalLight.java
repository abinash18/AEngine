package net.abi.abisEngine.components;

import net.abi.abisEngine.math.Vector3f;
import net.abi.abisEngine.rendering.shader.Shader;

public class DirectionalLight extends Light {

	// private Vector3f direction;

	// TODO: Add Base light back into parameter.
	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);

		// super.setShader(ForwardDirectionalShader.getInstance());
		super.setShader(new Shader("forward-directional"));
	}

	public Vector3f getDirection() {
		return super.getTransform().getTransformedRotation().getForward();
	}

}
