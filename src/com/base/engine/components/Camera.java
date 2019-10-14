package com.base.engine.components;

import com.base.engine.math.Matrix4f;
import com.base.engine.math.Vector3f;

public class Camera extends GameComponent {

	// private Vector3f position, forward, up;

//	private static Camera camera;
//
//	public static Camera getInstance() {
//		if (camera == null) {
//			camera = new Camera((float) Math.toRadians(70.0f), (float) Window.getWidth() / (float) Window.getHeight(),
//					0.01f, 1000.0f);
//		}
//		return camera;
//	}

	private Matrix4f projection;

	public Camera(float fov, float aspectRatio, float zNear, float zFar) {
		this.projection = new Matrix4f().initProjection(fov, aspectRatio, zNear, zFar);
	}

	public Camera(Vector3f pos, Vector3f forward, Vector3f up) {
		up.normalize();
		forward.normalize();
	}

//	public Camera resetCamera(float fov, float aspectRatio, float zNear, float zFar) {
//		Camera.camera = new Camera(fov, aspectRatio, zNear, zFar);
//		return getInstance();
//	}

	public Matrix4f getViewProjection() {
		Matrix4f cameraRotationMatrix = super.getTransform().getTransformedRotation().conjugate().toRotationMatrix();
		/*
		 * Doing Negative multiplication here to eradicate the use of it
		 *  in the return
		 * statement.
		 */
		Vector3f cameraPosition = super.getTransform().getTransformedPosition().mul(-1);
		Matrix4f cameraTranslationMatrix = new Matrix4f().initTranslation(cameraPosition.getX(), cameraPosition.getY(),
				cameraPosition.getZ());
		return projection.mul(cameraRotationMatrix.mul(cameraTranslationMatrix));
	}

	@Override
	public void input(float delta) {
		super.input(delta);
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

	@Override
	public void addToScene() {
		super.getParentScene().setMainCamera(this);
	}

	public void setProjection(Matrix4f projection) {
		this.projection = projection;
	}

	public Matrix4f getProjection() {
		return projection;
	}

}
