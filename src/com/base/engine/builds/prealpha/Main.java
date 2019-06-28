package com.base.engine.builds.prealpha;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.TestGame;

public class Main {

	public static void main(String[] args) {

		int width = 800, height = 600, frameRate = 60;
		String windowTitle = "3D Engine";

		TestGame game = new TestGame();
		
		CoreEngine engine = new CoreEngine(frameRate, game);
		engine.createWindow(width, height, windowTitle);
		engine.start();
	}

}
