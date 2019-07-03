package com.base.engine.core;

public class Main {

	public static void main(String[] args) {

		int width = 1920, height = 1080, frameRate = 9999;
		String windowTitle = "3D Engine";

		TestGame game = new TestGame();

		CoreEngine engine = new CoreEngine(frameRate, game);
		engine.createWindow(width, height, windowTitle, true, false);
		engine.start();
	}

}
