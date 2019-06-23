package com.base.engine.core;

import com.base.engine.rendering.Camera;
import com.base.engine.rendering.Matrix4f;

public class Transform {

	private static Camera cam;
	private static float zNear, zFar, width, height, fov;
	private Vector3f translation, rotation, scale;

	public Transform() {
		translation = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		scale = new Vector3f(1, 1, 1);

	}

	public Matrix4f getTransformation() {

		Matrix4f translationMatrix = new Matrix4f().initTranslation(translation.getX(), translation.getY(),
				translation.getZ());
		Matrix4f rotationMatrix = new Matrix4f().initRotation(rotation.getX(), rotation.getY(), rotation.getZ());
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());

		return translationMatrix.mul(rotationMatrix.mul(scaleMatrix));
	}

	public Matrix4f getProjectedTransformation() {

		Matrix4f projectionMatrix = new Matrix4f().initProjection(fov, width, height, zNear, zFar);
		Matrix4f transformationMatrix = getTransformation();
		Matrix4f cameraRotationMatrix = new Matrix4f().initCamera(cam.getForward(), cam.getUp());
		Matrix4f cameraTranslationMatrix = new Matrix4f().initTranslation(-cam.getPos().getX(), -cam.getPos().getY(),
				-cam.getPos().getZ());

		return (projectionMatrix.mul(cameraRotationMatrix.mul(cameraTranslationMatrix.mul(transformationMatrix))));

	}

	public static void setProjection(float fov, float width, float height, float zNear, float zFar) {

		Transform.fov = fov;
		Transform.width = width;
		Transform.height = height;
		Transform.zNear = zNear;
		Transform.zFar = zFar;

	}

	public Vector3f getTranslation() {
		return translation;
	}

	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}

	public void setTranslation(float x, float y, float z) {
		this.translation = new Vector3f(x, y, z);
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public void setRotation(float x, float y, float z) {
		this.rotation = new Vector3f(x, y, z);
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

	public static Camera getCam() {
		return cam;
	}

	public static void setCam(Camera cam) {
		Transform.cam = cam;
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
