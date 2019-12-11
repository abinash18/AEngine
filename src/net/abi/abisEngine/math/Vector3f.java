package net.abi.abisEngine.math;

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
		return x * r.x() + y * r.y() + z * r.z();
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Vector3f cross(Vector3f r) {
		float x_ = y * r.z() - z * r.y();
		float y_ = z * r.x() - x * r.z();
		float z_ = x * r.y() - y * r.x();

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

	public Vector3f rotateAxis(float angle, float x, float y, float z) {
		return rotateAxis(angle, x, y, z, this);
	}

	public Vector3f rotateAxis(float angle, float aX, float aY, float aZ, Vector3f dest) {
		if (aY == 0.0f && aZ == 0.0f && Math.abs(aX) == 1.0f)
			return rotateX(aX * angle, dest);
		else if (aX == 0.0f && aZ == 0.0f && Math.abs(aY) == 1.0f)
			return rotateY(aY * angle, dest);
		else if (aX == 0.0f && aY == 0.0f && Math.abs(aZ) == 1.0f)
			return rotateZ(aZ * angle, dest);
		return rotateAxisInternal(angle, aX, aY, aZ, dest);
	}

	private Vector3f rotateAxisInternal(float angle, float aX, float aY, float aZ, Vector3f dest) {
		float hangle = angle * 0.5f;
		float sinAngle = (float) Math.sin(hangle);
		float qx = aX * sinAngle, qy = aY * sinAngle, qz = aZ * sinAngle;
		float qw = (float) Math.cosFromSin(sinAngle, hangle);
		float w2 = qw * qw, x2 = qx * qx, y2 = qy * qy, z2 = qz * qz, zw = qz * qw;
		float xy = qx * qy, xz = qx * qz, yw = qy * qw, yz = qy * qz, xw = qx * qw;
		float nx = (w2 + x2 - z2 - y2) * x + (-zw + xy - zw + xy) * y + (yw + xz + xz + yw) * z;
		float ny = (xy + zw + zw + xy) * x + (y2 - z2 + w2 - x2) * y + (yz + yz - xw - xw) * z;
		float nz = (xz - yw + xz - yw) * x + (yz + yz + xw + xw) * y + (z2 - y2 - x2 + w2) * z;
		dest.x = nx;
		dest.y = ny;
		dest.z = nz;
		return dest;
	}

	public Vector3f rotateX(float angle) {
		return rotateX(angle, this);
	}

	public Vector3f rotateX(float angle, Vector3f dest) {
		float sin = (float) Math.sin(angle), cos = (float) Math.cosFromSin(sin, angle);
		float y = this.y * cos - this.z * sin;
		float z = this.y * sin + this.z * cos;
		dest.x = this.x;
		dest.y = y;
		dest.z = z;
		return dest;
	}

	public Vector3f rotateY(float angle) {
		return rotateY(angle, this);
	}

	public Vector3f rotateY(float angle, Vector3f dest) {
		float sin = (float) Math.sin(angle), cos = (float) Math.cosFromSin(sin, angle);
		float x = this.x * cos + this.z * sin;
		float z = -this.x * sin + this.z * cos;
		dest.x = x;
		dest.y = this.y;
		dest.z = z;
		return dest;
	}

	public Vector3f rotateZ(float angle) {
		return rotateZ(angle, this);
	}

	public Vector3f rotateZ(float angle, Vector3f dest) {
		float sin = (float) Math.sin(angle), cos = (float) Math.cosFromSin(sin, angle);
		float x = this.x * cos - this.y * sin;
		float y = this.x * sin + this.y * cos;
		dest.x = x;
		dest.y = y;
		dest.z = this.z;
		return dest;
	}

	public float lengthSquared() {
		return lengthSquared(x, y, z);
	}

	public static float lengthSquared(float x, float y, float z) {
		return x * x + y * y + z * z;
	}

	public float distance(Vector3f v) {
		return distance(v.x(), v.y(), v.z());
	}

	public float distance(float x, float y, float z) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public float distanceSquared(Vector3f v) {
		return distanceSquared(v.x(), v.y(), v.z());
	}

	public float distanceSquared(float x, float y, float z) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public static float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float) Math.sqrt(distanceSquared(x1, y1, z1, x2, y2, z2));
	}

	public static float distanceSquared(float x1, float y1, float z1, float x2, float y2, float z2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;
		return dx * dx + dy * dy + dz * dz;
	}

	public Vector3f set(Vector3f other) {
		this.set(other.x(), other.y(), other.z());
		return this;
	}

	public Vector3f lerp(Vector3f dest, float lerpFactor) {
		return (dest.sub(this).mul(lerpFactor).add(this));
	}

	public boolean equals(Vector3f r) {
		return (x == r.x() && y == r.y() && z == r.z());
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
		return new Vector3f(x + r.x(), y + r.y(), z + r.z());
	}

	public Vector3f add(float r) {
		return new Vector3f(x + r, y + r, z + r);
	}

	public Vector3f add(float xr, float yr, float zr) {
		return new Vector3f(x + xr, y + yr, z + zr);
	}

	public Vector3f sub(Vector3f r) {
		return new Vector3f(x - r.x(), y - r.y(), z - r.z());
	}

	public Vector3f sub(float r) {
		return new Vector3f(x - r, y - r, z - r);
	}

	public Vector3f mul(Vector3f r) {
		return new Vector3f(x * r.x(), y * r.y(), z * r.z());
	}

	public Vector3f mul(float r) {
		return new Vector3f(x * r, y * r, z * r);
	}

	public Vector3f div(Vector3f r) {
		return new Vector3f(x / r.x(), y / r.y(), z / r.z());
	}

	public Vector3f div(float r) {
		return new Vector3f(x / r, y / r, z / r);
	}

	public Vector3f abs() {
		return (new Vector3f(Math.abs(x), Math.abs(y), Math.abs(z)));
	}

	public float x() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float y() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float z() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "Vector3f [x=" + x + ", y=" + y + ", z=" + z + "] (" + x + ", " + y + ", " + z + ")";
	}

	public float angleCos(Vector3f v) {
		double length1Squared = x * x + y * y + z * z;
		double length2Squared = v.x() * v.x() + v.y() * v.y() + v.z() * v.z();
		double dot = x * v.x() + y * v.y() + z * v.z();
		return (float) (dot / (Math.sqrt(length1Squared * length2Squared)));
	}

	public float angle(Vector3f v) {
		float cos = angleCos(v);
		// This is because sometimes cos goes above 1 or below -1 because of lost
		// precision
		cos = cos < 1 ? cos : 1;
		cos = cos > -1 ? cos : -1;
		return (float) Math.acos(cos);
	}

	public float angleSigned(Vector3f v, Vector3f n) {
		return angleSigned(v.x(), v.y(), v.z(), n.x(), n.y(), n.z());
	}

	public float angleSigned(float x, float y, float z, float nx, float ny, float nz) {
		return (float) Math.atan2(
				(this.y * z - this.z * y) * nx + (this.z * x - this.x * z) * ny + (this.x * y - this.y * x) * nz,
				this.x * x + this.y * y + this.z * z);
	}

	public float min(Vector3f v) {
		return Math.min(x, Math.min(y, z));
	}

	public Vector3f zero() {
		return this.set(0, 0, 0);
	}

	public Vector3f negate() {
		return negate(this);
	}

	public Vector3f negate(Vector3f dest) {
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		return dest;
	}

	public Vector3f absolute() {
		return absolute(this);
	}

	public Vector3f absolute(Vector3f dest) {
		dest.x = Math.abs(this.x);
		dest.y = Math.abs(this.y);
		dest.z = Math.abs(this.z);
		return dest;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	public Vector3f reflect(Vector3f normal) {
		return reflect(normal, this);
	}

	public Vector3f reflect(float x, float y, float z) {
		return reflect(x, y, z, this);
	}

	public Vector3f reflect(Vector3f normal, Vector3f dest) {
		return reflect(normal.x(), normal.y(), normal.z(), dest);
	}

	public Vector3f reflect(float x, float y, float z, Vector3f dest) {
		float dot = this.dot(x, y, z);
		dest.x = this.x - (dot + dot) * x;
		dest.y = this.y - (dot + dot) * y;
		dest.z = this.z - (dot + dot) * z;
		return dest;
	}

	public Vector3f half(Vector3f other) {
		return half(other, this);
	}

	public Vector3f half(float x, float y, float z) {
		return half(x, y, z, this);
	}

	public Vector3f half(Vector3f other, Vector3f dest) {
		return half(other.x(), other.y(), other.z(), dest);
	}

	public Vector3f half(float x, float y, float z, Vector3f dest) {
		return dest.set(this).add(x, y, z).normalize();
	}

	public Vector3f smoothStep(Vector3f v, float t, Vector3f dest) {
		float t2 = t * t;
		float t3 = t2 * t;
		dest.x = (x + x - v.x() - v.x()) * t3 + (3.0f * v.x() - 3.0f * x) * t2 + x * t + x;
		dest.y = (y + y - v.y() - v.y()) * t3 + (3.0f * v.y() - 3.0f * y) * t2 + y * t + y;
		dest.z = (z + z - v.z() - v.z()) * t3 + (3.0f * v.z() - 3.0f * z) * t2 + z * t + z;
		return dest;
	}

	public Vector3f hermite(Vector3f t0, Vector3f v1, Vector3f t1, float t, Vector3f dest) {
		float t2 = t * t;
		float t3 = t2 * t;
		dest.x = (x + x - v1.x() - v1.x() + t1.x() + t0.x()) * t3
				+ (3.0f * v1.x() - 3.0f * x - t0.x() - t0.x() - t1.x()) * t2 + x * t + x;
		dest.y = (y + y - v1.y() - v1.y() + t1.y() + t0.y()) * t3
				+ (3.0f * v1.y() - 3.0f * y - t0.y() - t0.y() - t1.y()) * t2 + y * t + y;
		dest.z = (z + z - v1.z() - v1.z() + t1.z() + t0.z()) * t3
				+ (3.0f * v1.z() - 3.0f * z - t0.z() - t0.z() - t1.z()) * t2 + z * t + z;
		return dest;
	}

	public float get(int component) throws IllegalArgumentException {
		switch (component) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new IllegalArgumentException();
		}
	}

	public int maxComponent() {
		float absX = Math.abs(x);
		float absY = Math.abs(y);
		float absZ = Math.abs(z);
		if (absX >= absY && absX >= absZ) {
			return 0;
		} else if (absY >= absZ) {
			return 1;
		}
		return 2;
	}

	public int minComponent() {
		float absX = Math.abs(x);
		float absY = Math.abs(y);
		float absZ = Math.abs(z);
		if (absX < absY && absX < absZ) {
			return 0;
		} else if (absY < absZ) {
			return 1;
		}
		return 2;
	}

	public Vector3f orthogonalize(Vector3f v, Vector3f dest) {
		/*
		 * http://lolengine.net/blog/2013/09/21/picking-orthogonal-vector-combing-
		 * coconuts
		 */
		float rx, ry, rz;
		if (Math.abs(v.x()) > Math.abs(v.z())) {
			rx = -v.y();
			ry = v.x();
			rz = 0.0f;
		} else {
			rx = 0.0f;
			ry = -v.z();
			rz = v.y();
		}
		float invLen = 1.0f / (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
		dest.x = rx * invLen;
		dest.y = ry * invLen;
		dest.z = rz * invLen;
		return dest;
	}

	public Vector3f orthogonalize(Vector3f v) {
		return orthogonalize(v, this);
	}

	public Vector3f orthogonalizeUnit(Vector3f v, Vector3f dest) {
		return orthogonalize(v, dest);
	}

	public Vector3f orthogonalizeUnit(Vector3f v) {
		return orthogonalizeUnit(v, this);
	}

	public Vector3f floor() {
		return floor(this);
	}

	public Vector3f floor(Vector3f dest) {
		dest.x = Math.floor(x);
		dest.y = Math.floor(y);
		dest.z = Math.floor(z);
		return dest;
	}

	public Vector3f ceil() {
		return ceil(this);
	}

	public Vector3f ceil(Vector3f dest) {
		dest.x = Math.ceil(x);
		dest.y = Math.ceil(y);
		dest.z = Math.ceil(z);
		return dest;
	}

	public Vector3f round() {
		return round(this);
	}

	public Vector3f round(Vector3f dest) {
		dest.x = Math.round(x);
		dest.y = Math.round(y);
		dest.z = Math.round(z);
		return dest;
	}

	public boolean isFinite() {
		return Math.isFinite(x) && Math.isFinite(y) && Math.isFinite(z);
	}

}
