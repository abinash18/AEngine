package com.base.engine.core;

import com.base.engine.internalGame.Game;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Window;

public class CoreEngine {

	private double frameTime, frameRate;
	private boolean isRunning;
	private Game game;
	private RenderingEngine renderEngine;

	public CoreEngine(double framerate, Game game) {
		this.isRunning = false;
		this.game = game;
		this.frameRate = framerate;
		this.frameTime = 1.0 / framerate;

	}

	public void createWindow(int width, int height, String windowTitle, boolean fullscreen, boolean vSync) {

		Window.createWindow(width, height, windowTitle, fullscreen, vSync);
		this.renderEngine = new RenderingEngine();
		System.out.println(RenderingEngine.getOpenGLVersion());
	}

	public void init() {
		game.getRootObject().init();
	}

	public void start() {
		if (isRunning)
			return;

		run();
	}

	public void stop() {
		if (!isRunning)
			return;

		isRunning = false;
	}

	private void run() {
		game.init();
		isRunning = true;

		int frames = 0;
		double frameCounter = 0;

		final double frameTime = this.frameTime;

		double lastTime = Time.getTime();
		double unprocessedTime = 0;

		while (isRunning) {
			boolean render = false;

			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime;
			frameCounter += passedTime;

			while (unprocessedTime > frameTime) {
				render = true;

				unprocessedTime -= frameTime;

				if (Window.isCloseRequested()) {
					stop();
				}

				// Time.setDelta(frameTime);

				game.input((float) frameTime);
				Input.update();
				renderEngine.input((float) frameTime);
				game.update((float) frameTime);

				if (frameCounter >= 1.0) {
					System.out.println(frames);
					frames = 0;
					frameCounter = 0;
				}
			}
			if (render) {
				renderEngine.render(game.getRootObject());
				Window.render(frameRate);
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		cleanUp();
	}

	private void cleanUp() {
		Window.dispose();
	}

}
