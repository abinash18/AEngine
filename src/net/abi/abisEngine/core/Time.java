package net.abi.abisEngine.core;

public class Time {
	private static final long SECOND = 1000000000L;

	@Deprecated
	private static double delta;

	public static double getTime() {
		return ((double) System.nanoTime() / (double) SECOND);
	}

	@Deprecated
	public static double getDelta() {
		return delta;
	}

	@Deprecated
	public static void setDelta(double delta) {
		Time.delta = delta;
	}
}
