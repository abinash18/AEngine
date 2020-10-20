/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.math;

import net.abi.abisEngine.math.Math;

public class Vector4f implements Vector4fI {
	private float x;
	private float y;
	private float z;
	private float w;

	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float length3f() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float length4f() {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public float dot(Vector4f r) {
		return x * r.x() + y * r.y() + z * r.z() + w * r.w();
	}

	public float dot(float x, float y, float z, float w) {
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}

	public float angleCos(Vector4f v) {
		double length1Squared = x * x + y * y + z * z + w * w;
		double length2Squared = v.x() * v.x() + v.y() * v.y() + v.z() * v.z() + v.w() * v.w();
		double dot = x * v.x() + y * v.y() + z * v.z() + w * v.w();
		return (float) (dot / (Math.sqrt(length1Squared * length2Squared)));
	}

	public Vector4f rotateAxisInternal(float angle, float aX, float aY, float aZ) {
		return rotateAxisInternal(angle, aX, aY, aZ, this);
	}

	public Vector4f rotateAxisInternal(float angle, float aX, float aY, float aZ, Vector4f dest) {
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

	public float angle(Vector4f v) {
		float cos = angleCos(v);
		// This is because sometimes cos goes above 1 or below -1 because of lost
		// precision
		cos = cos < 1 ? cos : 1;
		cos = cos > -1 ? cos : -1;
		return (float) Math.acos(cos);
	}

	public Vector4f rotateX(float angle, Vector4f dest) {
		float sin = (float) Math.sin(angle), cos = (float) Math.cosFromSin(sin, angle);
		float y = this.y * cos - this.z * sin;
		float z = this.y * sin + this.z * cos;
		dest.x = this.x;
		dest.y = y;
		dest.z = z;
		dest.w = this.w;
		return dest;
	}

	public Vector4f rotateY(float angle) {
		return rotateY(angle, this);
	}

	public Vector4f rotateY(float angle, Vector4f dest) {
		float sin = (float) Math.sin(angle), cos = (float) Math.cosFromSin(sin, angle);
		float x = this.x * cos + this.z * sin;
		float z = -this.x * sin + this.z * cos;
		dest.x = x;
		dest.y = this.y;
		dest.z = z;
		dest.w = this.w;
		return dest;
	}

	public Vector4f rotateZ(float angle) {
		return rotateZ(angle, this);
	}

	public Vector4f rotateZ(float angle, Vector4f dest) {
		float sin = (float) Math.sin(angle), cos = (float) Math.cosFromSin(sin, angle);
		float x = this.x * cos - this.y * sin;
		float y = this.x * sin + this.y * cos;
		dest.x = x;
		dest.y = y;
		dest.z = this.z;
		dest.w = this.w;
		return dest;
	}

	public Vector3f normalize3f() {
		float length = length3f();

		x /= length;
		y /= length;
		z /= length;

		return new Vector3f(x, y, z);
	}

	public Vector4f normalize4f() {
		float length = length4f();

		x /= length;
		y /= length;
		z /= length;
		w /= length;

		return new Vector4f(x, y, z, w);
	}

	public float distance(Vector4f v) {
		return distance(v.x(), v.y(), v.z(), v.w());
	}

	public float distance(float x, float y, float z, float w) {
		return (float) Math.sqrt(distanceSquared(x, y, z, w));
	}

	public float distanceSquared(Vector4f v) {
		return distanceSquared(v.x(), v.y(), v.z(), v.w());
	}

	public float distanceSquared(float x, float y, float z, float w) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;
		float dw = this.w - w;
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	public Vector4f zero() {
		return this.set(0, 0, 0, 0);
	}

	public Vector4f negate() {
		return negate(this);
	}

	public Vector4f negate(Vector4f dest) {
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		dest.w = -w;
		return dest;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(w);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	public Vector4f smoothStep(Vector4f v, float t) {
		float t2 = t * t;
		float t3 = t2 * t;
		this.x = (x + x - v.x() - v.x()) * t3 + (3.0f * v.x() - 3.0f * x) * t2 + x * t + x;
		this.y = (y + y - v.y() - v.y()) * t3 + (3.0f * v.y() - 3.0f * y) * t2 + y * t + y;
		this.z = (z + z - v.z() - v.z()) * t3 + (3.0f * v.z() - 3.0f * z) * t2 + z * t + z;
		this.w = (w + w - v.w() - v.w()) * t3 + (3.0f * v.w() - 3.0f * w) * t2 + w * t + w;
		return this;
	}

	public Vector4f hermite(Vector4f t0, Vector4f v1, Vector4f t1, float t) {
		float t2 = t * t;
		float t3 = t2 * t;
		this.x = (x + x - v1.x() - v1.x() + t1.x() + t0.x()) * t3
				+ (3.0f * v1.x() - 3.0f * x - t0.x() - t0.x() - t1.x()) * t2 + x * t + x;
		this.y = (y + y - v1.y() - v1.y() + t1.y() + t0.y()) * t3
				+ (3.0f * v1.y() - 3.0f * y - t0.y() - t0.y() - t1.y()) * t2 + y * t + y;
		this.z = (z + z - v1.z() - v1.z() + t1.z() + t0.z()) * t3
				+ (3.0f * v1.z() - 3.0f * z - t0.z() - t0.z() - t1.z()) * t2 + z * t + z;
		this.w = (w + w - v1.w() - v1.w() + t1.w() + t0.w()) * t3
				+ (3.0f * v1.w() - 3.0f * w - t0.w() - t0.w() - t1.w()) * t2 + w * t + w;
		return this;
	}

	public float get(int component) throws IllegalArgumentException {
		switch (component) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		case 3:
			return w;
		default:
			throw new IllegalArgumentException();
		}
	}

	public int maxComponent() {
		float absX = Math.abs(x);
		float absY = Math.abs(y);
		float absZ = Math.abs(z);
		float absW = Math.abs(w);
		if (absX >= absY && absX >= absZ && absX >= absW) {
			return 0;
		} else if (absY >= absZ && absY >= absW) {
			return 1;
		} else if (absZ >= absW) {
			return 2;
		}
		return 3;
	}

	public int minComponent() {
		float absX = Math.abs(x);
		float absY = Math.abs(y);
		float absZ = Math.abs(z);
		float absW = Math.abs(w);
		if (absX < absY && absX < absZ && absX < absW) {
			return 0;
		} else if (absY < absZ && absY < absW) {
			return 1;
		} else if (absZ < absW) {
			return 2;
		}
		return 3;
	}

	public Vector4f floor() {
		return floor(this);
	}

	public Vector4f floor(Vector4f dest) {
		dest.x = Math.floor(x);
		dest.y = Math.floor(y);
		dest.z = Math.floor(z);
		dest.w = Math.floor(w);
		return dest;
	}

	public Vector4f ceil() {
		return ceil(this);
	}

	public Vector4f ceil(Vector4f dest) {
		dest.x = Math.ceil(x);
		dest.y = Math.ceil(y);
		dest.z = Math.ceil(z);
		dest.w = Math.ceil(w);
		return dest;
	}

	public Vector4f round() {
		return round(this);
	}

	public Vector4f round(Vector4f dest) {
		dest.x = Math.round(x);
		dest.y = Math.round(y);
		dest.z = Math.round(z);
		dest.w = Math.round(w);
		return dest;
	}

	public boolean isFinite() {
		return Math.isFinite(x) && Math.isFinite(y) && Math.isFinite(z) && Math.isFinite(w);
	}

	public float max() {
		return Math.max(x, Math.max(y, z));
	}

	public float min() {
		return Math.min(x, Math.min(y, z));
	}

	public Vector4f set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public Vector4f set(Vector4f v) {
		this.set(v.x(), v.y(), v.z(), v.w());
		return this;
	}

	public Vector4f lerp(Vector4f dest, float lerpFactor) {
		return (dest.sub(this).mul(lerpFactor).add(this));
	}

	public boolean equals(Vector3f r) {
		return (x == r.x() && y == r.y() && z == r.z());
	}

	public Vector2f xy() {
		return (new Vector2f(x, y));
	}

	public Vector2f yz() {
		return (new Vector2f(y, z));
	}

	public Vector2f zx() {
		return (new Vector2f(z, x));
	}

	public Vector2f yx() {
		return (new Vector2f(y, x));
	}

	public Vector2f zy() {
		return (new Vector2f(z, y));
	}

	public Vector2f xz() {
		return (new Vector2f(x, z));
	}

	public Vector4f add(Vector4f r) {
		return new Vector4f(x + r.x(), y + r.y(), z + r.z(), w + r.w());
	}

	public Vector4f add(float r) {
		return new Vector4f(x + r, y + r, z + r, w + r);
	}

	public Vector4f sub(Vector4f r) {
		return new Vector4f(x - r.x(), y - r.y(), z - r.z(), w - r.w());
	}

	public Vector4f sub(float r) {
		return new Vector4f(x - r, y - r, z - r, w - r);
	}

	public Vector4f mul(Vector4f r) {
		return new Vector4f(x * r.x(), y * r.y(), z * r.z(), w * r.w());
	}

	public Vector4f mul(float r) {
		return new Vector4f(x * r, y * r, z * r, w * r);
	}

	public Vector4f div(Vector4f r) {
		return new Vector4f(x / r.x(), y / r.y(), z / r.z(), w / r.w());
	}

	public Vector4f div(float r) {
		return new Vector4f(x / r, y / r, z / r, w / r);
	}

	public Vector4f abs() {
		return new Vector4f(Math.abs(x), Math.abs(y), Math.abs(z), Math.abs(w));
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

	public float w() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	@Override
	public String toString() {
		return "Vector3f [x=" + x + ", y=" + y + ", z=" + z + "] (" + x + ", " + y + ", " + z + ")";
	}

}
