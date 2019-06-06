package com.base.engine;

import org.lwjgl.Sys;

public class MainComponent {

	private boolean isRuning;

	public MainComponent() {
		isRuning = false;
	}

	public void start() {

		if (isRuning) {
			return;
		}
		run();

	}

	public void stop() {

		if (!isRuning) {
			return;
		}

		isRuning = false;

	}

	private void run() {

		isRuning = true;

		final double frameTime = 1.0 / InitializeEngine.FRAME_CAP;

		long lastTime = Time.getTime();
		double unprocessedTime = 0;

		while (isRuning) {

			int frames = 0;
			long frameCounter = 0;
			boolean render = false;
			long startTime = Time.getTime();
			long passedTime = startTime - lastTime;

			lastTime = startTime;
			unprocessedTime += passedTime / (double) Time.SECOND;

			while (unprocessedTime > frameTime) {

				render = true;

				unprocessedTime -= frameTime;
				frameCounter += passedTime;

				if (Window.isCloseRequested()) {
					stop();
				}

				// TODO: Update Game

				if (frameCounter >= Time.SECOND) {
					System.out.println(frames);
					frameCounter = 0;
					frames = 0;
				}
				
			}

			if (render) {

				render();
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

	private void render() {

		Window.render();

	}

	private void cleanUp() {

	}

	public static void main(String[] args) {

		////////////////////////////////////////////////////
		// Temporary Parameters.
		////////////////////////////////////////////////////

		////////////////////////////////////////////////////

		System.out.println("Engine Initializing.");

		InitializeEngine.InitializeEngineGL();

		MainComponent game = new MainComponent();

		game.start();

		System.out.println("Engine Terminating.");

	}

}
