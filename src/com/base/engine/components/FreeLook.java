package com.base.engine.components;

import com.base.engine.core.input.GLFWInput;
import com.base.engine.math.Transform;
import com.base.engine.math.Vector2f;

public class FreeLook extends SceneComponent {

	private float sensitivity;
	private int unlockMouseKey;
	// private boolean mouseGrabbed;

	public FreeLook(float sensitivity) {
		this(sensitivity, GLFWInput.GLFW_KEY_ESCAPE);
		this.sensitivity = sensitivity;
	}

	public FreeLook(float sensitivity, int unlockMouseKey) {
		this.sensitivity = sensitivity;
		this.unlockMouseKey = unlockMouseKey;
	}

	@Override
	public void input(float delta) {

		if (super.getParentScene().getInputController().isKeyDown(unlockMouseKey)) {
			super.getParentScene().getInputController()
					.setCursorPos(super.getParentScene().getParentWindow().getCenter());
			// Input.setCursor(true);
			super.getParentScene().getInputController().setCursorMode(GLFWInput.GLFW_CURSOR_NORMAL);
		}

		if (super.getParentScene().getInputController().isMouseButtonDown(GLFWInput.GLFW_MOUSE_BUTTON_LEFT)) {
			super.getParentScene().getInputController()
					.setCursorPos(super.getParentScene().getParentWindow().getCenter());
			// Input.setCursor(false);
			super.getParentScene().getInputController().setCursorMode(GLFWInput.GLFW_CURSOR_DISABLED);
		}

		if (super.getParentScene().getInputController().isMouseHiddenAndGrabbed()) {

			Vector2f deltaPos = super.getParentScene().getInputController().getCursorPos()
					.sub(super.getParentScene().getParentWindow().getCenter());

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
						(float) Math.toRadians(deltaPos.getY() * sensitivity));
			}

			if (rotY || rotX) {
				super.getParentScene().getInputController()
						.setCursorPos(super.getParentScene().getParentWindow().getCenter());
			}
		}
	}
}
