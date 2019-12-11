package net.abi.abisEngine.math;

public class Vector3i {
	private int x;
	private int y;
	private int z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float dot(Vector3i r) {
		return x * r.getX() + y * r.getY() + z * r.getZ();
	}

	public Vector3i cross(Vector3i r) {
		int x_ = y * r.getZ() - z * r.getY();
		int y_ = z * r.getX() - x * r.getZ();
		int z_ = x * r.getY() - y * r.getX();

		return new Vector3i(x_, y_, z_);
	}

	public Vector3i normalize() {
		float length = length();

		x /= length;
		y /= length;
		z /= length;

		return new Vector3i(x, y, z);
	}

	public float max() {
		return Math.max(x, Math.max(y, z));
	}

	public Vector3i set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3i set(Vector3i other) {
		this.set(other.getX(), other.getY(), other.getZ());
		return this;
	}

	public boolean equals(Vector3i r) {
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

	public Vector3i add(Vector3i r) {
		return new Vector3i(x + r.getX(), y + r.getY(), z + r.getZ());
	}

	public Vector3i add(int r) {
		return new Vector3i(x + r, y + r, z + r);
	}

	public Vector3i sub(Vector3i r) {
		return new Vector3i(x - r.getX(), y - r.getY(), z - r.getZ());
	}

	public Vector3i sub(int r) {
		return new Vector3i(x - r, y - r, z - r);
	}

	public Vector3i mul(Vector3i r) {
		return new Vector3i(x * r.getX(), y * r.getY(), z * r.getZ());
	}

	public Vector3i mul(int r) {
		return new Vector3i(x * r, y * r, z * r);
	}

	public Vector3i div(Vector3i r) {
		return new Vector3i(x / r.getX(), y / r.getY(), z / r.getZ());
	}

	public Vector3i div(int r) {
		return new Vector3i(x / r, y / r, z / r);
	}

	public Vector3i abs() {
		return (new Vector3i(Math.abs(x), Math.abs(y), Math.abs(z)));
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "Vector3f [x=" + x + ", y=" + y + ", z=" + z + "] (" + x + ", " + y + ", " + z + ")";
	}

}
