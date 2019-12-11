/**
 * 
 */
package net.abi.abisEngine.math;

/**
 * @author abinash
 *
 */
public class Math {

	public static final double PI = java.lang.Math.PI;
	static final double PI2 = PI * 2.0;
	static final double PIHalf = PI * 0.5;
	static final double PI_4 = PI * 0.25;
	static final double PI_INV = 1.0 / PI;

	public static double cosFromSin(double sin, double angle) {
		// if (Options.FASTMATH)
		// return sin(angle + PIHalf);
		// sin(x)^2 + cos(x)^2 = 1
		double cos = Math.sqrt(1.0 - sin * sin);
		double a = angle + PIHalf;
		double b = a - (int) (a / PI2) * PI2;
		if (b < 0.0)
			b = PI2 + b;
		if (b >= PI)
			return -cos;
		return cos;
	}

	public static double sin(double rad) {
		// if (Options.FASTMATH) {
		// if (Options.SIN_LOOKUP)
		// return sin_theagentd_lookup(rad);
		// return sin_roquen_newk(rad);
		// }
		return java.lang.Math.sin(rad);
	}

	public static double cos(double rad) {
		// if (Options.FASTMATH)
		// return sin(rad + PIHalf);
		return java.lang.Math.cos(rad);
	}

	/* Other math functions not yet approximated */

	public static double sqrt(double r) {
		return java.lang.Math.sqrt(r);
	}

	public static double tan(double r) {
		return java.lang.Math.tan(r);
	}

	public static double acos(double r) {
		return java.lang.Math.acos(r);
	}

	public static double atan2(double y, double x) {
		return java.lang.Math.atan2(y, x);
	}

	public static double asin(double r) {
		return java.lang.Math.asin(r);
	}

	public static double abs(double r) {
		return java.lang.Math.abs(r);
	}

	public static float abs(float r) {
		return java.lang.Math.abs(r);
	}

	public static int abs(int r) {
		return java.lang.Math.abs(r);
	}

	public static int max(int x, int y) {
		return java.lang.Math.max(x, y);
	}

	public static int min(int x, int y) {
		return java.lang.Math.min(x, y);
	}

	public static float min(float a, float b) {
		return a < b ? a : b;
	}

	public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static double min(double a, double b) {
		return a < b ? a : b;
	}

	public static double max(double a, double b) {
		return a > b ? a : b;
	}

	public static double toRadians(double angles) {
		return java.lang.Math.toRadians(angles);
	}

	public static double toDegrees(double angles) {
		return java.lang.Math.toDegrees(angles);
	}

	public static double floor(double v) {
		return java.lang.Math.floor(v);
	}

	public static float floor(float v) {
		return (float) java.lang.Math.floor(v);
	}

	public static double ceil(double v) {
		return java.lang.Math.ceil(v);
	}

	public static float ceil(float v) {
		return (float) java.lang.Math.ceil(v);
	}

	public static long round(double v) {
		return java.lang.Math.round(v);
	}

	public static int round(float v) {
		return java.lang.Math.round(v);
	}

	public static double exp(double a) {
		return java.lang.Math.exp(a);
	}

	public static boolean isFinite(double d) {
		return abs(d) <= Double.MAX_VALUE;
	}

	public static boolean isFinite(float f) {
		return abs(f) <= Float.MAX_VALUE;
	}

}
