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

import net.abi.abisEngine.math.matrix.Matrix4f;
import net.abi.abisEngine.math.vector.Vector3f;
import net.abi.abisEngine.math.vector.Vector3i;

public class Quaternionf {
	private float x, y, z, w;

	@Deprecated
	public Quaternionf() {
		this(0, 0, 0, 1);
	}

	public Quaternionf(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternionf(Quaternionf r) {
		this(r.x(), r.y(), r.z(), r.w());
	}

	public Quaternionf(Vector3f axis, float anglerad) {
		float sinHalfAngle = (float) Math.sin(anglerad / 2);
		float cosHalfAngle = (float) Math.cos(anglerad / 2);

		this.x = axis.x() * sinHalfAngle;
		this.y = axis.y() * sinHalfAngle;
		this.z = axis.z() * sinHalfAngle;
		this.w = cosHalfAngle;

	}

	@Deprecated
	public Quaternionf initRotationRad(Vector3f axis, float angleInRadians) {

		float sinHalfAngle = (float) Math.sin(angleInRadians / 2);
		float cosHalfAngle = (float) Math.cos(angleInRadians / 2);

		this.x = axis.x() * sinHalfAngle;
		this.y = axis.y() * sinHalfAngle;
		this.z = axis.z() * sinHalfAngle;
		this.w = cosHalfAngle;

		return (this);

	}

	public Quaternionf(Matrix4f rot) {
		float trace = rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2);
		if (trace > 0) {
			float s = 0.5f / (float) Math.sqrt(trace + 1.0f);
			w = 0.25f / s;
			x = (rot.get(1, 2) - rot.get(2, 1)) * s;
			y = (rot.get(2, 0) - rot.get(0, 2)) * s;
			z = (rot.get(0, 1) - rot.get(1, 0)) * s;
		} else {
			if (rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2)) {
				float s = 2.0f * (float) Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
				w = (rot.get(1, 2) - rot.get(2, 1)) / s;
				x = 0.25f * s;
				y = (rot.get(1, 0) + rot.get(0, 1)) / s;
				z = (rot.get(2, 0) + rot.get(0, 2)) / s;
			} else if (rot.get(1, 1) > rot.get(2, 2)) {
				float s = 2.0f * (float) Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
				w = (rot.get(2, 0) - rot.get(0, 2)) / s;
				x = (rot.get(1, 0) + rot.get(0, 1)) / s;
				y = 0.25f * s;
				z = (rot.get(2, 1) + rot.get(1, 2)) / s;
			} else {
				float s = 2.0f * (float) Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
				w = (rot.get(0, 1) - rot.get(1, 0)) / s;
				x = (rot.get(2, 0) + rot.get(0, 2)) / s;
				y = (rot.get(1, 2) + rot.get(2, 1)) / s;
				z = 0.25f * s;
			}
		}

		this.normalize();
	}

	public Quaternionf rotate(Vector3f axis, float angleRad) {
		float hangle = angleRad / 2.0f;
		float sinAngle = (float) Math.sin(hangle);
		float invVLength = (float) (1.0 / Math.sqrt(axis.x() * axis.x() + axis.y() * axis.y() + axis.z() * axis.z()));

		x = axis.x() * invVLength * sinAngle;
		y = axis.y() * invVLength * sinAngle;
		z = axis.z() * invVLength * sinAngle;
		w = (float) Math.cosFromSin(sinAngle, hangle);
		return (this);
	}

	public boolean equals(Quaternionf r) {
		return (x == r.x() && y == r.y() && z == r.z() && w == r.w());
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public Quaternionf normalize() {
		float length = length();

		return new Quaternionf(x / length, y / length, z / length, w / length);
	}

	public Quaternionf conjugate() {
		return new Quaternionf(-x, -y, -z, w);
	}

	public Matrix4f toRotationMatrix() {

		Vector3f forward = new Vector3f(2.0f * (x * z - w * y), 2.0f * (y * z + w * x), 1.0f - 2.0f * (x * x + y * y));
		Vector3f up = new Vector3f(2.0f * (x * y + w * z), 1.0f - 2.0f * (x * x + z * z), 2.0f * (y * z - w * x));
		Vector3f right = new Vector3f(1.0f - 2.0f * (y * y + z * z), 2.0f * (x * y - w * z), 2.0f * (x * z + w * y));

		return new Matrix4f().initRotation(forward, up, right);
	}

	public Vector3f getForward() {
		return new Vector3f(0, 0, 1).rotate(this);
	}

	public Vector3f getBack() {
		return new Vector3f(0, 0, -1).rotate(this);
	}

	public Vector3f getUp() {
		return new Vector3f(0, 1, 0).rotate(this);
	}

	public Vector3f getDown() {
		return new Vector3f(0, -1, 0).rotate(this);
	}

	public Vector3f getRight() {
		return new Vector3f(1, 0, 0).rotate(this);
	}

	public Vector3f getLeft() {
		return new Vector3f(-1, 0, 0).rotate(this);
	}

	public Quaternionf mul(float r) {
		return new Quaternionf(x * r, y * r, z * r, w * r);
	}

	public Quaternionf mul(Quaternionf r) {
		float w_ = w * r.w() - x * r.x() - y * r.y() - z * r.z();
		float x_ = x * r.w() + w * r.x() + y * r.z() - z * r.y();
		float y_ = y * r.w() + w * r.y() + z * r.x() - x * r.z();
		float z_ = z * r.w() + w * r.z() + x * r.y() - y * r.x();

		return new Quaternionf(x_, y_, z_, w_);
	}

	public Quaternionf mul(Vector3f r) {
		float w_ = -x * r.x() - y * r.y() - z * r.z();
		float x_ = w * r.x() + y * r.z() - z * r.y();
		float y_ = w * r.y() + z * r.x() - x * r.z();
		float z_ = w * r.z() + x * r.y() - y * r.x();

		return new Quaternionf(x_, y_, z_, w_);
	}

	public Quaternionf mul(Vector3i r) {
		float w_ = -x * r.x() - y * r.y() - z * r.z();
		float x_ = w * r.x() + y * r.z() - z * r.y();
		float y_ = w * r.y() + z * r.x() - x * r.z();
		float z_ = w * r.z() + x * r.y() - y * r.x();

		return new Quaternionf(x_, y_, z_, w_);
	}

	public Quaternionf set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public Quaternionf sub(Quaternionf r) {
		return new Quaternionf(x - r.x(), y - r.y(), z - r.z(), w - r.w());
	}

	public Quaternionf sub(float x, float y, float z, float w) {
		return new Quaternionf(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	public Quaternionf add(Quaternionf r) {
		return new Quaternionf(x + r.x(), y + r.y(), z + r.z(), w + r.w());
	}

	public Quaternionf add(float x, float y, float z, float w) {
		return new Quaternionf(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	public float dot(Quaternionf r) {
		return (x * r.x() + y * r.y() + z * r.z() + w * r.w());
	}

	public float dot(float x, float y, float z, float w) {
		return (this.x * x + this.y * y + this.z * z + this.w * w);
	}

	public Quaternionf nlerp(Quaternionf dest, float lerpFactor, boolean shortest) {
		Quaternionf correctDestination = dest;
		if (shortest && this.dot(dest) < 0) {
			correctDestination = new Quaternionf(dest.mul(-1));
		}
		return correctDestination.sub(this).mul(lerpFactor).add(this).normalize();
	}

	public Quaternionf slerp(Quaternionf dest, float lerpFactor, boolean shortest) {
		final float EPSILON = 1e3f;

		float cos = this.dot(dest);
		Quaternionf correctDestination = dest;

		if (shortest && cos < 0) {
			cos = -cos;
			correctDestination = new Quaternionf(dest.mul(-1));
		}

		if (Math.abs(cos) >= 1 - EPSILON) {
			return nlerp(correctDestination, lerpFactor, false);
		}

		float sin = (float) Math.sqrt(1.0f - cos * cos);
		float angle = (float) Math.atan2(sin, cos);
		float invSin = 1.0f / sin;

		float srcFactor = (float) Math.sin((1.0f - lerpFactor) * angle) * invSin;
		float destFactor = (float) Math.sin((lerpFactor) * angle) * invSin;

		return this.mul(srcFactor).add(correctDestination.mul(destFactor));
	}

	public Quaternionf set(Quaternionf other) {
		this.set(other.x(), other.y(), other.z(), other.w());
		return this;
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
}
