package com.base.engine;

public class InitializeEngine {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	public static final String TITLE = "Engine";
	
	public static void InitializeEngineGL() {
		Window.createWindow(WIDTH, HEIGHT, TITLE);
	}
	
}
