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

	}

	public Matrix4f getTransformation() {

		Matrix4f translationMatrix = new Matrix4f().initTranslation(position.getX(), position.getY(), position.getZ());
		Matrix4f rotationMatrix = rotation.toRotationMatrix();
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());

		return getParentMatrix().mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
	}

	public void update() {
		if (oldPosition != null) {
			oldPosition.set(position);
			oldRotation.set(rotation);
			oldScale.set(scale);
		} else {
			oldPosition = new Vector3f(0, 0, 0).set(position).add(1.0f); // This way if these are not set somehow it is guaranteed
																// that they will not be null.
			oldRotation = new Quaternion(0, 0, 0, 0).set(rotation).mul(0.5f);
			oldScale = new Vector3f(0, 0, 0).set(scale).add(1.0f);
		}

	}

	public void rotate(Vector3f axis, float anglerad) {

		this.rotation = new Quaternion(axis, anglerad).mul(rotation).normalize();

	}

	public boolean hasChanged() {

		/*
		 * This crawls up the hierarchy until the top one that has changed and is not
		 * null and uses that ones value.
		 */
		if (parent != null && parent.hasChanged()) {
			return true;
		}

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

	private Matrix4f getParentMatrix() {
		/*
		 * This crawls up the hierarchy until the top one that has changed and is not
		 * null and uses that ones value.
		 */
		if (parent != null && parent.hasChanged()) {
			this.parentMatrix = parent.getTransformation();
		}

		return parentMatrix;

	}

	public Quaternion getTransformedRotation() {
		Quaternion parentRotation = new Quaternion(0, 0, 0, 1);
		/*
		 * This crawls up the hierarchy until the top one that has changed and is not
		 * null and uses that ones value. In this case there is no rotation cache for
		 * the parent like there is for position and scale. So there is no
		 * .hasChanged();
		 */
		if (parent != null) {
			parentRotation = parent.getTransformedRotation();
		}
		return parentRotation.mul(rotation);
	}

	public Vector3f getTransformedPosition() {

		return getParentMatrix().transform(position);

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
