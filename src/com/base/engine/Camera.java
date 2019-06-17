package com.base.engine;

import org.lwjgl.input.Mouse;

public class Camera {

	private static final Vector3f yAxis = new Vector3f(0, 1, 0);

	private Vector3f pos, forward, up;

	public Camera() {
		this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
	}

	public Camera(Vector3f pos, Vector3f forward, Vector3f up) {
		this.pos = pos;
		this.forward = forward;
		this.up = up;

		up.normalize();
		forward.normalize();

	}

	public Vector3f getLeft() {

		Vector3f left = forward.cross(up);

		left.normalize();

		return (left);

	}

	public Vector3f getRight() {

		Vector3f right = up.cross(forward);

		right.normalize();

		return (right);

	}

	public void rotateX(float angle) {
		Vector3f Haxis = yAxis.cross(forward);

		Haxis.normalize();

		forward.rotate(angle, Haxis);
		forward.normalize();

		up = forward.cross(Haxis);
		up.normalize();

	}

	public void rotateY(float angle) {
		Vector3f Haxis = yAxis.cross(forward);

		Haxis.normalize();

		forward.rotate(angle, yAxis);
		forward.normalize();

		up = forward.cross(Haxis);
		up.normalize();

	}

	boolean mouseGrabbed = false;
	Vector2f mouseOrigin = Input.getMousePosition(), centerWindow = Window.getCenter();

	public void input() {

		float sensitivity = 0.25f;
		float moveAmount = (float) (10 * Time.getDelta());
		float rotationAmount = (float) (100 * Time.getDelta());

		if (Input.getKey(Input.KEY_W)) {
			move(getForward(), moveAmount);
		}
		if (Input.getKey(Input.KEY_A)) {
			move(getLeft(), moveAmount);
		}
		if (Input.getKey(Input.KEY_D)) {
			move(getRight(), moveAmount);
		}
		if (Input.getKey(Input.KEY_S)) {
			move(getForward(), -moveAmount);
		}

//		if (Input.getKey(Input.KEY_UP)) {
//			rotateX(-rotationAmount);
//		}
//		if (Input.getKey(Input.KEY_LEFT)) {
//			rotateY(-rotationAmount);
//		}
//		if (Input.getKey(Input.KEY_RIGHT)) {
//			rotateY(rotationAmount);
//		}
//		if (Input.getKey(Input.KEY_DOWN)) {
//			rotateX(rotationAmount);
//		}

		if (Input.getKey(Input.KEY_ESCAPE)) {
			Input.setCursor(true);
			mouseGrabbed = false;
		}
		if (Mouse.isButtonDown(0)) {
			Input.setMousePosition(centerWindow);
			Input.setCursor(false);
			mouseGrabbed = true;
		}

		if (mouseGrabbed) {

			Vector2f deltaPos = Input.getMousePosition().sub(centerWindow);

			boolean rotY = deltaPos.getX() != 0, rotX = deltaPos.getY() != 0;

			if (rotY) {
				rotateY(deltaPos.getX() * sensitivity);
			}

			if (rotX) {
				rotateX(-deltaPos.getY() * sensitivity);
			}

			if (rotY || rotX) {
				Input.setMousePosition(centerWindow);
			}

		}

	}

	public void move(Vector3f dir, float amt) {

		pos = pos.add(dir.mul(amt));

	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getForward() {
		return forward;
	}

	public void setForward(Vector3f forward) {
		this.forward = forward;
	}

	public Vector3f getUp() {
		return up;
	}

	public void setUp(Vector3f up) {
		this.up = up;
	}

	public static Vector3f getYaxis() {
		return yAxis;
	}

}
