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
	private static final Logger logger = LogManager.getLogger(GLFWWindowManager.class.getName());
	// private static Map<String, GLFWWindow> activeWindows = new HashMap<String,
	// GLFWWindow>();
	/*
	 * Bucket list of contexts and windows which share context. The name of the
	 * context is the name of the window that shares its context
	 */
	private static Map<Context, ArrayList<GLFWWindow>> sharedContexts = new HashMap<Context, ArrayList<GLFWWindow>>();
	private static CoreEngine coreEngine;
	private static boolean engineStopFlag = false;

	public static class Context {
		public long context = NULL;
		public String name;

		public Context(String name, long context) {
			this.context = context;
			this.name = name;
		}

	}

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

		for (Iterator<Map.Entry<Context, ArrayList<GLFWWindow>>> entries = sharedContexts.entrySet().iterator(); entries
				.hasNext();) {

			Map.Entry<Context, ArrayList<GLFWWindow>> entry = entries.next();

			setContext(entry.getKey());

			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				GL.setCapabilities(wnd.getCapabilities());
				wnd.render();
			}

		}
		GL.setCapabilities(null);
	}

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
	public static void openWindow(GLFWWindow wnd) {
		openWindow(wnd, NULL);
	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list.
	 * 
	 * @param wnd
	 * @param monitor The monitor to open the window on.
	 */
	public static void openWindow(GLFWWindow wnd, long monitor) {
		openWindow(wnd, monitor, NULL);
	}

	/**
	 * Opens the window using the create() method in GLFWWindow with a shared
	 * context and adds it under the specific context.
	 * 
	 * @param wnd
	 * @param sharedContext The context the window should inherit.
	 */
	public static void openWindowSC(GLFWWindow wnd, long sharedContext) {
		openWindow(wnd, NULL, sharedContext);
	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list.
	 * 
	 * @param wnd
	 */
	public static void openWindow(GLFWWindow wnd, long monitor, long sharedContext) {

		try {
			wnd.create(sharedContext, monitor);
		} catch (Exception e) {
			logger.error("Unable To Create Window. ", e);
			return;
		}
		if (sharedContext == NULL) {
			if (findContext(wnd.getGlfw_Handle()) == null) {
				ArrayList<GLFWWindow> wnds = new ArrayList<GLFWWindow>();
				wnds.add(wnd);
				sharedContexts.put(new Context(wnd.getWindowName(), wnd.getGlfw_Handle()), wnds);
				return;
			}
			logger.error("Context Already Exists! Window Is Probably Already Open.");
		} else {
			Context tempContext;
			if ((tempContext = findContext(sharedContext)) != null) {
				ArrayList<GLFWWindow> wnds = sharedContexts.get(tempContext);
				wnds.add(wnd);
			}
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
