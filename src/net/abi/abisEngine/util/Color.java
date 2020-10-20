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
package net.abi.abisEngine.util;

import net.abi.abisEngine.math.Vector3f;

/**
 * This Class Allows To Have A Alpha Value To Be Set And Wraps Around A Vector3f
 * To Include Alpha As Well.
 * 
 * @author abinash
 *
 */
public class Color extends Vector3f {

	public static final Color DEFAULT_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final int SIZEOF = 4;
	private float alpha;

	public Color(float red, float green, float blue) {
		super(red, green, blue);
		this.alpha = 1.0f;
	}

	public Color(float red, float green, float blue, float alpha) {
		super(red, green, blue);
		this.alpha = alpha;
	}

	public Color(Vector3f colorInVector3f) {
		super(colorInVector3f.x(), colorInVector3f.y(), colorInVector3f.z());
		this.alpha = 1.0f;
	}

	public Color(Vector3f colorInVector3f, float alpha) {
		super(colorInVector3f.x(), colorInVector3f.y(), colorInVector3f.z());
		this.alpha = alpha;
	}

	public Color(String hex) {
		super(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16),
				Integer.valueOf(hex.substring(5, 7), 16));
		this.alpha = 1.0f;
	}

	public Color(String hex, float alpha) {
		super(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16),
				Integer.valueOf(hex.substring(5, 7), 16));
		this.alpha = alpha;
	}

	/**
	 * 
	 * @param colorStr e.g. "#FFFFFF"\
	 * Beware if you are using this for any open gl functions the value needs to be normalized see Vector3f normalize.
	 * @return 
	 */
	public static Color hex2Rgb(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}

	@Override
	public Color normalize() {
		super.normalize();
		return this;
	}

	public float getRed() {
		return super.x();
	}

	public void setRed(float red) {
		super.setX(red);
	}

	public float getGreen() {
		return super.y();
	}

	public void setGreen(float green) {
		super.setY(green);
	}

	public float getBlue() {
		return super.z();
	}

	public void setBlue(float blue) {
		super.setZ(blue);
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

}
