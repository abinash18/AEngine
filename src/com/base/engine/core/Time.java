package com.base.engine.core;

public class Time {
	private static final long SECOND = 1000000000L;

	private static double delta;

	public static double getTime() {
		return ((double) System.nanoTime() / (double) SECOND);
	}

//	public static double getDelta() {
//		return delta;
//	}
//
//	public static void setDelta(double delta) {
//		Time.delta = delta;
//	}
}
