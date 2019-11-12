package net.abi.abisEngine.rendering.windowManagement;

import static net.abi.abisEngine.rendering.windowManagement.GLFWWindow.NULL;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL;

import net.abi.abisEngine.core.CoreEngine;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.RenderingEngine;

/**
 * GLFW Implementation Of Window. This Implementation supports shared contexts.
 * 
 * @author abinash
 */
public class GLFWWindowManager {
	/*
	 * NOTE to self: After about 6 windows it will start having a visible impact on
	 * performance, maybe render clusters of windows on different threads at a time
	 * if a context contains 6 or more windows, or even render each window on a
	 * different thread, or even render windows on a different thread than the core
	 * engine.F
	 */

	// TODO: Make a better exception for throwing when there is a failure to create
	// windows.
	private static final Logger logger = LogManager.getLogger(GLFWWindowManager.class.getName());
	// private static Map<String, GLFWWindow> activeWindows = new HashMap<String,
	// GLFWWindow>();
	/*
	 * Bucket list of contexts and windows which share context. The name of the
	 * context is the name of the window that shares its context
	 */
	private static Map<Context, CopyOnWriteArrayList<GLFWWindow>> sharedContexts = new ConcurrentHashMap<Context, CopyOnWriteArrayList<GLFWWindow>>();
	/*
	 * Core engine really has no use in this class for now since we have a separate
	 * rendering engine for each context.
	 */
	private static CoreEngine coreEngine;
	/* If this is set to true the engine will terminate. */
	private static boolean engineStopFlag = false;

	/**
	 * Class defining Context. This is the key to all mapped windows it contains the
	 * parent context that all windows that are mapped to it will share. The first
	 * window added to the map will have it's handle stored as the context value.
	 * 
	 * @author abinash
	 *
	 */
	public static class Context {
		/* Handle of the stored context */
		public long context = NULL;
		/* Name of the window which this context belongs to. */
		public String name;

		/*
		 * The Rendering Engine needs to be referenced too, since my engine yet dose not
		 * support independent engines for rendering, such as 2d and 3d contexts, so
		 * each window will either have its own engine if it dose not share context. or
		 * it will inherit an engine from the parent window's Context. So if you only
		 * want to render 2d on a window you can do so, thus saving resources, by not
		 * allocating unnecessary openGL context for 3d rendering, if there are multiple
		 * windows which you know will all render in either 2d or 3d just let them share
		 * context with one window which already has the 2d rendering engine allocated
		 * to it. But this method comes with risks, if the parent window is closed all
		 * windows inheriting the context will also be destroyed, to avoid this either
		 * hide the window by setting a window hint or create windows with independent
		 * context with 2d Rendering Engines.
		 */
		public RenderingEngine renderEngine;

		public Context(String name, long context, RenderingEngine rndEng) {
			this.context = context;
			this.name = name;
			this.renderEngine = rndEng;
		}

	}

	/**
	 * Sets the current context to the one provided.
	 * 
	 * @param context the context to set to.
	 * @return Returns true if the context has been set successfully. Otherwise
	 *         false is returned if the context invalid.
	 */
	private static void setContext(Context context) {
		glfwMakeContextCurrent(context.context);
	}

	private static void setContext(long context) {
		glfwMakeContextCurrent(context);
	}

	/**
	 * Renders all windows stored in the bucket list, and the windows which inherit
	 * their context.
	 */
	public static void render() {

		/*
		 * Since using a for loop iterating over the hash map will cause a concurrent
		 * modification exception, we use the iterator which iterates over the entris in
		 * the map.
		 */
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			/*
			 * Current Entry, we cannot call next() on the iterator every time we need the
			 * entry because then it will skip to the next entry in line.
			 */
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			/* Iterates over the windows which share the context. */
			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				if (wnd.getGlfw_Handle() == NULL) {
					break;
				}

				/*
				 * TODO: Add Background Render optimization by breaking out of the loop if the
				 * window is not in focus or iconified.
				 */
				/*
				 * if (!wnd.isFocused()) { break; }
				 */

				/*
				 * Sets the current context to the one provided in the entry so we can render
				 * that context.
				 */
				setContext(wnd.getGlfw_Handle());
				/*
				 * This will set the capabilities to render the current context on the window.
				 */
				GL.setCapabilities(wnd.getCapabilities());
				/* We are ready to render the context now. */
				wnd.render();
			}

		}
		setContext(NULL);
		/*
		 * We must set the capabilities to null, because we dont want any operations
		 * done any where else to affect the last window we just rendered.
		 */
		GL.setCapabilities(null);
	}

	/**
	 * Returns either true or false if the window has requested close or not,
	 * respectively.
	 */
	private static boolean checkClose(GLFWWindow wnd) {
		if (wnd.isCloseRequested()) {
			return true;
		}
		return false;
	}

	public static void input(float delta) {
		/* If there are no contexts left stop the engine. */
		if (sharedContexts.size() == 0) {
			raiseStopFlag();
			return;
		}

		/* Iterates over everything contained in the hash map to */
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {

			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			for (int i = 0; i < entry.getValue().size(); i++) {
				GLFWWindow wnd = entry.getValue().get(i);

				/* Check if the window is destroyed already or not */
				if (wnd.getGlfw_Handle() != NULL) {
					if (entry.getKey().context == NULL) {
						entry.getKey().context = wnd.getGlfw_Handle();
					}
					/*
					 * Sets the current context to the one provided in the entry so we can render
					 * that context.
					 */
					setContext(wnd.getGlfw_Handle());
					GL.setCapabilities(wnd.getCapabilities());
					/*
					 * These operations are done in here because I call input first core engine. It
					 * would not make sense and have terrible consequences if it was done in a
					 * method called later.
					 */
					if (checkClose(wnd)) {
						/* Dispose the window destroying its context and capability's. */
						wnd.dispose();
						logger.debug("Window Destroyed. " + wnd.getWindowName());

						/*
						 * Break, so we don't cause a null pointer exception if we try updating the
						 * input for a empty window and one we have removed from the array list.
						 */
						break;
					}
					/* Updates the input for the window. */
					wnd.input(delta);
				} else { /* If so then delete the entry. */
					/* Remove the window from the list. */
					entry.getValue().remove(i);
				}

			}

//			for (Iterator<GLFWWindow> subEntry = entry.getValue().iterator(); subEntry.hasNext();) {
//				GLFWWindow wnd = subEntry.next();
//				/* Check if the window is destroyed already or not */
//				if (wnd.getGlfw_Handle() != NULL) {
//					if (entry.getKey().context == NULL) {
//						entry.getKey().context = wnd.getGlfw_Handle();
//					}
//					/*
//					 * Sets the current context to the one provided in the entry so we can render
//					 * that context.
//					 */
//					setContext(wnd.getGlfw_Handle());
//					GL.setCapabilities(wnd.getCapabilities());
//					/*
//					 * These operations are done in here because I call input first core engine. It
//					 * would not make sense and have terrible consequences if it was done in a
//					 * method called later.
//					 */
//					if (checkClose(wnd)) {
//						/* Dispose the window destroying its context and capability's. */
//						wnd.dispose();
//						logger.debug("Window Destroyed. " + wnd.getWindowName());
//
//						/*
//						 * Break, so we don't cause a null pointer exception if we try updating the
//						 * input for a empty window and one we have removed from the array list.
//						 */
//						break;
//					}
//					/* Updates the input for the window. */
//					wnd.input(delta);
//				} else { /* If so then delete the entry. */
//					/* Remove the window from the list. */
//					subEntry.remove();
//				}
//			}

		}
		/*
		 * Resets the context and capabilities to NULL so no action called on the
		 * current thread after the loop will have effect.
		 */
		setContext(NULL);
		GL.setCapabilities(null);
	}

	public static void update(float delta) {
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {

			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				/*
				 * Sets the current context to the one provided in the entry so we can render
				 * that context.
				 */
				setContext(wnd.getGlfw_Handle());
				GL.setCapabilities(wnd.getCapabilities());
				wnd.update(delta);
			}

		}
		setContext(NULL);
		GL.setCapabilities(null);

	}

	/**
	 * Gets the window specified.
	 * 
	 * @param name
	 * @return
	 */
	public static GLFWWindow getGLFWWindow(String name) {
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				if (name.equals(wnd.getWindowName())) {
					return wnd;
				}
			}
		}
		logger.debug("No Window Found With Name: " + name);
		return null;
	}

	/**
	 * Finds the specific context in the shared contexts bucket list.
	 * 
	 * @param name Name of the context to search for.
	 * @return Returns null if no context was found with the specified name, else
	 *         returns the entry in the sharedContexts Bucket list.
	 */
	public static Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> getContext(String name) {
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			if (entry.getKey().name.equals(name)) {
				return entry;
			}
		}
		logger.debug("No Window Found With Name: " + name);
		return null;
	}

	private static void setCoreEngine(CoreEngine creng) {
		GLFWWindowManager.coreEngine = creng;
	}

	public static void init(CoreEngine coreEngine) {
		setCoreEngine(coreEngine);
	}

	/**
	 * Destroys all active contexts. This is executed before the engine terminates.
	 * Throws Exception.
	 */
	public static void destroyAll() throws Exception {
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				wnd.dispose();
				subEntrys.remove();
				break;
			}
			entries.remove();
		}
	}

	/**
	 * Destroys all windows in a active context. Throws Exception.
	 */
	public static void destroyAll(Context context) throws Exception {
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			/*
			 * If the context being iterated over is the same as the one provided then
			 * execute.
			 */
			if (entry.getKey().name == context.name) {
				for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
					GLFWWindow wnd = subEntrys.next();
					wnd.dispose();
					subEntrys.remove();
				}
				/* Remove the row because the context related to that is destroyed. */
				entries.remove();
			}
			break;
		}
	}

	/**
	 * Returns a Context, if no Context is found null is returned.
	 * 
	 * @param context
	 * @return
	 */
	public static Context findContext(String name) {
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			if (entry.getKey().name.equals(name)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static Context findContext(long context) {
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			if (entry.getKey().context == context) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list. With Shared Context. Using the name, if the context with that
	 * name exists already then it adds the window to that shared context, if not
	 * then creates a new context with that name and sets the context handle to the
	 * window handle.
	 * 
	 * @param wnd
	 * @throws Exception
	 */
	public static void openWindow(GLFWWindow wnd, long monitor, String sharedContextName) throws Exception {

		if (monitor == NULL) {
			logger.debug("Monitor Provided Is NULL, Defaulting To Primary Monitor.");
		}

		Context tempContext;
		if ((tempContext = findContext(sharedContextName)) != null) {
			CopyOnWriteArrayList<GLFWWindow> wnds = sharedContexts.get(tempContext);
			wnd.setRenderEngine(tempContext.renderEngine);
			wnds.add(wnd);
			wnd.create(monitor, tempContext.context, tempContext.renderEngine);
			return;
		}
	}

	public static void openWindow(GLFWWindow wnd, long monitor, long sharedContext) throws Exception {

		if (monitor == NULL) {
			logger.debug("Monitor Provided Is NULL, Defaulting To Primary Monitor.");
		}

		Context tempContext;
		if ((tempContext = findContext(sharedContext)) != null) {
			CopyOnWriteArrayList<GLFWWindow> wnds = sharedContexts.get(tempContext);
			wnd.setRenderEngine(tempContext.renderEngine);
			wnds.add(wnd);
			wnd.create(monitor, tempContext.context, tempContext.renderEngine);
			return;
		}
	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list. Without Shared Context. And creates a entry in the shared
	 * context bucket list using the name of the window as context name and handle
	 * as context handle, and if in the future you wish to add a window to this
	 * context use the window's name as the sharedContextName.
	 */
	public static void openWindow(GLFWWindow wnd, long monitor, RenderingEngine rndEng) throws Exception {

		if (monitor == NULL) {
			logger.error("Monitor Provided Is NULL, Defaulting To Primary Monitor.");
		}

		if (findContext(wnd.getWindowName()) != null) {
			throw new Exception("Window Already Exits With The Same Name.");
		}

		wnd.create(monitor, rndEng);
		CopyOnWriteArrayList<GLFWWindow> wnds = new CopyOnWriteArrayList<GLFWWindow>();
		wnds.add(wnd);
		sharedContexts.put(new Context(wnd.getWindowName(), wnd.getGlfw_Handle(), rndEng), wnds);
		rndEng.initGraphics();
	}

	public static void printWindows() {

		StringBuilder opnWnds = new StringBuilder();

		logger.debug("Open Windows: ");
		for (Iterator<Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<Context, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			opnWnds.append("\n Context Name: " + entry.getKey().name + ": " + "\n");

			for (GLFWWindow wnd : entry.getValue()) {
				opnWnds.append("\t - " + wnd.getWindowName() + " " + wnd.getGlfw_Handle() + "\n");
			}
		}
		logger.debug(opnWnds.toString());
	}

	public static boolean isStopRequested() {
		return engineStopFlag;
	}

	public static void raiseStopFlag() {
		engineStopFlag = true;
	}

	public static CoreEngine getCoreEngine() {
		return coreEngine;
	}

}
