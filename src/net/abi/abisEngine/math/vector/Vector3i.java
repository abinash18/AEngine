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

import net.abi.abisEngine.math.Math;

public class Vector3i {
	public int x;
	public int y;
	public int z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float dot(Vector3i r) {
		return x * r.x() + y * r.y() + z * r.z();
	}

	public Vector3i cross(Vector3i r) {
		int x_ = y * r.z() - z * r.y();
		int y_ = z * r.x() - x * r.z();
		int z_ = x * r.y() - y * r.x();

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
		this.set(other.x(), other.y(), other.z());
		return this;
	}

	public boolean equals(Vector3i r) {
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

	public Vector3i add(Vector3i r) {
		return new Vector3i(x + r.x(), y + r.y(), z + r.z());
	}

	public Vector3i add(int r) {
		return new Vector3i(x + r, y + r, z + r);
	}

	public Vector3i sub(Vector3i r) {
		return new Vector3i(x - r.x(), y - r.y(), z - r.z());
	}

	public Vector3i sub(int r) {
		return new Vector3i(x - r, y - r, z - r);
	}

	public Vector3i mul(Vector3i r) {
		return new Vector3i(x * r.x(), y * r.y(), z * r.z());
	}

	public Vector3i mul(int r) {
		return new Vector3i(x * r, y * r, z * r);
	}

	public Vector3i div(Vector3i r) {
		return new Vector3i(x / r.x(), y / r.y(), z / r.z());
	}

	public Vector3i div(int r) {
		return new Vector3i(x / r, y / r, z / r);
	}

	public Vector3i abs() {
		return (new Vector3i(Math.abs(x), Math.abs(y), Math.abs(z)));
	}

	public int x() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int y() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int z() {
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
