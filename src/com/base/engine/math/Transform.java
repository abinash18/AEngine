package com.base.engine.math;

public class Transform {

	private Transform parent;
	private Matrix4f parentMatrix;

	private static float zNear, zFar, width, height, fov;
	private Vector3f position, scale, oldPosition, oldScale;
	private Quaternion rotation, oldRotation;

	public Transform() {
		this.position = new Vector3f(0, 0, 0);
		this.rotation = new Quaternion(0, 0, 0, 1);
		this.scale = new Vector3f(1, 1, 1);
		this.parentMatrix = new Matrix4f().initIdentity();

		oldPosition = new Vector3f(0, 0, 0);
		oldRotation = new Quaternion(0, 0, 0, 0);
		oldScale = new Vector3f(0, 0, 0);

	}

	public Matrix4f getTransformation() {

		Matrix4f translationMatrix = new Matrix4f().initTranslation(position.getX(), position.getY(), position.getZ());
		Matrix4f rotationMatrix = rotation.toRotationMatrix();
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());

		if (parent != null && parent.hasChanged()) {
			this.parentMatrix = parent.getTransformation();
		}

		oldPosition.set(position);
		oldRotation.set(rotation);
		oldScale.set(scale);

		return parentMatrix.mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
	}

	public boolean hasChanged() {

		if (!position.equals(oldPosition)) {
			return true;
		}
		if (!rotation.equals(oldRotation)) {
			return true;
		}
		if (!scale.equals(oldScale)) {
			return true;
		}

		return false;

	}

	public void setParent(Transform parent) {
		this.parent = parent;
	}

	public Transform getParent() {
		return parent;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f translation) {
		this.position = translation;
	}

	public void setTranslation(float x, float y, float z) {
		this.position = new Vector3f(x, y, z);
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public void setRotation(float x, float y, float z, float w) {
		this.rotation = new Quaternion(x, y, z, w);
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = (scale);
	}

	public void setScale(float x, float y, float z) {
		this.scale = new Vector3f(x, y, z);
	}

	public static float getzNear() {
		return zNear;
	}

	public static void setzNear(float zNear) {
		Transform.zNear = zNear;
	}

	public static float getzFar() {
		return zFar;
	}

	public static void setzFar(float zFar) {
		Transform.zFar = zFar;
	}

	public static float getWidth() {
		return width;
	}

	public static void setWidth(float width) {
		Transform.width = width;
	}

	public static float getHeight() {
		return height;
	}

	public static void setHeight(float height) {
		Transform.height = height;
	}

	public static float getFov() {
		return fov;
	}

	public static void setFov(float fov) {
		Transform.fov = fov;
	}
}
