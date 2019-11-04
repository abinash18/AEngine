package net.abi.abisEngine.rendering.windowManagement;

import static net.abi.abisEngine.rendering.windowManagement.GLFWWindow.NULL;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL;

import net.abi.abisEngine.core.CoreEngine;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.RenderingEngine;

/**
 * GLFW Implementation Of Window. This Implementation supports shared contexts,
 * but only to a degree. The limitations are that if the main window (The one
 * sharing its context to its children, by children it means windows which
 * inherit context.) closes it will cause the chain of windows to close as well.
 * Another limitation is that you cannot have a tree of shared contexts. Windows
 * cannot share their context if they already have a inherited context.
 * 
 * @author abinash
 */
public class GLFWWindowManager {
	// TODO: Make a better exception for throwing when there is a faliure to create
	// windows.
	private static final Logger logger = LogManager.getLogger(GLFWWindowManager.class.getName());
	// private static Map<String, GLFWWindow> activeWindows = new HashMap<String,
	// GLFWWindow>();
	/*
	 * Bucket list of contexts and windows which share context. The name of the
	 * context is the name of the window that shares its context
	 */
	private static Map<Context, ArrayList<GLFWWindow>> sharedContexts = new HashMap<Context, ArrayList<GLFWWindow>>();
	/*
	 * Core engine really has no use in this class for now since we have a seperate
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
		public long context = NULL;
		public String name;
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
	 * @param context
	 */
	private static void setContext(Context context) {
		glfwMakeContextCurrent(context.context);
	}

	/*
	 * private static void setContext(long context) {
	 * glfwMakeContextCurrent(context); }
	 */

	/**
	 * Renders all windows stored in the bucket list, and the windows which inherit
	 * their context.
	 */
	public static void render() {

		/*
		 * Since useing a for loop iterating over the hash map will cause a concurrent
		 * modification exception, we use the iterator which iterates over the entris in
		 * the map.
		 */
		for (Iterator<Map.Entry<Context, ArrayList<GLFWWindow>>> entries = sharedContexts.entrySet().iterator(); entries
				.hasNext();) {
			/*
			 * Current Entry, we cannot call next() on the iterator every time we need the
			 * entry because then it will skip to the next entry in line.
			 */
			Map.Entry<Context, ArrayList<GLFWWindow>> entry = entries.next();

			/*
			 * Sets the current context to the one provided in the entry so we can render
			 * that context.
			 */
			setContext(entry.getKey());

			/* Iterates over the windows which share the context. */
			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				/*
				 * This will set the capabilities to render the current context on the window.
				 */
				GL.setCapabilities(wnd.getCapabilities());
				/* We are ready to render the context now. */
				wnd.render();
			}

		}
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
		for (Iterator<Map.Entry<Context, ArrayList<GLFWWindow>>> entries = sharedContexts.entrySet().iterator(); entries
				.hasNext();) {

			Map.Entry<Context, ArrayList<GLFWWindow>> entry = entries.next();

			setContext(entry.getKey());

			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				GL.setCapabilities(wnd.getCapabilities());
				wnd.input(delta);
			}

		}
		GL.setCapabilities(null);
	}

	public static void update(float delta) {
		for (Iterator<Map.Entry<Context, ArrayList<GLFWWindow>>> entries = sharedContexts.entrySet().iterator(); entries
				.hasNext();) {

			Map.Entry<Context, ArrayList<GLFWWindow>> entry = entries.next();

			setContext(entry.getKey());

			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				if (checkClose(wnd)) {
					wnd.dispose();
					subEntrys.remove();
					// entries.remove();
					break;
				}
				GL.setCapabilities(wnd.getCapabilities());
				wnd.update(delta);
			}

		}
		GL.setCapabilities(null);

	}

	/*
	 * TODO: Return an array of windows instead of one so the down stream function
	 * can sort later.
	 */
	/**
	 * Gets the window specified.
	 * 
	 * @param name
	 * @return
	 */
	public static GLFWWindow getGLFWWindow(String name) {
		for (Iterator<Map.Entry<Context, ArrayList<GLFWWindow>>> entries = sharedContexts.entrySet().iterator(); entries
				.hasNext();) {
			Map.Entry<Context, ArrayList<GLFWWindow>> entry = entries.next();
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

	private static void setCoreEngine(CoreEngine creng) {
		GLFWWindowManager.coreEngine = creng;
	}

	public static void init(CoreEngine coreEngine) {
		setCoreEngine(coreEngine);
	}

	/**
	 * Destroys all active contexts. This is executed before the engine terminates.
	 */
	public static void destroyAll() {
		try {
			for (Iterator<Map.Entry<Context, ArrayList<GLFWWindow>>> entries = sharedContexts.entrySet()
					.iterator(); entries.hasNext();) {
				Map.Entry<Context, ArrayList<GLFWWindow>> entry = entries.next();
				for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
					GLFWWindow wnd = subEntrys.next();
					wnd.dispose();
					subEntrys.remove();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Error While Destroying Windows. ", e);
		}

	}

	/*
	 * TODO: Make a function which returns the entire entry in the map instead of
	 * just the context.
	 */
	/**
	 * Returns a Context, if no Context is found null is returned.
	 * 
	 * @param context
	 * @return
	 */
	public static Context findContext(long context) {
		for (Iterator<Map.Entry<Context, ArrayList<GLFWWindow>>> entries = sharedContexts.entrySet().iterator(); entries
				.hasNext();) {
			Map.Entry<Context, ArrayList<GLFWWindow>> entry = entries.next();

			if (entry.getKey().context == context) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list.
	 * 
	 * @param wnd
	 */
	/*
	 * public static void openWindow(GLFWWindow wnd, long monitor, RenderingEngine
	 * rndEng) { openWindow(wnd, monitor, rndEng); }
	 */

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list.
	 * 
	 * @param wnd
	 * @param monitor The monitor to open the window on.
	 */
	/*
	 * public static void openWindow(GLFWWindow wnd, long monitor, RenderingEngine
	 * rndEng) { openWindow(wnd, monitor, NULL, rndEng); }
	 */

	/**
	 * Opens the window using the create() method in GLFWWindow with a shared
	 * context and adds it under the specific context.
	 * 
	 * @param wnd
	 * @param sharedContext The context the window should inherit.
	 * @throws Exception
	 */
	/*
	 * public static void openWindowSC(GLFWWindow wnd, long monitor, long
	 * sharedContext) throws Exception { openWindow(wnd, monitor, sharedContext); }
	 */

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list. With Shared Context.
	 * 
	 * @param wnd
	 * @throws Exception
	 */
	public static void openWindow(GLFWWindow wnd, long monitor, long sharedContext) throws Exception {

		if (sharedContext == NULL) {
			logger.error("Context Provided Is NULL.");
			throw new Exception("Context Provided Is NULL.");
		}

		if (monitor == NULL) {
			logger.error("Monitor Provided Is NULL, Defaulting To Primary Monitor.");
		}

		Context tempContext;
		if ((tempContext = findContext(sharedContext)) != null) {
			ArrayList<GLFWWindow> wnds = sharedContexts.get(tempContext);
			wnd.setRenderEngine(tempContext.renderEngine);
			wnds.add(wnd);
			try {
				wnd.create(monitor, sharedContext, tempContext.renderEngine);
			} catch (Exception e) {
				logger.error("Unable To Create Window. ", e);
				return;
			}
			// tempContext.renderEngine.initGraphics();
			return;
		}
		logger.error("Could'nt Find The Shared Context Supplied.");
		throw new Exception("Could'nt Find The Shared Context Supplied.");

	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list. Without Shared Context.
	 * 
	 * @param wnd
	 */
	public static void openWindow(GLFWWindow wnd, long monitor, RenderingEngine rndEng) throws Exception {

		if (monitor == NULL) {
			logger.error("Monitor Provided Is NULL, Defaulting To Primary Monitor.");
		}

		wnd.create(monitor, NULL, rndEng);
		if (findContext(wnd.getGlfw_Handle()) == null) {
			ArrayList<GLFWWindow> wnds = new ArrayList<GLFWWindow>();
			wnds.add(wnd);
			sharedContexts.put(new Context(wnd.getWindowName(), wnd.getGlfw_Handle(), rndEng), wnds);
			rndEng.initGraphics();
			return;
		} else {
			logger.error("Window Already Exists.");
			throw new Exception("Window Already Exists.");
		}
	}

	// a
//	public static void printWindows() {
//
//		logger.debug("Open Windows: ");
//		for (entries = openWindows.entrySet().iterator(); entries.hasNext();) {
//			Map.Entry<String, GLFWWindow> entry = entries.next();
//			logger.debug("| Window Name: " + entry.getValue().getName() + " Title: " + entry.getValue().getTitle()
//					+ " UUID: " + entry.getValue().getID() + " GLFW Handle: " + entry.getValue().getGlfw_Handle()
//					+ " |");
//		}
//		logger.debug("Window Templates: ");
//		for (Iterator<Map.Entry<String, GLFWWindow>> entries2 = models.entrySet().iterator(); entries2.hasNext();) {
//			Map.Entry<String, GLFWWindow> entry = entries2.next();
//			logger.debug("| Window Name: " + entry.getValue().getName() + " Title: " + entry.getValue().getTitle()
//					+ " UUID: " + entry.getValue().getID() + " GLFW Handle: " + entry.getValue().getGlfw_Handle()
//					+ " |");
//		}
//
//	}

//	public static void openWindow(GLFWWindow wnd, long monitor, long sharedContext) {
//
//		try {
//			wnd.create(monitor, sharedContext);
//			if (sharedContext == NULL) {
//				if (findContext(wnd.getGlfw_Handle()) == null) {
//					ArrayList<GLFWWindow> wnds = new ArrayList<GLFWWindow>();
//					wnds.add(wnd);
//					sharedContexts.put(new Context(wnd.getWindowName(), wnd.getGlfw_Handle(), rndEng), wnds);
//					return;
//				}
//				logger.error("Context Already Exists! Window Is Probably Already Open.");
//				throw new Exception("Context Alreadt Exists!");
//			} else {
//				Context tempContext;
//				if ((tempContext = findContext(sharedContext)) != null) {
//					ArrayList<GLFWWindow> wnds = sharedContexts.get(tempContext);
//					wnd.setRenderEngine(tempContext.renderEngine);
//					wnds.add(wnd);
//					return;
//				}
//				logger.error("Could'nt Find The Shared Context Supplied.");
//				throw new Exception("Could'nt Find The Shared Context Supplied.");
//			}
//		} catch (Exception e) {
//			logger.error("Unable To Create Window. ", e);
//			return;
//		}
//
//	}

	public static boolean isStopRequested() {
//		if (models.size() == 1) {
//			return currentWindow.isCloseRequested();
//		}
		return engineStopFlag;
	}

	public static void raiseStopFlag() {
		engineStopFlag = true;
	}

	public static CoreEngine getCoreEngine() {
		return coreEngine;
	}

}
