package net.abi.abisEngine.components;

import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.input.GLFWMouseAndKeyboardInput;
import net.abi.abisEngine.math.Transform;
import net.abi.abisEngine.math.Vector2f;

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

		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController()).isKeyDown(unlockMouseKey)) {
			((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController())
					.setCursorPos(super.getParentScene().getParentWindow().getCenter());
			// Input.setCursor(true);
			((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController())
					.setCursorMode(GLFWInput.GLFW_CURSOR_NORMAL);
		}

		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController())
				.isMouseButtonDown(GLFWInput.GLFW_MOUSE_BUTTON_LEFT)) {
			((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController())
					.setCursorPos(super.getParentScene().getParentWindow().getCenter());
			// Input.setCursor(false);
			((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController())
					.setCursorMode(GLFWInput.GLFW_CURSOR_DISABLED);
		}

		if (((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController()).isMouseHiddenAndGrabbed()) {

			Vector2f deltaPos = ((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController()).getCursorPos()
					.sub(super.getParentScene().getParentWindow().getCenter());

			boolean rotY = deltaPos.x() != 0, rotX = deltaPos.y() != 0;

			if (rotY) {
				super.getTransform().rotate(Transform.Y_AXIS, (float) Math.toRadians(deltaPos.x() * sensitivity));
			}

			if (rotX) {
				/*
				 * There Is No Need To have The transformed rotation because it is already done
				 * when calling the conjugate in the getViewProjection() Method.
				 */
				super.getTransform().rotate(super.getTransform().getRotation().getRight(),
						(float) Math.toRadians(deltaPos.y() * sensitivity));
			}

			if (rotY || rotX) {
				((GLFWMouseAndKeyboardInput) super.getParentScene().getInputController())
						.setCursorPos(super.getParentScene().getParentWindow().getCenter());
			}
		}
	}
}
