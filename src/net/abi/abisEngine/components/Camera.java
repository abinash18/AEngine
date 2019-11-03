package net.abi.abisEngine.components;

import net.abi.abisEngine.math.Matrix4f;
import net.abi.abisEngine.math.Vector3f;

public class Camera extends SceneComponent {

	private Matrix4f projection;

	public Camera(float fov, float aspectRatio, float zNear, float zFar, String name) {
		this.projection = new Matrix4f().initProjection(fov, aspectRatio, zNear, zFar);
		super.setName(name);
	}

	public Camera(Vector3f pos, Vector3f forward, Vector3f up, String name) {
		up.normalize();
		forward.normalize();
		super.setName(name);
	}

	public Matrix4f getViewProjection() {
		Matrix4f cameraRotationMatrix = super.getTransform().getTransformedRotation().conjugate().toRotationMatrix();
		/*
		 * Doing Negative multiplication here to eradicate the use of it in the return
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
		super.getParentScene().addCamera(this);
	}

	public void setProjection(Matrix4f projection) {
		this.projection = projection;
	}

	public Matrix4f getProjection() {
		return projection;
	}

}
