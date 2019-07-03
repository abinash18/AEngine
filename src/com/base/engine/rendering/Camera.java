package com.base.engine.rendering;

import org.lwjgl.input.Mouse;

import com.base.engine.core.Input;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class Camera {

	private static final Vector3f yAxis = new Vector3f(0, 1, 0);

	private Vector3f position, forward, up;

	private Matrix4f projection;

	public Camera(float fov, float aspectRatio, float zNear, float zFar) {
		// this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));

		this.position = new Vector3f(0, 0, 0);
		this.forward = new Vector3f(0, 0, 1).normalize();
		this.up = new Vector3f(0, 1, 0).normalize();
		this.projection = new Matrix4f().initProjection(fov, aspectRatio, zNear, zFar);

	}

	public Camera(Vector3f pos, Vector3f forward, Vector3f up) {
		this.position = pos;
		this.forward = forward.normalize();
		this.up = up.normalize();

		up.normalize();
		forward.normalize();

	}

	public Matrix4f getViewProjection() {
		Matrix4f cameraRotationMatrix = new Matrix4f().initRotation(forward, up);
		Matrix4f cameraTranslationMatrix = new Matrix4f().initTranslation(-position.getX(), -position.getY(), -position.getZ());

		return projection.mul(cameraRotationMatrix.mul(cameraTranslationMatrix));

	}

	public Vector3f getLeft() {

		Vector3f left = forward.cross(up).normalize();

		return (left);

	}

	public Vector3f getRight() {

		Vector3f right = up.cross(forward).normalize();

		return (right);

	}

	public void rotateX(float angle) {
		Vector3f Haxis = yAxis.cross(forward).normalize();

		forward = forward.rotate(angle, Haxis).normalize();

		up = forward.cross(Haxis).normalize();

	}

	public void rotateY(float angle) {
		Vector3f Haxis = yAxis.cross(forward).normalize();

		forward = forward.rotate(angle, yAxis).normalize();

		up = forward.cross(Haxis).normalize();

	}

	public void setView(Vector3f forword, Vector3f up) {

		this.forward = forword;
		this.up = up;

	}

	boolean mouseGrabbed = false;
	Vector2f mouseOrigin = Input.getMousePosition(), centerWindow = Window.getCenter();

	public void input(float delta) {

		float sensitivity = 0.25f;
		float moveAmount = (float) (10 * delta);
		// float rotationAmount = (float) (100 * delta);

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
			System.out.println(this);
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

	@Override
	public String toString() {
		return "Camera [position=" + position + ", forward=" + forward + ", up=" + up + ", mouseGrabbed=" + mouseGrabbed
				+ ", mouseOrigin=" + mouseOrigin + ", centerWindow=" + centerWindow + "]";
	}

	public void move(Vector3f dir, float amt) {

		position = position.add(dir.mul(amt));

	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f pos) {
		this.position = pos;
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

	public void setProjection(Matrix4f projection) {
		this.projection = projection;
	}

	public boolean isMouseGrabbed() {
		return mouseGrabbed;
	}

	public void setMouseGrabbed(boolean mouseGrabbed) {
		this.mouseGrabbed = mouseGrabbed;
	}

	public Vector2f getMouseOrigin() {
		return mouseOrigin;
	}

	public void setMouseOrigin(Vector2f mouseOrigin) {
		this.mouseOrigin = mouseOrigin;
	}

	public Vector2f getCenterWindow() {
		return centerWindow;
	}

	public void setCenterWindow(Vector2f centerWindow) {
		this.centerWindow = centerWindow;
	}

}
