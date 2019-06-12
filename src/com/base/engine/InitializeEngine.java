package com.base.engine;

public class InitializeEngine {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final String TITLE = "Engine";
	public static final double FRAME_CAP = 5000.0;

	public static void InitializeEngineGL() {

		Window.createWindow(WIDTH, HEIGHT, TITLE);
		RenderUtil.InitGraphics();
		
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public static String getTitle() {
		return TITLE;
	}

	public static double getFrameCap() {
		return FRAME_CAP;
	}

}
