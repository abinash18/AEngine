package com.base.engine.math;

public class Vector3f {
	private float x;
	private float y;
	private float z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float dot(Vector3f r) {
		return x * r.getX() + y * r.getY() + z * r.getZ();
	}

	public Vector3f cross(Vector3f r) {
		float x_ = y * r.getZ() - z * r.getY();
		float y_ = z * r.getX() - x * r.getZ();
		float z_ = x * r.getY() - y * r.getX();

		return new Vector3f(x_, y_, z_);
	}

	public Vector3f normalize() {
		float length = length();

		x /= length;
		y /= length;
		z /= length;

		return new Vector3f(x, y, z);
	}

	public Vector3f rotate(float angleInDegrees, Vector3f axis) {
		float sinAngle = (float) Math.sin(-angleInDegrees);
		float cosAngle = (float) Math.cos(-angleInDegrees);

		return this.cross(axis.mul(sinAngle)).add( // Rotation on local X
				(this.mul(cosAngle)).add( // Rotation on local Z
						axis.mul(this.dot(axis.mul(1 - cosAngle))))); // Rotation on local Y
	}

	public Vector3f rotate(Quaternion rotation) {

		Quaternion conjugate = rotation.conjugate();
		Quaternion w = rotation.mul(this).mul(conjugate);

		x = w.getX();
		y = w.getY();
		z = w.getZ();

		return (new Vector3f(x, y, z));
	}

	public float max() {
		return Math.max(x, Math.max(y, z));
	}

	public Vector3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3f set(Vector3f other) {
		this.set(other.getX(), other.getY(), other.getZ());
		return this;
	}

	public Vector3f lerp(Vector3f dest, float lerpFactor) {

		return (dest.sub(this).mul(lerpFactor).add(this));

	}

	public boolean equals(Vector3f r) {
		return (x == r.getX() && y == r.getY() && z == r.getZ());
	}

	public Vector2f getXY() {
		return (new Vector2f(x, y));
	}

	public Vector2f getYZ() {
		return (new Vector2f(y, z));
	}

	public Vector2f getZX() {
		return (new Vector2f(z, x));
	}

	public Vector2f getYX() {
		return (new Vector2f(y, x));
	}

	public Vector2f getZY() {
		return (new Vector2f(z, y));
	}

	public Vector2f getXZ() {
		return (new Vector2f(x, z));
	}

	public Vector3f add(Vector3f r) {
		return new Vector3f(x + r.getX(), y + r.getY(), z + r.getZ());
	}

	public Vector3f add(float r) {
		return new Vector3f(x + r, y + r, z + r);
	}

	public Vector3f sub(Vector3f r) {
		return new Vector3f(x - r.getX(), y - r.getY(), z - r.getZ());
	}

	public Vector3f sub(float r) {
		return new Vector3f(x - r, y - r, z - r);
	}

	public Vector3f mul(Vector3f r) {
		return new Vector3f(x * r.getX(), y * r.getY(), z * r.getZ());
	}

	public Vector3f mul(float r) {
		return new Vector3f(x * r, y * r, z * r);
	}

	public Vector3f div(Vector3f r) {
		return new Vector3f(x / r.getX(), y / r.getY(), z / r.getZ());
	}

	public Vector3f div(float r) {
		return new Vector3f(x / r, y / r, z / r);
	}

	public Vector3f abs() {
		return (new Vector3f(Math.abs(x), Math.abs(y), Math.abs(z)));
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "Vector3f [x=" + x + ", y=" + y + ", z=" + z + "] (" + x + ", " + y + ", " + z + ")";
	}

}
