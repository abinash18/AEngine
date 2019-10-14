package com.base.engine.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import com.base.engine.handlers.logging.LogLevel;
import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.sceneManagement.SceneManager;
import com.base.engine.rendering.windowManagement.Window;

public class CoreEngine {

	private static Logger logger = LogManager.getLogger(CoreEngine.class.getName());
	private double frameTime, frameRate;
	private boolean isRunning;
	// private RenderingEngine renderEngine;

	public CoreEngine(double framerate) {
		this.isRunning = false;
		this.frameRate = framerate;
		this.frameTime = 1.0 / framerate;
	}

	public void createWindow(int width, int height, String windowTitle, boolean fullscreen, boolean vSync) {
		Window.createWindow(width, height, windowTitle, fullscreen, vSync);
		// this.renderEngine = new RenderingEngine();
		// System.out.println(RenderingEngine.getOpenGLVersion());
		logger.info(RenderingEngine.getOpenGLVersion());
		this.printDeviceProperties();
	}

	public void init() {
	}

	public void start() {
		if (isRunning) {
			return;
		}
		run();
	}

	public void stop() {
		logger.info("Terminating Engine.");
		if (!isRunning) {
			return;
		}
		isRunning = false;
	}

	private void run() {

		logger.info("Starting Engine.");
		SceneManager.init(this);
		isRunning = true;

		int frames = 0;
		double frameCounter = 0;

		final double frameTime = this.frameTime;

		double lastTime = Time.getTime();
		double unprocessedTime = 0;

		boolean finestLoglevel = LogManager.isLevelAllowed(LogLevel.FINEST);

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

				SceneManager.input((float) frameTime);
				Input.update();
				// renderEngine.input((float) frameTime);
				SceneManager.update((float) frameTime);

				if (frameCounter >= 1.0) {
					// System.out.println(frames);
					if (!finestLoglevel) {
						System.out.println("Frames: " + frames);
					} else {
						logger.finest("Frames: " + frames);
					}
					frames = 0;
					frameCounter = 0;
				}
			}
			if (render) {
				SceneManager.render();
				// renderEngine.render(game.getRootObject());
				Window.render(frameRate);
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					// e.printStackTrace();
				}
			}
		}

		cleanUp();
	}

	private static void cleanUp() {
		Window.dispose();
	}

	public static void exit(int exitCode) {
		cleanUp();
		System.runFinalization();
		System.exit(exitCode);
	}

	private void printDeviceProperties() {
//		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
//		System.out.println("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS + " bytes");
//		System.out.println("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
//		System.out.println("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
//		System.out.println("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
//		System.out.println("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");

		logger.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		logger.info("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS + " bytes");
		logger.info("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
		logger.info("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
		logger.info("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
		logger.info("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");

	}

//	public RenderingEngine getRenderEngine() {
//		return renderEngine;
//	}
//
//	public void setRenderEngine(RenderingEngine renderEngine) {
//		this.renderEngine = renderEngine;
//	}

}
