package com.base.engine.components;

import org.lwjgl.input.Mouse;

import com.base.engine.core.Input;
import com.base.engine.math.Transform;
import com.base.engine.math.Vector2f;
import com.base.engine.rendering.windowManagement.Window;

public class FreeLook extends SceneComponent {

	private float sensitivity;
	private int unlockMouseKey;
	private boolean mouseGrabbed;

	public FreeLook(float sensitivity) {
		this(sensitivity, Input.KEY_ESCAPE);
		this.sensitivity = sensitivity;
	}

	public FreeLook(float sensitivity, int unlockMouseKey) {
		this.sensitivity = sensitivity;
		this.unlockMouseKey = unlockMouseKey;
	}

	@Override
	public void input(float delta) {

		if (Input.getKey(unlockMouseKey)) {
			Input.setMousePosition(Window.getCenter());
			// Input.setCursor(true);
			Input.setMouseGrabbed(false);
		}

		if (Mouse.isButtonDown(0)) {
			Input.setMousePosition(Window.getCenter());
			// Input.setCursor(false);
			Input.setMouseGrabbed(true);
		}

		if (Input.isMouseGrabbed()) {

			Vector2f deltaPos = Input.getMousePosition().sub(Window.getCenter());

			boolean rotY = deltaPos.getX() != 0, rotX = deltaPos.getY() != 0;

			if (rotY) {
				super.getTransform().rotate(Transform.Y_AXIS, (float) Math.toRadians(deltaPos.getX() * sensitivity));
			}

			if (rotX) {
				/*
				 * There Is No Need To have The transformed rotation because it is already done
				 * when calling the conjugate in the getViewProjection() Method.
				 */
				super.getTransform().rotate(super.getTransform().getRotation().getRight(),
						(float) Math.toRadians(-deltaPos.getY() * sensitivity));
			}

			if (rotY || rotX) {
				Input.setMousePosition(Window.getCenter());
			}
		}
	}
}
