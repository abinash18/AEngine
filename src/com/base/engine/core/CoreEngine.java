package com.base.engine.core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.base.engine.handlers.logging.LogLevel;
import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.sceneManagement.SceneManager;
import com.base.engine.rendering.windowManagement.GLFWWindow;
import com.base.engine.rendering.windowManagement.GLFWWindowManager;
import com.base.engine.rendering.windowManagement.Window;

public class CoreEngine {

	private static Logger logger = LogManager.getLogger(CoreEngine.class.getName());
	private double frameTime, frameRate;
	private boolean isRunning;
	private RenderingEngine renderEngine;
	// Have A Better Way Of Doing This Rather Than Just Make It Static.
	// I.e. Maybe Pass It As A Parameter Through Each Scene.
	public static GLFWWindow currentWindow;

	public CoreEngine(double framerate) {
		this.isRunning = false;
		this.frameRate = framerate;
		this.frameTime = 1.0 / framerate;
		initGLFW();
	}

	public void createWindow(int width, int height, String windowTitle, boolean fullscreen, boolean vSync,
			RenderingEngine rndrEng) {
		// Window.createWindow(width, height, windowTitle, fullscreen, vSync);
		// In The Future We Can Make Multiple Windows Such As Ones Showing Loading Icons
		// And Bars Or Some Other Info And Even Have Multiple Running At The Same Time.
		currentWindow = GLFWWindowManager.getGLFWWindowHandle(width, height, "mainEngineWindow", windowTitle,
				fullscreen, vSync);
		currentWindow.create();
		this.renderEngine = rndrEng;
		logger.info(RenderingEngine.getOpenGLVersion());
		this.printDeviceProperties();
	}

	public void createWindow(int width, int height, String windowTitle, boolean fullscreen, boolean vSync) {
		// Window.createWindow(width, height, windowTitle, fullscreen, vSync);
		currentWindow = GLFWWindowManager.getGLFWWindowHandle(width, height, "mainEngineWindow", windowTitle,
				fullscreen, vSync);
		currentWindow.create();

		this.renderEngine = new RenderingEngine();
		logger.info(RenderingEngine.getOpenGLVersion());
		this.printDeviceProperties();
	}

	public void initGLFW() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			logger.error("Unable to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// This line is critical for LWJGL's inter-operation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

	}

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
		logger.info("Terminating Engine.");
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

				if (currentWindow.isCloseRequested()) {
					stop();
				}

				// Time.setDelta(frameTime);

				SceneManager.input((float) frameTime);
				currentWindow.update();
				SceneManager.update((float) frameTime);

				if (frameCounter >= 1.0) {
					//////////////////////////////////////////////
					//////////// -Just For Debugging-/////////////
					if (!finestLoglevel) {
						System.out.println("Frames: " + frames);
					} else {
						logger.finest("Frames: " + frames);
					}
					//////////// -Just For Debugging-/////////////
					//////////////////////////////////////////////
					frames = 0;
					frameCounter = 0;
				}

			}
			if (render) {
				SceneManager.render();
				currentWindow.swapBuffers();
				// Window.render(frameRate);
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
	}

	private static void cleanUp() {
		GLFWWindowManager.destroyAll();
		// Window.dispose();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static void exit(int exitCode) {
		cleanUp();
		System.runFinalization();
		System.exit(exitCode);
	}

	private void printDeviceProperties() {
		logger.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		logger.info("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS + " bytes");
		logger.info("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
		logger.info("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
		logger.info("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
		logger.info("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");

	}

	public RenderingEngine getRenderEngine() {
		return renderEngine;
	}

}
