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

public class Vector2i {
	/**
	 * The x component of the vector.
	 */
	private int x;
	/**
	 * The y component of the vector.
	 */
	private int y;

	/**
	 * Create a new Vector2i and initialize its components to zero.
	 */
	public Vector2i() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Create a new Vector2i and initialize both of its components with the given
	 * value.
	 *
	 * @param s the value of both components
	 */
	public Vector2i(int s) {
		this.x = s;
		this.y = s;
	}

	/**
	 * Create a new Vector2i and initialize its components to the given values.
	 *
	 * @param x the x component
	 * @param y the y component
	 */
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new Vector2i and initialize its components to the one of the given
	 * vector.
	 *
	 * @param v the {@link Vector2ic} to copy the values from
	 */
	public Vector2i(Vector2i v) {
		x = v.x();
		y = v.y();
	}

	public int x() {
		return this.x;
	}

	public int y() {
		return this.y;
	}

	/**
	 * Set the x and y components to the supplied value.
	 *
	 * @param s scalar value of both components
	 * @return this
	 */
	public Vector2i set(int s) {
		return set(s, s);
	}

	public Vector2i set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2i set(Vector2i v) {
		return set(v.x(), v.y());
	}

	public Vector2i sub(int x, int y) {
		this.x = this.x - x;
		this.y = this.y - y;
		return this;
	}

	public long lengthSquared() {
		return lengthSquared(x, y);
	}

	public static long lengthSquared(int x, int y) {
		return x * x + y * y;
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public static double length(int x, int y) {
		return Math.sqrt(lengthSquared(x, y));
	}

	public Vector2i add(Vector2i v) {
		this.x = x + v.x();
		this.y = y + v.y();
		return this;
	}

	public Vector2i add(int x, int y) {
		this.x = this.x + x;
		this.y = this.y + y;
		return this;
	}

	public Vector2i mul(int scalar) {
		this.x = x * scalar;
		this.y = y * scalar;
		return this;
	}

	public Vector2i mul(Vector2i v) {
		this.x = x * v.x();
		this.y = y * v.y();
		return this;
	}

	public Vector2i zero() {
		return this.set(0, 0);
	}

	public Vector2i negate() {
		return negate(this);
	}

	public Vector2i negate(Vector2i dest) {
		dest.x = -x;
		dest.y = -y;
		return dest;
	}

	public Vector2i min(Vector2i v) {
		this.x = x < v.x() ? x : v.x();
		this.y = y < v.y() ? y : v.y();
		return this;
	}

	public Vector2i max(Vector2i v) {
		this.x = x > v.x() ? x : v.x();
		this.y = y > v.y() ? y : v.y();
		return this;
	}

	public int maxComponent() {
		int absX = Math.abs(x);
		int absY = Math.abs(y);
		if (absX >= absY)
			return 0;
		return 1;
	}

	public int minComponent() {
		int absX = Math.abs(x);
		int absY = Math.abs(y);
		if (absX < absY)
			return 0;
		return 1;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Vector2i other = (Vector2i) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	public boolean equals(int x, int y) {
		if (this.x != x)
			return false;
		if (this.y != y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vector2i [x=" + x + ", y=" + y + "]";
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

}
