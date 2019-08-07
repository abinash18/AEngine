package com.base.engine.util;

import com.base.engine.math.Vector3f;

/**
 * This Class Allows To Have A Alpha Value To Be Set And Wraps Around A Vector3f
 * To Include Alpha As Well.
 * 
 * @author abinash
 *
 */
public class Color extends Vector3f {

	private float alpha;

	public Color(float red, float green, float blue) {
		super(red, green, blue);
		this.alpha = 1;
	}

	public Color(float red, float green, float blue, float alpha) {
		super(red, green, blue);
		this.alpha = alpha;
	}

	public Color(Vector3f colorInVector3f) {
		super(colorInVector3f.getX(), colorInVector3f.getY(), colorInVector3f.getZ());
		this.alpha = 1;
	}

	public Color(Vector3f colorInVector3f, float alpha) {
		super(colorInVector3f.getX(), colorInVector3f.getY(), colorInVector3f.getZ());
		this.alpha = alpha;
	}

	public float getRed() {
		return super.getX();
	}

	public void setRed(float red) {
		super.setX(red);
	}

	public float getGreen() {
		return super.getY();
	}

	public void setGreen(float green) {
		super.setY(green);
	}

	public float getBlue() {
		return super.getZ();
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
