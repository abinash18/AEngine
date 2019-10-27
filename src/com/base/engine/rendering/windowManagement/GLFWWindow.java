package com.base.engine.rendering.windowManagement;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.input.GLFWInput;
import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;
import com.base.engine.math.Vector2f;
import com.base.engine.rendering.sceneManagement.Scene;
import com.base.engine.rendering.sceneManagement.SceneManager;

/**
 * Window Model For The GLFW Context Handle.
 * 
 * @author abinash
 *
 */
public abstract class GLFWWindow {
	public static final int GLFW_FOCUSED = 0x20001, GLFW_ICONIFIED = 0x20002, GLFW_RESIZABLE = 0x20003,
			GLFW_VISIBLE = 0x20004, GLFW_DECORATED = 0x20005, GLFW_AUTO_ICONIFY = 0x20006, GLFW_FLOATING = 0x20007,
			GLFW_MAXIMIZED = 0x20008, GLFW_CENTER_CURSOR = 0x20009, GLFW_TRANSPARENT_FRAMEBUFFER = 0x2000A,
			GLFW_HOVERED = 0x2000B, GLFW_FOCUS_ON_SHOW = 0x2000C;
	public static final int GLFW_CLIENT_API = 0x22001, GLFW_CONTEXT_VERSION_MAJOR = 0x22002,
			GLFW_CONTEXT_VERSION_MINOR = 0x22003, GLFW_CONTEXT_REVISION = 0x22004, GLFW_CONTEXT_ROBUSTNESS = 0x22005,
			GLFW_OPENGL_FORWARD_COMPAT = 0x22006, GLFW_OPENGL_DEBUG_CONTEXT = 0x22007, GLFW_OPENGL_PROFILE = 0x22008,
			GLFW_CONTEXT_RELEASE_BEHAVIOR = 0x22009, GLFW_CONTEXT_NO_ERROR = 0x2200A,
			GLFW_CONTEXT_CREATION_API = 0x2200B, GLFW_SCALE_TO_MONITOR = 0x2200C;
	/** PixelFormat hints. */
	public static final int GLFW_RED_BITS = 0x21001, GLFW_GREEN_BITS = 0x21002, GLFW_BLUE_BITS = 0x21003,
			GLFW_ALPHA_BITS = 0x21004, GLFW_DEPTH_BITS = 0x21005, GLFW_STENCIL_BITS = 0x21006,
			GLFW_ACCUM_RED_BITS = 0x21007, GLFW_ACCUM_GREEN_BITS = 0x21008, GLFW_ACCUM_BLUE_BITS = 0x21009,
			GLFW_ACCUM_ALPHA_BITS = 0x2100A, GLFW_AUX_BUFFERS = 0x2100B, GLFW_STEREO = 0x2100C, GLFW_SAMPLES = 0x2100D,
			GLFW_SRGB_CAPABLE = 0x2100E, GLFW_REFRESH_RATE = 0x2100F, GLFW_DOUBLEBUFFER = 0x21010;
	private static Logger logger = LogManager.getLogger(GLFWWindow.class.getName());

	private long glfw_Handle;
	private int width, height;
	private String name, title;
	private boolean fullscreen, vSync, closeRequested;
	private GLFWInput input;

	private SceneManager sceneManager;
	private CoreEngine coreEngine;

	private GLCapabilities capabilities;

	// private GLFWVidMode vidMode;
	private GLFWFramebufferSizeCallback frmBffrClbk;
	private GLFWWindowCloseCallback wndCloseClbk;
	private GLFWWindowContentScaleCallback wndCntSclClbk;
	private GLFWWindowFocusCallback wndFcsClbk;
	private GLFWWindowIconifyCallback wndIconifyClbk;
	private GLFWWindowMaximizeCallback wndMxmzClbk;
	private GLFWWindowPosCallback wndPosClbk;
	private GLFWWindowSizeCallback wndSizeClbk;
	private GLFWWindowRefreshCallback wndRfrshClbk;

	protected abstract void addScenes();

	protected abstract void init();

	protected abstract void close();

	public GLFWWindow(int width, int height, String name, String title, boolean fullscreen, boolean vSync) {
		this.width = width;
		this.height = height;
		this.name = name;
		this.title = title;
		this.fullscreen = fullscreen;
		this.vSync = vSync;
		this.closeRequested = false;
		this.sceneManager = new SceneManager(this);
		this.input = new GLFWInput();
		this.addToWindowManager();
	}

	private void initCallBacks() {

		glfwSetFramebufferSizeCallback(glfw_Handle, (frmBffrClbk = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {

			}
		}));

		glfwSetWindowCloseCallback(glfw_Handle, (wndCloseClbk = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				closeRequested = true;
			}
		}));

		glfwSetWindowContentScaleCallback(glfw_Handle, (wndCntSclClbk = new GLFWWindowContentScaleCallback() {
			@Override
			public void invoke(long window, float xscale, float yscale) {

			}
		}));

		glfwSetWindowFocusCallback(glfw_Handle, (wndFcsClbk = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {

			}
		}));

		glfwSetWindowIconifyCallback(glfw_Handle, (wndIconifyClbk = new GLFWWindowIconifyCallback() {
			@Override
			public void invoke(long window, boolean iconified) {

			}
		}));

		glfwSetWindowMaximizeCallback(glfw_Handle, (wndMxmzClbk = new GLFWWindowMaximizeCallback() {
			@Override
			public void invoke(long window, boolean maximized) {

			}
		}));

		glfwSetWindowPosCallback(glfw_Handle, (wndPosClbk = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {

			}
		}));

		glfwSetWindowRefreshCallback(glfw_Handle, (wndRfrshClbk = new GLFWWindowRefreshCallback() {
			@Override
			public void invoke(long window) {

			}
		}));

		glfwSetWindowSizeCallback(glfw_Handle, (wndSizeClbk = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {

			}
		}));

	}

	private void addToWindowManager() {
		GLFWWindowManager.addWindow(this);
	}

	public void addHints(int hint, int value) {
		glfwWindowHint(hint, value);
	}

	public void resetToDefaults() {
		glfwDefaultWindowHints();
	}

	public void swapBuffers() {
		glfwSwapBuffers(glfw_Handle); // swap the color buffers
	}

	public GLFWWindow create() {
		glfw_Handle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (glfw_Handle == NULL) {
			logger.error("Failed to create the GLFW window: name: '" + name + "' title: '" + title + "'");
			throw new RuntimeException("Failed to create the GLFW window");
		}

		if (vSync) {
			glfwSwapInterval(1); // Enables V Sync.
		}

		// Make the OpenGL context current
		glfwMakeContextCurrent(glfw_Handle);
		glfwShowWindow(glfw_Handle);
		// This line is critical for LWJGL's inter-operation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		capabilities = GL.createCapabilities();
		this.initCallBacks();
		this.input.initInput(glfw_Handle);
		this.addScenes();
		this.init();
		return this;
	}

	/**
	 * Frees callbacks and destroys the window. Also before termination calls the
	 * abstract function close to let specified actions be performed before
	 * terminating.
	 */
	public void dispose() {
		// input.destroy(); // This Will Cause Crashes Because glfw Thinks there still
		// is a call back there even if there isnt and will cause a an access violation.
		// Free the window callbacks and destroy the window
		this.close();
		input.destroySafe();
		glfwFreeCallbacks(glfw_Handle);
		glfwDestroyWindow(glfw_Handle);
	}

	public void setAttribute(int attrib, int value) {
		glfwSetWindowAttrib(glfw_Handle, attrib, value);
	}

	public void addScene(Scene scene) {
		sceneManager.addScene(scene);
	}

	public boolean isCloseRequested() {
		return closeRequested;
	};

	public void input(float delta) {
		input.update();
		sceneManager.input(delta);
	}

	public void update(float delta) {
		sceneManager.update(delta);
	}

	public void render() {
		sceneManager.render();
	}

	public void showWindow() {
		glfwShowWindow(glfw_Handle);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int hight) {
		this.height = hight;
	}

	public Vector2f getCenter() {
		return (new Vector2f(getWidth() / 2, getHeight() / 2));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public boolean isvSync() {
		return vSync;
	}

	public SceneManager getSceneManager() {
		return sceneManager;
	}

	public void setSceneManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
	}

	public CoreEngine getCoreEngine() {
		return coreEngine;
	}

	public void setCoreEngine(CoreEngine coreEngine) {
		this.coreEngine = coreEngine;
	}

	public void setvSync(boolean vSync) {
		this.vSync = vSync;
	}

	public long getGlfw_Handle() {
		return glfw_Handle;
	}

	public String getName() {
		return name;
	}

	public GLFWInput getInput() {
		return input;
	}

	public GLCapabilities getCapabilities() {
		return capabilities;
	}

}
