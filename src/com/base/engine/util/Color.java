package com.base.engine.util;

import com.base.engine.math.Vector3f;

/**
 * This Class Allows To Have A Alpha Value To Be Set And Wraps Around A Vector3f To Include Alpha As Well.
 * @author abinash
 *
 */
public class Color extends Vector3f {

	private float red, green, blue, alpha;

	public Color(float red, float green, float blue) {
		super(red, green, blue);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1;
	}

	public Color(float red, float green, float blue, float alpha) {
		super(red, green, blue);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color(Vector3f colorInVector3f) {
		super(colorInVector3f.getX(), colorInVector3f.getY(), colorInVector3f.getZ());
		this.red = colorInVector3f.getX();
		this.green = colorInVector3f.getY();
		this.blue = colorInVector3f.getZ();
		this.alpha = 1;
	}

	public Color(Vector3f colorInVector3f, float alpha) {
		super(colorInVector3f.getX(), colorInVector3f.getY(), colorInVector3f.getZ());
		this.red = colorInVector3f.getX();
		this.green = colorInVector3f.getY();
		this.blue = colorInVector3f.getZ();
		this.alpha = alpha;
	}

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

}
