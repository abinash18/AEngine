package net.abi.abisEngine.rendering.windowManagement;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwGetWindowFrameSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowOpacity;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwRequestWindowAttention;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowContentScaleCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMaximizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowOpacity;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeLimits;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;
import java.util.UUID;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
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
import org.lwjgl.system.MemoryStack;

import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.input.GLFWInput;
import net.abi.abisEngine.input.GLFWMouseAndKeyboardInput;
import net.abi.abisEngine.math.Vector2f;
import net.abi.abisEngine.rendering.RenderingEngine;
import net.abi.abisEngine.rendering.asset.AssetStore;
import net.abi.abisEngine.rendering.asset.AssetManager;
import net.abi.abisEngine.rendering.sceneManagement.Scene;
import net.abi.abisEngine.rendering.sceneManagement.SceneManager;
import net.abi.abisEngine.util.AERuntimeException;

/**
 * Window ModelScene For The GLFW GLContext Handle.
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

	private long glfw_handle, monitor;
	/**
	 * sc_ is the dimensions of the window in screen coordinates, This is different
	 * than pixels since the positive of the y axis is inverted meaning it points
	 * down instead of up so the 0, 0 of the window is in the top left of the
	 * corner.
	 */
	private int sc_width, sc_height,
			/**
			 * The dimensions of the window in pixels this corresponds to the size of the
			 * frame buffer and may not always be the size of the window, since some
			 * displays can have a higher pixel density.
			 */
			p_width, p_height,
			/**
			 * Stores the size of each of the frame elements, if the window is not decorated
			 * than the value is zero.
			 */
			f_top, f_left, f_right, f_bottom;
	/**
	 * Unique id given to the window, this is used if there are multiple windows
	 * with the same names.
	 */
	private UUID id;
	/** The name is what the engine recognizes and it is used to find the window. */
	private String name,
			/**
			 * The title to show on the decorated frame and the general title where ever it
			 * is showed.
			 */
			title;
	private boolean fullscreen,
			/**
			 * This option Synchronizes the frames so they render more steadily instead of
			 * dropping and causing lag.
			 */
			vSync,
			/** If the window is currently focused on or not. */
			focused,
			/** If the window has been minimized to tray (iconified) */
			minimized,
			/** If the window is maximized or not */
			maximized;
	/**
	 * GLFW Supports whole window transparency, but only if the system supports it
	 * as well.
	 */
	private float opacity;
	/** Position of the window in screen coordinates (the top left of the window) */
	private Vector2f position;
	/**
	 * The type of input this window accepts;
	 */
	private GLFWMouseAndKeyboardInput input;
	private SceneManager sceneManager;

	private AssetStore store;
	private AssetManager assetManager;

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

	/**
	 * Scenes should only be added here, since it is systematically called during
	 * the window making process.
	 */
	protected abstract void addScenes();

	/**
	 * This is method where window hints can be set and attributes added. This
	 * method is called before the creation of the window and should be the only
	 * place to set hints and set other constraints. This method should not be
	 * called externally since it can alter the state of the next window that is
	 * created.
	 */
	protected abstract void pre_init();

	/**
	 * Same as pre_init except its called after window creation and only attributes
	 * should be set here. This method is called after creating and before showing
	 * the window.
	 */
	protected abstract void post_init();

	/**
	 * Called before the window is destroyed with the context, here the user can
	 * call last minute methods which require window handles.
	 */
	protected abstract void close();

	public AssetManager getAssetManager() {
		return assetManager;
	}

	/**
	 * Initializes the window instance but dose not create the window.
	 * 
	 * @param sc_width   Width of the window.
	 * @param sc_height  Height of the window.
	 * @param name
	 * @param title
	 * @param fullscreen
	 * @param vSync
	 */
	public GLFWWindow(int sc_width, int sc_height, String name, String title, boolean fullscreen, boolean vSync) {
		this.sc_width = sc_width;
		this.sc_height = sc_height;
		this.name = name;
		this.title = title;
		this.fullscreen = fullscreen;
		this.vSync = vSync;
		this.sceneManager = new SceneManager(this);
		this.input = new GLFWMouseAndKeyboardInput();

		// this.addToWindowManager();
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
	 *                make a new GLContext.
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
	 *                make a new GLContext.
	 * @return
	 */
	public GLFWWindow create(long monitor, long share) {
		this.genUniqueID();
		/*
		 * Pre initialization is for the user to set any window hints or any other
		 * action which dose not require a window handle to be created.
		 */
		this.pre_init();
		this.glfw_handle = glfwCreateWindow(sc_width, sc_height, title, monitor, share);
		if (this.glfw_handle == NULL) {
			logger.error("Failed to create the GLFW window: name: '" + name + "' title: '" + title + "'");
			throw new RuntimeException("Failed to create the GLFW window");
		}

		if (store == null) {
			throw new AERuntimeException("AssetI Store Not Defined.");
		}

		// this.assetManager = new AssetManager(glfw_handle, store);

		this.monitor = monitor;

		if (this.monitor == NULL) {
			this.monitor = glfwGetPrimaryMonitor();
		}
		// Make the OpenGL context current
		glfwMakeContextCurrent(glfw_handle);

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			IntBuffer scWidth = stack.mallocInt(1);
			IntBuffer scHeight = stack.mallocInt(1);
			IntBuffer fTop = stack.mallocInt(1);
			IntBuffer fLeft = stack.mallocInt(1);
			IntBuffer fRight = stack.mallocInt(1);
			IntBuffer fBottom = stack.mallocInt(1);
			IntBuffer xpos = stack.mallocInt(1);
			IntBuffer ypos = stack.mallocInt(1);
			/*
			 * If the system forces us to use different size for the window then this will
			 * be updated and we will know.
			 */
			glfwGetWindowSize(glfw_handle, scWidth, scHeight);
			glfwGetFramebufferSize(glfw_handle, pWidth, pHeight);
			/*
			 * If the window has a decorated edge than the values will be stored here and we
			 * can add them on to the sc_* size if the user requests.
			 */
			glfwGetWindowFrameSize(glfw_handle, fLeft, fTop, fRight, fBottom);

			glfwGetWindowPos(glfw_handle, xpos, ypos);

			sc_width = scWidth.get(0);
			sc_height = scHeight.get(0);
			p_width = pWidth.get(0);
			p_height = pHeight.get(0);
			f_top = fTop.get(0);
			f_left = fLeft.get(0);
			f_right = fRight.get(0);
			f_bottom = fBottom.get(0);

			position = new Vector2f(xpos.get(0), ypos.get(0));

		}

		/*
		 * Post initialization is for the user to center the window set attributes and
		 * change title or border or anything else they need to do before the window is
		 * shown like actions which require window handles.
		 */
		this.post_init();

		/* TODO: let the user decide when or if to show the window. */
		glfwShowWindow(glfw_handle);

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
		this.input.initInput(glfw_handle);
		this.addScenes();
		this.assetManager = new AssetManager(glfw_handle);
		this.resetToDefaults();
		return this;
	}

	/**
	 * This checks if the window has been created or not, if not it throws an
	 * exception
	 */
	private void checkWindowPointer() {
		if (glfw_handle == NULL) {
			logger.error("Handle is invalid.");
			throw new NullPointerException("Handle is invalid for the current call.");
		}
	}

	/**
	 * Do not call this method it is only called by GLFWWindowManager if there is a
	 * conflict of duplicate id's. NOTE: - This will produce a null pointer
	 * exception if called externally after creation of the window.
	 */
	public void genUniqueID() {
		id = UUID.randomUUID();
	}

	public void setAssetStore(AssetStore store) {
		this.store = store;
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

	/**
	 * Sets the size limits of the current created window to ones provided.
	 * 
	 * @param minwidth
	 * @param minheight
	 * @param maxwidth
	 * @param maxheight
	 */
	public void setSizeLimit(int minwidth, int minheight, int maxwidth, int maxheight) {
		checkWindowPointer();
		glfwSetWindowSizeLimits(glfw_handle, minwidth, minheight, maxwidth, maxheight);
	}

	/**
	 * Centers the window on the monitor it was created on.
	 */
	public void centerWindow() {
		checkWindowPointer();

		GLFWVidMode vidmode = glfwGetVideoMode(this.monitor);

		position.set((float) ((vidmode.width() - sc_width) / 2), (float) ((vidmode.height() - sc_height) / 2));
		// Center the window using the sc_* values.
		glfwSetWindowPos(glfw_handle, (int) position.x(), (int) position.y());
	}

	public void addHints(int hint, int value) {
		glfwWindowHint(hint, value);
	}

	public void resetToDefaults() {
		glfwDefaultWindowHints();
	}

	public void swapBuffers() {
		checkWindowPointer();
		glfwSwapBuffers(glfw_handle); // swap the color buffers
	}

	/**
	 * Frees callbacks and destroys the window. Also before termination calls the
	 * abstract function close to let specified actions be performed before
	 * terminating.
	 */
	public void dispose() {
		/* Call user operations before disposal. */
		this.close();
		// input.destroySafe();
		glfwFreeCallbacks(glfw_handle);
		glfwDestroyWindow(glfw_handle);
		glfw_handle = NULL;
	}

	/**
	 * Sets the attribute and the value given to the current window.
	 * 
	 * @param attrib
	 * @param value
	 */
	public void setAttribute(int attrib, int value) {
		checkWindowPointer();
		glfwSetWindowAttrib(glfw_handle, attrib, value);
	}

	/**
	 * Returns a boolean for the attribute given to query.
	 * 
	 * @param attrib
	 * @return
	 */
	public boolean getAttribute(int attrib) {
		checkWindowPointer();
		int temp = glfwGetWindowAttrib(glfw_handle, attrib);
		if (temp == GLFW_TRUE) {
			return true;
		}
		return false;
	}

	public void addScene(Scene scene) {
		sceneManager.addScene(scene);
	}

	public boolean isCloseRequested() {
		checkWindowPointer();
		return glfwWindowShouldClose(glfw_handle);
	};

	public void closeWindow() {
		// GLFWWindowManager.closeWindow(this);
		checkWindowPointer();
		glfwSetWindowShouldClose(glfw_handle, true);
	}

	public void input(float delta) {
		input.update();
		sceneManager.input(delta);
	}

	public void update(float delta) {
		sceneManager.update(delta);
	}

	public void render() {
		sceneManager.render();
		swapBuffers();
	}

	public void showWindow() {
		glfwShowWindow(glfw_handle);
	}

	private void initCallBacks() {

		glfwSetFramebufferSizeCallback(glfw_handle, (frmBffrClbk = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				p_width = width;
				p_height = height;
			}
		}));

		glfwSetWindowCloseCallback(glfw_handle, (wndCloseClbk = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				glfwSetWindowShouldClose(glfw_handle, true);
			}
		}));

		glfwSetWindowContentScaleCallback(glfw_handle, (wndCntSclClbk = new GLFWWindowContentScaleCallback() {
			@Override
			public void invoke(long window, float xscale, float yscale) {

			}
		}));

		glfwSetWindowFocusCallback(glfw_handle, (wndFcsClbk = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focus) {
				focused = focus;
			}
		}));

		glfwSetWindowIconifyCallback(glfw_handle, (wndIconifyClbk = new GLFWWindowIconifyCallback() {
			@Override
			public void invoke(long window, boolean iconified) {
				minimized = iconified;
			}
		}));

		glfwSetWindowMaximizeCallback(glfw_handle, (wndMxmzClbk = new GLFWWindowMaximizeCallback() {
			@Override
			public void invoke(long window, boolean maximize) {
				maximized = maximize;
			}
		}));

		glfwSetWindowPosCallback(glfw_handle, (wndPosClbk = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				position.set(xpos, ypos);
			}
		}));

		glfwSetWindowRefreshCallback(glfw_handle, (wndRfrshClbk = new GLFWWindowRefreshCallback() {
			@Override
			public void invoke(long window) {
				swapBuffers();
			}
		}));

		glfwSetWindowSizeCallback(glfw_handle, (wndSizeClbk = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				sc_width = width;
				sc_height = height;
			}
		}));

	}

	public void setWindowTitle(String title) {
		checkWindowPointer();
		glfwSetWindowTitle(glfw_handle, title);
		this.title = title;
	}

	public void setWindowOpacity(float opacity) {
		checkWindowPointer();
		glfwSetWindowOpacity(glfw_handle, opacity);
		this.opacity = glfwGetWindowOpacity(glfw_handle);
	}

	public float getWindowOpacity() {
		checkWindowPointer();
		return (opacity = glfwGetWindowOpacity(glfw_handle));
	}

	public boolean isFocused() {
		return this.getAttribute(GLFW_FOCUSED);
	}

	public void focusWindow() {
		checkWindowPointer();
		glfwFocusWindow(glfw_handle);
	}

	public void requestFocus() {
		checkWindowPointer();
		glfwRequestWindowAttention(glfw_handle);
	}

	public void restoreWindow() {
		checkWindowPointer();
		glfwRestoreWindow(glfw_handle);
	}

	public boolean isMinimized() {
		return this.getAttribute(GLFW_ICONIFIED);
	}

	public void minimizeWindow() {
		checkWindowPointer();
		glfwIconifyWindow(glfw_handle);
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

	public Vector2f getCenter() {
		return (new Vector2f(getPWidth() / 2, getPHeight() / 2));
	}

	/**
	 * Returns width in pixels
	 * 
	 * @return
	 */
	public int getPWidth() {
		return p_width;
	}

	public int getPHeight() {
		return p_height;
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
		if (glfw_handle == NULL) {
			return NULL;
		}
		return glfw_handle;
	}

	public String getWindowName() {
		return name;
	}

	public <T> T getInput() {
		return (T) input;
	}

	public GLCapabilities getCapabilities() {
		return capabilities;
	}

}
