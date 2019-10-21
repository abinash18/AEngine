package com.base.engine.rendering.windowManagement;

import java.util.HashMap;

/**
 * GLFW Implementation Of Window.
 * 
 * @author abinash
 */
public class GLFWWindowManager {

	private static HashMap<String, GLFWWindow> models;

	public static GLFWWindow getGLFWWindowHandle(int width, int height, String name, String title, boolean fullscreen,
			boolean vSync) {

		if (models == null) {
			models = new HashMap<String, GLFWWindow>();
		}

		return models.put(name, new GLFWWindow(width, height, name, title, fullscreen, vSync));
	}

	public static void destroyAll() {
		models.forEach((k, v) -> v.dispose());
	}

}
