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
import net.abi.abisEngine.rendering.RenderingEngine;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;
import net.abi.abisEngine.rendering.windowManagement.GLFWWindowManager;
import net.abi.abisEngine.rendering.windowManagement.models.EngineLoader;

public class CoreEngine {

	private static Logger logger = LogManager.getLogger(CoreEngine.class.getName());
	private double frameTime, frameRate;
	private boolean isRunning;
	private RenderingEngine renderEngine;
	// Have A Better Way Of Doing This Rather Than Just Make It Static.
	// I.e. Maybe Pass It As A Parameter Through Each Scene.
	// private GLFWWindow currentWindow;

	@SuppressWarnings("unused")
	private GLFWErrorCallback errClbk;

	public CoreEngine(double framerate) {
		this.isRunning = false;
		this.frameRate = framerate;
		this.frameTime = 1.0 / framerate;
		initGLFW();
	}

	public void initialize() {
		// GLFWWindowManager.getCurrentWindow().create();
		this.renderEngine = new RenderingEngine();
		this.printDeviceProperties();
	}

	public void initialize(RenderingEngine rndrEng) {
		// GLFWWindowManager.getCurrentWindow().create();
		this.renderEngine = rndrEng;
		this.printDeviceProperties();
	}

	public void initGLFW() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback(errClbk = GLFWErrorCallback.createPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			logger.error("Unable to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}
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

	/**
	 * Specifies the entry point to the engine.
	 */
	protected void openLoadingWindow() {
		try {
			GLFWWindowManager.openWindow(new EngineLoader(), GLFWWindow.NULL, new RenderingEngine());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run() {
		logger.info("Starting Engine.");

		GLFWWindowManager.init(this);
		openLoadingWindow();
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

			while (isRunning && (unprocessedTime > frameTime)) {

				if (GLFWWindowManager.isStopRequested()) {
					stop();
					render = false;
					System.out.println(isRunning);
					continue;
				} else {
					render = true;
					unprocessedTime -= frameTime;
					GLFWWindowManager.input((float) frameTime);
					GLFWWindowManager.update((float) frameTime);

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
			}
			if (render) {
				GLFWWindowManager.render();
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

	private void cleanUp() {
		GLFWWindowManager.destroyAll();
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

	public RenderingEngine getRenderEngine() {
		return renderEngine;
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
