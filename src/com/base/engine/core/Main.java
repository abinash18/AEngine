package com.base.engine.core;

import com.base.engine.internalGame.Game;

public class Main {

	public static void main(String[] args) {

		int width = 800, height = 600, frameRate = 60;
		String windowTitle = "3D Engine";

		CoreEngine engine = new CoreEngine(frameRate, new TestGame());
		engine.createWindow(width, height, windowTitle);
		engine.start();
	}

}
