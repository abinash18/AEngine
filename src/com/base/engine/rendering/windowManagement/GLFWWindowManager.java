package com.base.engine.rendering.windowManagement;

import java.util.HashMap;

import com.base.engine.core.CoreEngine;
import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;

/**
 * GLFW Implementation Of Window.
 * 
 * @author abinash
 */
public class GLFWWindowManager {

	private static HashMap<String, GLFWWindow> models = new HashMap<String, GLFWWindow>();

	private static GLFWWindow currentWindow;

	private static CoreEngine coreEngine;

	private static boolean engineStopFlag = false;

	private static Logger logger = LogManager.getLogger(GLFWWindowManager.class.getName());

//	@Deprecated
//	public static GLFWWindow getGLFWWindowHandle(int width, int height, String name, String title, boolean fullscreen,
//			boolean vSync) {
//		GLFWWindow tempWindow = new GLFWWindow(width, height, name, title, fullscreen, vSync);
//		models.put(name, tempWindow);
//		return tempWindow;
//	}

	public static GLFWWindow getGLFWWindowHandle(String name) {
		return models.get(name);
	}

	public static GLFWWindow getGLFWWindow(String name) {
		return models.get(name);
	}

	public static GLFWWindow getCurrentWindow() {
		if (currentWindow != null)
			return currentWindow;
		throw new IllegalStateException("No Current Window Found Engine Cannot Be Run Without Main Window.");
	}

	public static void init(CoreEngine coreEngine) {
		GLFWWindowManager.coreEngine = coreEngine;
		currentWindow.setCoreEngine(coreEngine);
		models.forEach((k, v) -> {
			v.setCoreEngine(coreEngine);
		});
	}

	public static void render() {
		currentWindow.render();
		GLFWWindowManager.swapBuffers();
	}

	/**
	 * This Method Sets The Current Window That Is Being Rendered Too, To The One's
	 * Name Provided. Since This Method Can Create Errors And The Engine Currently
	 * Dose Not Support Concurrent Window Updates The Previous Window (If There
	 * Exists One) Will Be Disposed Of. Be Aware You Will Still Need To Call Create
	 * On The New Window.
	 * 
	 * @param name
	 */
	public static GLFWWindow setCurrentWindow(String name) {
//		if (currentWindow != null) {
//			// currentWindow.dispose();
//		}
		currentWindow = models.get(name);
		// GL.setCapabilities(currentWindow.getCapabilities()); TODO
		return currentWindow;
	}

	public static void destroyAll() {
		models.forEach((k, v) -> v.dispose());
	}

	public static void addWindow(GLFWWindow model) {
		models.put(model.getName(), model);
		model.setCoreEngine(coreEngine);
		if (currentWindow == null) {
			currentWindow = model;
		}
		logger.debug("New Window: " + model.getName());
	}

	public static boolean isStopRequested() {
		if (models.size() == 1) {
			return currentWindow.isCloseRequested();
		}
		return engineStopFlag;
	}

	public static void setStopRequested() {
		engineStopFlag = true;
	}

	public static void input(float delta) {
		getCurrentWindow().input(delta);
	}

	public static void update(float delta) {
		getCurrentWindow().update(delta);
	}

	private static void swapBuffers() {
		getCurrentWindow().swapBuffers();
	}

	public static CoreEngine getCoreEngine() {
		return coreEngine;
	}

	public static void setCoreEngine(CoreEngine coreEngine) {
		GLFWWindowManager.coreEngine = coreEngine;
	}

}
