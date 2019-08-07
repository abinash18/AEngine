package com.base.engine.components;

import com.base.engine.math.Quaternion;
import com.base.engine.math.Transform;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class LockView extends GameComponent {

	RenderingEngine engine;

	@Override
	public void update(float delta) {
		if (engine != null) {
			Quaternion newRotation = super.getTransform().getLookAtDirection(
					engine.getMainCamera().getTransform().getTransformedPosition(), Transform.Y_AXIS);
			// super.getTransform().setRotation(super.getTransform().getRotation().nlerp(newRotation,
			// delta * 2, true));

			super.getTransform().setRotation(super.getTransform().getRotation().slerp(newRotation, delta * 10, true));

		}
	}

	@Override
	public void render(Shader shader, RenderingEngine engine) {
		this.engine = engine;
	}

}
