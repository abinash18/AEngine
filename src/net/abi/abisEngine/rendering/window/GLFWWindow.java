/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.window;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
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
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwRequestWindowAttention;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowContentScaleCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMaximizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
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
import org.lwjgl.glfw.GLFWImage;
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
import net.abi.abisEngine.input.GLFWMouseAndKeyboardInput;
import net.abi.abisEngine.math.vector.Vector2f;
import net.abi.abisEngine.math.vector.Vector2i;
import net.abi.abisEngine.rendering.asset.AssetManager;
import net.abi.abisEngine.rendering.asset.AssetStore;
import net.abi.abisEngine.rendering.image.AEImage;
import net.abi.abisEngine.rendering.material.Material;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.scene.Scene;
import net.abi.abisEngine.rendering.scene.SceneManager;
import net.abi.abisEngine.util.Expendable;
import net.abi.abisEngine.util.cacheing.GenericCache;
import net.abi.abisEngine.util.exceptions.AECursorInitializationException;
import net.abi.abisEngine.util.exceptions.AEGLFWWindowInitializationException;

public abstract class GLFWWindow implements Expendable {

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
	/** Boolean values. */
	public static final int GLFW_TRUE = 1, GLFW_FALSE = 0;

	private static Logger logger = LogManager.getLogger(GLFWWindow.class.getName());

	public static GenericCache<String, StaticCursorResource> cursors = new GenericCache<String, StaticCursorResource>(
			String.class, StaticCursorResource.class);

	/**
	 * Properties and preferences
	 */
	protected GLFWWindowProperties properties;

	/**
	 * These are actualized values, these values are unique to each window,
	 * exempting monitor which can be altered because of monitors.
	 */
	private long glfw_handle, currentMonitor, currentRefreshRate;

	/**
	 * Unique id given to the window, this is used if there are multiple windows
	 * with the same names.
	 */
	public UUID id;

	public GLFWVidMode videoMode;

	/**
	 * The type of input this window accepts;
	 */
	private GLFWMouseAndKeyboardInput input;
	private SceneManager sceneManager;
	private AssetStore store;
	private AssetManager assetManager;

	/**
	 * Scenes should only be added here, since it is systematically called during
	 * the window making process.
	 */
	protected abstract void addScenes();

	/**
	 * This is method where window hints can be set. This method is called before
	 * the creation of the window and should be the only place to set hints and set
	 * other constraints. This method should not be called externally since it can
	 * alter the state of the next window that is created.
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
	 * call last minute methods which require window handles. But Dont call the
	 * engine to stop becasue the window is already destroyed after and is removed
	 * from GLFW context.
	 */
	protected abstract void close();

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public GLFWWindow() {
		this(new GLFWWindowProperties());
	}

	public GLFWWindow(GLFWWindowProperties props) {
		this.properties = props;
		this.sceneManager = new SceneManager(this);
		this.input = new GLFWMouseAndKeyboardInput();
	}

	public GLFWWindow(int sc_width, int sc_height, String name, String title, boolean fullscreen, int vSync) {
		this(sc_width, sc_height, name, title, fullscreen, vSync, 0);
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
	public GLFWWindow(int sc_width, int sc_height, String name, String title, boolean fullscreen, int vSync,
			long preferedMonitor) {
		this.properties = new GLFWWindowProperties();
		this.properties.sc_width = sc_width;
		this.properties.sc_height = sc_height;
		this.properties.name = name;
		this.properties.title = title;
		this.properties.fullscreen = fullscreen;
		this.properties.vSync = vSync;
		this.properties.preferredMonitor = preferedMonitor;
		this.sceneManager = new SceneManager(this);
		this.input = new GLFWMouseAndKeyboardInput();

	}

	/**
	 * Creates a window on the shared context on the preferred monitor.
	 * 
	 * @param share
	 * @return
	 * @throws AEGLFWWindowInitializationException
	 */
	public GLFWWindow create(long share) throws AEGLFWWindowInitializationException {
		return this.create(share);
	}

	/**
	 * Creates the window. Must be called from main thread.
	 * 
	 * @param monitor The monitor to create the window on. NULL will create it on
	 *                main monitor.
	 * @param share   The handle of the window the new window will share. NULL will
	 *                make a new GLContext.
	 * @return
	 * @throws AEGLFWWindowInitializationException
	 */
	public GLFWWindow create(long monitor, long share, RenderingEngine rndEng)
			throws AEGLFWWindowInitializationException {
		this.properties.sharedContext = share;
		this.create(monitor, rndEng);
		return this;
	}

	/**
	 * This monitor will be considered the preferred monitor.
	 * 
	 * @param monitor
	 * @param rndEng
	 * @return
	 * @throws AEGLFWWindowInitializationException
	 */
	public GLFWWindow create(long monitor, RenderingEngine rndEng) throws AEGLFWWindowInitializationException {
		this.properties.preferredMonitor = monitor;
		this.create(rndEng);
		return this;
	}

	public GLFWWindow create(RenderingEngine rndEng) throws AEGLFWWindowInitializationException {
		properties.renderEngine = rndEng;
		this.create();
		return this;
	}

	/**
	 * Creates a new window on the primary monitor with a new context. It is not
	 * recommended that you create a window with this method unless you have a way
	 * of keeping track of this window and updating rendering etc. Otherwise use
	 * GLWFWWindowManager's openWindow function. But this instance if created
	 * explicitly through user code and not the engine's window manager this window
	 * will not be updated automatically through the core engine.
	 * 
	 * @return
	 * @throws AEGLFWWindowInitializationException
	 */
	public GLFWWindow create() throws AEGLFWWindowInitializationException {
		this.genUniqueID();

		if (this.properties.preferredMonitor == NULL) {
			this.properties.preferredMonitor = glfwGetPrimaryMonitor();
		}

		this.currentMonitor = properties.preferredMonitor;
		long mon = properties.preferredMonitor;

		videoMode = glfwGetVideoMode(currentMonitor);
		currentRefreshRate = videoMode.refreshRate();

		addGLFWWindowHint(GLFW_REFRESH_RATE, properties.preferredRefreshRate);
		addGLFWWindowHint(GLFW_VISIBLE, properties.startHidden);

		/*
		 * Pre initialization is for the user to set any window hints or any other
		 * action which dose not require a window handle to be created.
		 */
		this.pre_init();

		/**
		 * This is done because creating a window defaults to full screen and if the
		 * user wants it full screen it will be other wise we can change it like so and
		 * then move the window to the proper monitor later.
		 */
		if (!properties.fullscreen) {
			mon = NULL;
		}

		this.glfw_handle = glfwCreateWindow(properties.sc_width, properties.sc_height, properties.title, mon,
				properties.sharedContext);

		/*
		 * To check if the window was created without errors this checks if glfw
		 * provided a handle, if it is NULL then a AEWindowInitializationException is
		 * thrown.
		 */
		if (this.glfw_handle == NULL) {
			logger.error("Failed to create the GLFW window: name: '" + properties.name + "' title: '" + properties.title
					+ "'");
			throw new AEGLFWWindowInitializationException(
					"Failed to create the GLFW window, Either GLFW denied to create this context or it failed.", this);
		}

		/* If there is no store provided there is no place to cache assets. */
		if (store == null) {
			throw new AEGLFWWindowInitializationException("Asset Store Not Defined.", this);
		}

		// this.assetManager = new AssetManager(glfw_handle, store);

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
			properties.p_width = pWidth.get(0);
			properties.sc_width = scWidth.get(0);
			properties.p_height = pHeight.get(0);
			properties.sc_height = scHeight.get(0);

			properties.f_top = fTop.get(0);
			properties.f_left = fLeft.get(0);
			properties.f_right = fRight.get(0);
			properties.f_bottom = fBottom.get(0);

			properties.position = new Vector2i(xpos.get(0), ypos.get(0));

		}

		/*
		 * Post initialization is for the user to center the window set attributes and
		 * change title or border or anything else they need to do before the window is
		 * shown like actions which require window handles.
		 */
		this.post_init();

		/* TODO: let the user decide when or if to show the window. */
		// glfwShowWindow(glfw_handle);
		glfwSwapInterval(this.properties.vSync); // Enables V Sync.
		/*
		 * This line is critical for LWJGL's inter-operation with GLFW's OpenGL context,
		 * or any context that is managed externally. LWJGL detects the context that is
		 * current in the current thread, creates the GLCapabilities instance and makes
		 * the OpenGL bindings available for use.
		 */
		this.properties.capabilities = GL.createCapabilities();
		this.initCallBacks();
		this.input.initInput(glfw_handle);
		this.addScenes();
		this.assetManager = new AssetManager(glfw_handle);
		this.resetToDefaults();
		this.properties.renderEngine.initGraphics();

		Material.DEFAULT_MATERIAL.getTexture("default").load();

		return this;
	}

	public void setCursorType(int cursor) {

	}

	/**
	 * Sets the preferred monitor for this window. meaning what monitor this window
	 * is supposed to be on, is going to be on, or the default for it. it can also
	 * be used to set the target location for the setMonitor() function.
	 */
	public void setPreferredMonitor(long prfdMntr) {
		this.properties.preferredMonitor = prfdMntr;
	}

	/**
	 * Moves the window to the monitor described in the preferredMonitor variable or
	 * in the setPreferredMonitor() function.
	 */
	public void setMonitor() {
		glfwSetWindowMonitor(glfw_handle, properties.preferredMonitor, properties.position.x(), properties.position.y(),
				properties.sc_width, properties.sc_height, properties.preferredRefreshRate);
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

	public void setCursor(CursorI cursor) throws AECursorInitializationException {
		glfwSetCursor(glfw_handle, cursor.create());
	}

	/**
	 * Sets the window onto the preferred monitor as defined in properties and also
	 * carries all other setting over so to change the window on the other monitor
	 * change the properties before moving.
	 */
	public void setWindowMonitor() {
		long mon = NULL;
		if (properties.fullscreen) {
			mon = properties.preferredMonitor;
		}
		glfwSetWindowMonitor(glfw_handle, mon, properties.position.x(), properties.position.y(), properties.sc_width,
				properties.sc_height, properties.preferredRefreshRate);
	}

	public void setWindowPosition(Vector2i newPos) {
		setWindowPosition(newPos.x(), newPos.y());
	}

	/**
	 * Sets the windows position on the preferred monitor.
	 */
	public void setWindowPosition(int xpos, int ypos) {
		glfwSetWindowPos(glfw_handle, xpos, ypos);
		this.properties.position.set(xpos, ypos);
	}

	/**
	 * Icons can be set to null by setting the first element of the array to null to
	 * reset to default platform window icon.
	 * 
	 * @param icons
	 */
	public void setWindowIcon(AEImage... icons) {
		if (icons[0] == null) {
			glfwSetWindowIcon(glfw_handle, null);
			return;
		}

		GLFWImage[] i = new GLFWImage[icons.length];
		GLFWImage.Buffer ibf = GLFWImage.malloc(icons.length);
		for (int j = 0; j < icons.length; j++) {
			i[j] = GLFWImage.malloc();
			i[j].set(icons[j].getImageMetaData().width, icons[j].getImageMetaData().height,
					icons[j].getData().getPixelsInByteBuffer());
			ibf.put(j, i[j]);
		}
		// ibf.flip();
		glfwSetWindowIcon(glfw_handle, ibf);
		for (int j = 0; j < icons.length; j++) {
			i[j].free();
		}
		ibf.free();
	}

	/**
	 * Changes the video mode to full screen or vice versa
	 * 
	 * @param fullscreen
	 */
	public void setFullscreen(boolean fullscreen) {
		this.properties.fullscreen = fullscreen;
		this.setWindowMonitor();
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
		this.properties.renderEngine = rndEng;
	}

	public RenderingEngine getRenderEngine() {
		if (properties.renderEngine != null) {
			return properties.renderEngine;
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

		GLFWVidMode vidmode = glfwGetVideoMode(this.currentMonitor);

		properties.position.set(((vidmode.width() - properties.sc_width) / 2),
				((vidmode.height() - properties.sc_height) / 2));
		// Center the window using the sc_* values.
		glfwSetWindowPos(glfw_handle, properties.position.x(), properties.position.y());
	}

	public void addGLFWWindowHint(int hint, int value) {
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
		if (glfw_handle == NULL) {
			return;
		}
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

	public void hideWindow() {
		glfwHideWindow(glfw_handle);
	}

	private void initCallBacks() {

		glfwSetFramebufferSizeCallback(glfw_handle, (properties.frmBffrClbk = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				properties.p_width = width;
				properties.p_height = height;
			}
		}));

		glfwSetWindowCloseCallback(glfw_handle, (properties.wndCloseClbk = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				glfwSetWindowShouldClose(glfw_handle, true);
			}
		}));

		glfwSetWindowContentScaleCallback(glfw_handle,
				(properties.wndCntSclClbk = new GLFWWindowContentScaleCallback() {
					@Override
					public void invoke(long window, float xscale, float yscale) {

					}
				}));

		glfwSetWindowFocusCallback(glfw_handle, (properties.wndFcsClbk = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focus) {
				properties.focused = focus;
			}
		}));

		glfwSetWindowIconifyCallback(glfw_handle, (properties.wndIconifyClbk = new GLFWWindowIconifyCallback() {
			@Override
			public void invoke(long window, boolean iconified) {
				properties.minimized = iconified;
			}
		}));

		glfwSetWindowMaximizeCallback(glfw_handle, (properties.wndMxmzClbk = new GLFWWindowMaximizeCallback() {
			@Override
			public void invoke(long window, boolean maximize) {
				properties.maximized = maximize;
			}
		}));

		glfwSetWindowPosCallback(glfw_handle, (properties.wndPosClbk = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				properties.position.set(xpos, ypos);
			}
		}));

		glfwSetWindowRefreshCallback(glfw_handle, (properties.wndRfrshClbk = new GLFWWindowRefreshCallback() {
			@Override
			public void invoke(long window) {
				swapBuffers();
			}
		}));

		glfwSetWindowSizeCallback(glfw_handle, (properties.wndSizeClbk = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				properties.sc_width = width;
				properties.sc_height = height;
			}
		}));

	}

	public void toggleVSync() {
		properties.vSync = properties.vSync == GLFW_TRUE ? GLFW_FALSE : GLFW_TRUE;
		glfwSwapInterval(properties.vSync);
	}

	public void toggleFullScreen() {
		this.setFullscreen(!properties.fullscreen);
	}

	public void setWindowTitle(String title) {
		checkWindowPointer();
		glfwSetWindowTitle(glfw_handle, title);
		this.properties.title = title;
	}

	public void setWindowOpacity(float opacity) {
		checkWindowPointer();
		glfwSetWindowOpacity(glfw_handle, opacity);
		this.properties.opacity = glfwGetWindowOpacity(glfw_handle);
	}

	public float getWindowOpacity() {
		checkWindowPointer();
		return (properties.opacity = glfwGetWindowOpacity(glfw_handle));
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

	/*
	 * public GLFWFramebufferSizeCallback getFrmBffrClbk() { return frmBffrClbk; }
	 * 
	 * public void setFrmBffrClbk(GLFWFramebufferSizeCallback frmBffrClbk) {
	 * this.frmBffrClbk = frmBffrClbk; }
	 */

	public long getMonitor() {
		return currentMonitor;
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
		return properties.p_width;
	}

	public int getPHeight() {
		return properties.p_height;
	}

	public String getTitle() {
		return properties.title;
	}

	public void setTitle(String title) {
		this.properties.title = title;
	}

	public boolean isFullscreen() {
		return properties.fullscreen;
	}

	public int isvSync() {
		return properties.vSync;
	}

	public SceneManager getSceneManager() {
		return sceneManager;
	}

	public void setSceneManager(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
	}

	public void setvSync(int vSync) {
		this.properties.vSync = vSync;
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
		return properties.name;
	}

	public GLCapabilities getCapabilities() {
		return properties.capabilities;
	}

	@Override
	public String toString() {
		return "GLFWWindow [monitor=" + currentMonitor + ", id=" + id + ", name=" + properties.name + ", title="
				+ properties.title + "]";
	}

	public <T> T getInput() {
		return (T) input;
	}

	public GLFWWindowProperties getProperties() {
		return properties;
	}

	public void setProperties(GLFWWindowProperties properties) {
		this.properties = properties;
	}

}
