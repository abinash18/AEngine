package com.base.engine.components;

import org.lwjgl.input.Mouse;

import com.base.engine.core.Input;
import com.base.engine.math.Matrix4f;
import com.base.engine.math.Quaternion;
import com.base.engine.math.Vector2f;
import com.base.engine.math.Vector3f;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Window;

public class Camera extends GameComponent {

	private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	// private Vector3f position, forward, up;

	private Matrix4f projection;

	public Camera(float fov, float aspectRatio, float zNear, float zFar) {

		this.projection = new Matrix4f().initProjection(fov, aspectRatio, zNear, zFar);

	}

	public Camera(Vector3f pos, Vector3f forward, Vector3f up) {

		up.normalize();
		forward.normalize();

	}

	public Matrix4f getViewProjection() {
		Matrix4f cameraRotationMatrix = super.getTransform().getRotation().toRotationMatrix();
		Matrix4f cameraTranslationMatrix = new Matrix4f().initTranslation(-super.getTransform().getPosition().getX(),
				-super.getTransform().getPosition().getY(), -super.getTransform().getPosition().getZ());

		return projection.mul(cameraRotationMatrix.mul(cameraTranslationMatrix));

	}

	public Vector3f getLeft() {

		Vector3f left = super.getTransform().getRotation().getForward()
				.cross(super.getTransform().getRotation().getUp()).normalize();

		return (left);

	}

	public Vector3f getRight() {

		Vector3f right = super.getTransform().getRotation().getUp()
				.cross(super.getTransform().getRotation().getForward()).normalize();

		return (right);

	}

	boolean mouseGrabbed = false;
	Vector2f mouseOrigin = Input.getMousePosition(), centerWindow = Window.getCenter();

	@Override
	public void addToRenderingEngine(RenderingEngine engine) {
		engine.addCamera(this);
	}

	@Override
	public void input(float delta) {

		float sensitivity = -0.25f;
		float moveAmount = (float) (10 * delta);
		// float rotationAmount = (float) (100 * delta);

		if (Input.getKey(Input.KEY_W)) {
			move(super.getTransform().getRotation().getForward(), moveAmount);
		}
		if (Input.getKey(Input.KEY_A)) {
			move(super.getTransform().getRotation().getLeft(), moveAmount);
		}
		if (Input.getKey(Input.KEY_D)) {
			move(super.getTransform().getRotation().getRight(), moveAmount);
		}
		if (Input.getKey(Input.KEY_S)) {
			move(super.getTransform().getRotation().getForward(), -moveAmount);
		}

		if (Input.getKey(Input.KEY_ESCAPE)) {
			Input.setCursor(true);
			mouseGrabbed = false;
			System.out.println(this);
			System.exit(1);
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
				super.getTransform().setRotation(super.getTransform().getRotation()
						.mul(new Quaternion(Y_AXIS, (float) Math.toRadians(deltaPos.getX() * sensitivity)))
						.normalize());
			}

			if (rotX) {
				super.getTransform().setRotation(super.getTransform().getRotation()
						.mul(new Quaternion(super.getTransform().getRotation().getRight(),
								(float) Math.toRadians(-deltaPos.getY() * sensitivity)))
						.normalize());
			}

			if (rotY || rotX) {
				Input.setMousePosition(centerWindow);
			}

		}

	}

	@Override
	public String toString() {
		return "Camera [position=" + super.getTransform().getPosition() + ", forward="
				+ super.getTransform().getRotation().getForward() + ", up=" + super.getTransform().getRotation().getUp()
				+ ", mouseGrabbed=" + mouseGrabbed + ", mouseOrigin=" + mouseOrigin + ", centerWindow=" + centerWindow
				+ "]";
	}

	public void move(Vector3f dir, float amt) {

		super.getTransform().setPosition(super.getTransform().getPosition().add(dir.mul(amt)));

	}

	public static Vector3f getYaxis() {
		return Y_AXIS;
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
