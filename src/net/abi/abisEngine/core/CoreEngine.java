package net.abi.abisEngine.core;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.util.Objects;

import org.lwjgl.glfw.GLFWErrorCallback;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.mesh.AIMeshLoader;
import net.abi.abisEngine.rendering.window.GLFWWindowManager;
import net.abi.abisEngine.rendering.window.models.EngineLoader;

public class CoreEngine {

	private static Logger logger = LogManager.getLogger(CoreEngine.class.getName());
	private double frameTime, frameRate;
	private boolean isRunning;

	private GLFWErrorCallback errClbk;

	private GLFWWindowManager windowManager;

	public CoreEngine(double framerate) {
		this.isRunning = false;
		this.frameRate = framerate;
		this.frameTime = 1.0 / framerate;
		initGLFW();
		AIMeshLoader.printLibInfo();
	}

	public void initGLFW() {
		/*
		 * Setup an error callback. The default implementation will print the error
		 * message in System.err.
		 */
		glfwSetErrorCallback((errClbk = GLFWErrorCallback.createPrint(System.err)));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			logger.error("Unable to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		windowManager = new GLFWWindowManager(this);
	}

	/**
	 * Starts the engine. And if it is already running dose nothing.
	 */
	public void start() {
		if (isRunning) {
			return;
		}
		run();
	}

	public void stop() {
		if (!isRunning) {
			return;
		}
		logger.info("Terminating Engine...");
		isRunning = false;
	}

	/**
	 * Specifies the entry point to the engine.
	 */
	@Deprecated
	protected void openLoadingWindow() {
		try {
			windowManager.openWindow(new EngineLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run() {
		logger.info("Starting Engine.");
		Runtime runtime = Runtime.getRuntime();
		// openLoadingWindow();
		isRunning = true;

		int frames = 0;
		double frameCounter = 0;

		final double m_frameTime = this.frameTime;
		double lastTime = Time.getTime();
		double unprocessedTime = 0;
		boolean uncapped = true;

		while (isRunning) {
			boolean render = uncapped;
			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;
			unprocessedTime += passedTime;
			frameCounter += passedTime;
			while (isRunning && (unprocessedTime > m_frameTime)) {
				if (GLFWWindowManager.isStopRequested()) {
					stop();
					render = false;
					break;
				} else {
					render = true;
					unprocessedTime -= m_frameTime;
					windowManager.input((float) m_frameTime);
					windowManager.update((float) m_frameTime);
					if (frameCounter >= 1.0) {
						System.out.println("Frames: " + frames + " Frame Time: " + (double) 1000 / frames + "ms");
						// Run the garbage collector
						// runtime.gc();
						long memory = runtime.totalMemory() - runtime.freeMemory();
						System.out.println("Memory Usage: " + memory / (1024L * 1024L) + "MB");
						frames = 0;
						frameCounter = 0;
					}
				}
			}
			if (render) {
				windowManager.render();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		cleanUp();
		System.exit(1);
	}

	private void cleanUp() {
		try {
			windowManager.disposeAllWindows();
		} catch (Exception e) {
			e.printStackTrace();
		}
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}

	public void exit(int exitCode) {
		cleanUp();
		System.runFinalization();
		System.exit(exitCode);
	}

	public double getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
		this.frameTime = 1.0 / frameRate;
	}

	public double getFrameTime() {
		return frameTime;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public GLFWWindowManager getWindowManager() {
		return windowManager;
	}

}
