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
		super(colorInVector3f.x(), colorInVector3f.y(), colorInVector3f.z());
		this.alpha = 1;
	}

	public Color(Vector3f colorInVector3f, float alpha) {
		super(colorInVector3f.x(), colorInVector3f.y(), colorInVector3f.z());
		this.alpha = alpha;
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
