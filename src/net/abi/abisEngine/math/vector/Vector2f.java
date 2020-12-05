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
package net.abi.abisEngine.math.vector;

import org.joml.Math;

public class Vector2f {
	protected float x;
	protected float y;

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float dot(Vector2f r) {
		return x * r.x() + y * r.y();
	}

	public Vector2f normalize() {
		float length = length();

		x /= length;
		y /= length;

		return this;
	}

	public float angle(Vector2f v) {
		float dot = x * v.x() + y * v.y();
		float det = x * v.y() - y * v.x();
		return (float) Math.atan2(det, dot);
	}

	public float distance(Vector2f v) {
		return distance(v.x(), v.y());
	}

	public float distanceSquared(Vector2f v) {
		return distanceSquared(v.x(), v.y());
	}

	public float distance(float x, float y) {
		float dx = this.x - x;
		float dy = this.y - y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public float distanceSquared(float x, float y) {
		float dx = this.x - x;
		float dy = this.y - y;
		return dx * dx + dy * dy;
	}

	public static float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(distanceSquared(x1, y1, x2, y2));
	}

	public static float distanceSquared(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return dx * dx + dy * dy;
	}

	public Vector2f rotate(float angle) {
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);

		return new Vector2f((float) (x * cos - y * sin), (float) (x * sin + y * cos));
	}

	public float cross(Vector3f r) {
		return (x * r.y() - y * r.x());
	}

	public Vector2f zero() {
		return this.set(0, 0);
	}

	public Vector2f negate() {
		return negate(this);
	}

	public Vector2f negate(Vector2f dest) {
		dest.x = -x;
		dest.y = -y;
		return dest;
	}

	public float max() {
		return Math.max(x, y);
	}

	public Vector2f lerp(Vector2f dest, float lerpFactor) {
		return (dest.sub(this).mul(lerpFactor).add(this));
	}

	public boolean equals(Vector2f r) {
		return (x == r.x() && y == r.y());
	}

	public Vector2f add(Vector2f r) {
		return new Vector2f(x + r.x(), y + r.y());
	}

	public Vector2f add(float r) {
		return new Vector2f(x + r, y + r);
	}

	public Vector2f sub(Vector2f r) {
		return new Vector2f(x - r.x(), y - r.y());
	}

	public Vector2f sub(float r) {
		return new Vector2f(x - r, y - r);
	}

	public Vector2f mul(Vector2f r) {
		return new Vector2f(x * r.x(), y * r.y());
	}

	public Vector2f mul(float r) {
		return new Vector2f(x * r, y * r);
	}

	public Vector2f div(Vector2f r) {
		return new Vector2f(x / r.x(), y / r.y());
	}

	public Vector2f div(float r) {
		return new Vector2f(x / r, y / r);
	}

	public Vector2f abs() {
		return (new Vector2f(Math.abs(x), Math.abs(y)));
	}

	@Override
	public String toString() {
		return "(" + x + " " + y + ")";
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

	public Vector2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2f set(Vector2f other) {
		this.set(other.x(), other.y());
		return this;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	public Vector2f fma(Vector2f a, Vector2f b) {
		return fma(a, b, this);
	}

	public Vector2f fma(float a, Vector2f b) {
		return fma(a, b, this);
	}

	public Vector2f fma(Vector2f a, Vector2f b, Vector2f dest) {
		dest.x = x + a.x() * b.x();
		dest.y = y + a.y() * b.y();
		return dest;
	}

	public Vector2f fma(float a, Vector2f b, Vector2f dest) {
		dest.x = x + a * b.x();
		dest.y = y + a * b.y();
		return dest;
	}

	public Vector2f min(Vector2f v) {
		return min(v, this);
	}

	public Vector2f min(Vector2f v, Vector2f dest) {
		dest.x = x < v.x() ? x : v.x();
		dest.y = y < v.y() ? y : v.y();
		return dest;
	}

	public Vector2f max(Vector2f v) {
		return max(v, this);
	}

	public Vector2f max(Vector2f v, Vector2f dest) {
		dest.x = x > v.x() ? x : v.x();
		dest.y = y > v.y() ? y : v.y();
		return dest;
	}

	public int maxComponent() {
		float absX = Math.abs(x);
		float absY = Math.abs(y);
		if (absX >= absY)
			return 0;
		return 1;
	}

	public int minComponent() {
		float absX = Math.abs(x);
		float absY = Math.abs(y);
		if (absX < absY)
			return 0;
		return 1;
	}

	public Vector2f floor() {
		return floor(this);
	}

	public Vector2f floor(Vector2f dest) {
		dest.x = Math.floor(x);
		dest.y = Math.floor(y);
		return dest;
	}

	public Vector2f ceil() {
		return ceil(this);
	}

	public Vector2f ceil(Vector2f dest) {
		dest.x = Math.ceil(x);
		dest.y = Math.ceil(y);
		return dest;
	}

	public Vector2f round() {
		return ceil(this);
	}

	public Vector2f round(Vector2f dest) {
		dest.x = Math.round(x);
		dest.y = Math.round(y);
		return dest;
	}

	public boolean isFinite() {
		return Math.isFinite(x) && Math.isFinite(y);
	}

}
