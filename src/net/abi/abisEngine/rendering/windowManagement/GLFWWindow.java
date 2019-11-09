package net.abi.abisEngine.rendering.windowManagement;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowContentScaleCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMaximizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.UUID;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowContentScaleCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowMaximizeCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.rendering.RenderingEngine;
import net.abi.abisEngine.rendering.sceneManagement.Scene;
import net.abi.abisEngine.rendering.sceneManagement.SceneManager;

/**
 * Window Model For The GLFW Context Handle.
 * 
 * @author abinash
 *
 */
public abstract class GLFWWindow {

	public static final long NULL = 0L;

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

	private long glfw_Handle, monitor;
	private int width, height;
	private UUID id;
	private String name, title;
	private boolean fullscreen, vSync;
	private GLFWInput input;
	private SceneManager sceneManager;
	private GLCapabilities capabilities;
	private GLFWFramebufferSizeCallback frmBffrClbk;
	private GLFWWindowCloseCallback wndCloseClbk;
	private GLFWWindowContentScaleCallback wndCntSclClbk;
	private GLFWWindowFocusCallback wndFcsClbk;
	private GLFWWindowIconifyCallback wndIconifyClbk;
	private GLFWWindowMaximizeCallback wndMxmzClbk;
	private GLFWWindowPosCallback wndPosClbk;
	private GLFWWindowSizeCallback wndSizeClbk;
	private GLFWWindowRefreshCallback wndRfrshClbk;

	private RenderingEngine renderEngine;

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
		this.sceneManager = new SceneManager(this);
		this.input = new GLFWInput();
		// this.addToWindowManager();
	}

	/**
	 * Do not call this method it is only called by GLFWWindowManager if there is a
	 * conflict of duplicate id's. NOTE: - This will produce a null pointer
	 * exception if called externally after creation of the window.
	 */
	public void genUniqueID() {
		id = UUID.randomUUID();
	}

	public void setRenderEngine(RenderingEngine rndEng) {
		this.renderEngine = rndEng;
	}

	public RenderingEngine getRenderEngine() {
		if (renderEngine != null) {
			return renderEngine;
		}
		logger.error("Render Engine Not Specified. ", new NullPointerException("Render Engine Not Specified."));
		throw new NullPointerException();
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

	/**
	 * Creates a new window on the primary monitor with a new context. It is not
	 * recommended that you create a window with this method unless you have a way
	 * of keeping track of this window and updating rendering etc. Otherwise use
	 * GLWFWWindowManager's openWindow function.
	 * 
	 * @return
	 */
	public GLFWWindow create() {
		return this.create(NULL);
	}

	/**
	 * Creates a window on the shared context on the primary monitor.
	 * 
	 * @param share
	 * @return
	 */
	public GLFWWindow create(long share) {
		return this.create(glfwGetPrimaryMonitor(), share);
	}

	/**
	 * Creates the window. Must be called from main thread.
	 * 
	 * @param monitor The monitor to create the window on. NULL will create it on
	 *                main monitor.
	 * @param share   The handle of the window the new window will share. NULL will
	 *                make a new Context.
	 * @return
	 */
	public GLFWWindow create(long monitor, long share, RenderingEngine rndEng) {
		this.setRenderEngine(rndEng);
		this.create(monitor, share);
		rndEng.initGraphics(); // Since the capabilities are created and set, and the context is current we can
								// initialize the graphics for this window.
		return this;
	}

	public GLFWWindow create(long monitor, RenderingEngine rndEng) {
		this.setRenderEngine(rndEng);
		this.create(monitor, NULL);
		rndEng.initGraphics(); // Since the capabilities are created and set, and the context is current we can
								// initialize the graphics for this window.
		return this;
	}

	/**
	 * Creates the window. Must be called from main thread.
	 * 
	 * @param monitor The monitor to create the window on. NULL will create it on
	 *                main monitor.
	 * @param share   The handle of the window the new window will share. NULL will
	 *                make a new Context.
	 * @return
	 */
	public GLFWWindow create(long monitor, long share) {
		this.genUniqueID();
		glfw_Handle = glfwCreateWindow(width, height, title, monitor, share);
		if (glfw_Handle == NULL) {
			logger.error("Failed to create the GLFW window: name: '" + name + "' title: '" + title + "'");
			throw new RuntimeException("Failed to create the GLFW window");
		}
		this.monitor = monitor;
		// Make the OpenGL context current
		glfwMakeContextCurrent(glfw_Handle);
		glfwShowWindow(glfw_Handle);

		if (vSync) {
			glfwSwapInterval(1); // Enables V Sync.
		} else {
			glfwSwapInterval(0);
		}
		// This line is critical for LWJGL's inter-operation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		capabilities = GL.createCapabilities();
		// this.coreEngine = GLFWWindowManager.getCoreEngine();
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
		// input.destroySafe();
		glfwFreeCallbacks(glfw_Handle);
		glfwDestroyWindow(glfw_Handle);
		glfw_Handle = NULL;
	}

	public void setAttribute(int attrib, int value) {
		glfwSetWindowAttrib(glfw_Handle, attrib, value);
	}

	public void addScene(Scene scene) {
		sceneManager.addScene(scene);
	}

	public boolean isCloseRequested() {
		return glfwWindowShouldClose(glfw_Handle);
	};

	public void closeWindow() {
		// GLFWWindowManager.closeWindow(this);
		glfwSetWindowShouldClose(glfw_Handle, true);
	}

	public void input(float delta) {
		input.update();
		sceneManager.input(delta);
	}

	public GLFWFramebufferSizeCallback getFrmBffrClbk() {
		return frmBffrClbk;
	}

	public void setFrmBffrClbk(GLFWFramebufferSizeCallback frmBffrClbk) {
		this.frmBffrClbk = frmBffrClbk;
	}

	public long getMonitor() {
		return monitor;
	}

	public void update(float delta) {
		sceneManager.update(delta);
	}

	public void render() {
		sceneManager.render();
		swapBuffers();
	}

	public void showWindow() {
		glfwShowWindow(glfw_Handle);
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
				glfwSetWindowShouldClose(glfw_Handle, true);
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

	public void setvSync(boolean vSync) {
		this.vSync = vSync;
	}

	public UUID getID() {
		return id;
	}

	public long getGlfw_Handle() {
		if (glfw_Handle == NULL) {
			return NULL;
		}
		return glfw_Handle;
	}

	public String getWindowName() {
		return name;
	}

	public GLFWInput getInput() {
		return input;
	}

	public GLCapabilities getCapabilities() {
		return capabilities;
	}

}
