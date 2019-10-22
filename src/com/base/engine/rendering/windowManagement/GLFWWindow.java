package com.base.engine.rendering.windowManagement;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import com.base.engine.core.GLFWInput;
import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;

/**
 * Window Model For The GLFW Context Handle.
 * 
 * @author abinash
 *
 */
public class GLFWWindow {

	private static Logger logger = LogManager.getLogger(GLFWWindow.class.getName());

	private long glfw_Handle;
	private int width, height;
	private String name, title;
	private boolean fullscreen, vSync;
	private GLFWInput input;
	// private GLFWVidMode vidMode;

	public GLFWWindow(int width, int height, String name, String title, boolean fullscreen, boolean vSync) {
		this.width = width;
		this.height = height;
		this.name = name;
		this.title = title;
		this.fullscreen = fullscreen;
		this.vSync = vSync;
		this.input = new GLFWInput();
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

	public long create() {
		glfw_Handle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (glfw_Handle == NULL) {
			logger.error("Failed to create the GLFW window: name: '" + name + "' title: '" + title + "'");
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(glfw_Handle, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(glfw_Handle, (vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		if (vSync) {
			glfwSwapInterval(1); // Enables V Sync.
		}

		// Make the OpenGL context current
		glfwMakeContextCurrent(glfw_Handle);
		showWindow();

		return glfw_Handle;
	}

	public void update() {
		input.update();
	}

	public void showWindow() {
		glfwShowWindow(glfw_Handle);
	}

//	public void setIcon(String path) {
//
//		ByteBuffer bufferedImage = ResourceLoader.loadImageToByteBuffer(path);
//
//		GLFWImage image = GLFWImage.malloc();
//
//		image.set(32, 32, bufferedImage);
//
//		GLFWImage.Buffer images = GLFWImage.malloc(1);
//		images.put(0, image);
//
//		glfwSetWindowIcon(getId(), images);
//	}

	public void dispose() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(glfw_Handle);
		glfwDestroyWindow(glfw_Handle);
	}

	public boolean isCloseRequested() {
		return glfwWindowShouldClose(glfw_Handle);
	};

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

}
