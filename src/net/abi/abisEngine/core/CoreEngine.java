package net.abi.abisEngine.core;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.util.Objects;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import net.abi.abisEngine.handlers.logging.LogLevel;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.mesh.AIMeshLoader;
import net.abi.abisEngine.rendering.renderPipeline.RenderingEngine;
import net.abi.abisEngine.rendering.window.GLFWWindow;
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

	public void initialize() {
		this.printDeviceProperties();
		AIMeshLoader.printLibInfo();
	}

	public void initialize(RenderingEngine rndrEng) {
		this.printDeviceProperties();
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
	protected void openLoadingWindow() {
		try {
			windowManager.openWindow(new EngineLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run() {
		logger.info("Starting Engine.");

		openLoadingWindow();
		isRunning = true;

		int frames = 0;
		double frameCounter = 0;

		final double m_frameTime = this.frameTime;

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
			while (isRunning && (unprocessedTime > m_frameTime)) {
				if (GLFWWindowManager.isStopRequested()) {
					stop();
					render = false;
					break;
				} else {
					render = true;
					unprocessedTime -= m_frameTime;
					windowManager.update((float) m_frameTime);
					windowManager.input((float) m_frameTime);
					if (frameCounter >= 1.0) {
						//////////////////////////////////////////////
						//////////// -Just For Debugging-/////////////
						if (!finestLoglevel) {
							System.out.println("Frames: " + frames + " Frame Time: " + m_frameTime + "ms");
						} else {
							logger.finest("Frames: " + frames);
						}
						//////////// -Just For Debugging-/////////////
						//////////////////////////////////////////////
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

	private void printDeviceProperties() {
		logger.info(RenderingEngine.getOpenGLVersion());
		logger.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		logger.info("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS + " bytes");
		logger.info("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
		logger.info("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
		logger.info("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
		logger.info("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");
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

}
