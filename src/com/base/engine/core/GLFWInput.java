package com.base.engine.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import com.base.engine.math.Vector2f;

public class GLFWInput {

	private GLFWKeyCallback keyClbk;
	private GLFWMouseButtonCallback mouseCalbk;
	private GLFWCursorPosCallback mousePosCalbk;
	private GLFWScrollCallback scrlClbk;

	private ArrayList<Integer> keysDown = new ArrayList<Integer>();
	private ArrayList<Integer> keysUp = new ArrayList<Integer>();
	private ArrayList<Integer> mouseBtnsDown = new ArrayList<Integer>();
	private ArrayList<Integer> mouseBtnsUp = new ArrayList<Integer>();

	private Vector2f mousePos;
	private float scrlOffset;
	private long glfw_Handle;

	public GLFWInput() {
		mousePos = new Vector2f(0, 0);
	}

	// TODO: Implement Scan Code Instead Of Key Code
	public void initInput(long glfw_Handle) {
		this.glfw_Handle = glfw_Handle;
		glfwSetKeyCallback(glfw_Handle, keyClbk = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW_PRESS) {
					if (!keysDown.contains(Integer.valueOf(key))) {
						keysDown.add(key);
						keysUp.remove(Integer.valueOf(key));
						return;
					}
				}

				if (action == GLFW_RELEASE) {
					if (!keysUp.contains(Integer.valueOf(key))) {
						keysUp.add(key);
						keysDown.remove(Integer.valueOf(key));
						return;
					}
				}
			}
		});

		glfwSetMouseButtonCallback(glfw_Handle, mouseCalbk = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {

				if (action == GLFW_PRESS) {
					if (!mouseBtnsDown.contains(Integer.valueOf(button))) {
						mouseBtnsDown.add(button);
						mouseBtnsUp.remove(Integer.valueOf(button));
					}
				}

				if (action == GLFW_RELEASE) {
					if (!mouseBtnsUp.contains(Integer.valueOf(button))) {
						mouseBtnsUp.add(button);
						mouseBtnsDown.remove(Integer.valueOf(button));
					}
				}
			}
		});

		glfwSetCursorPosCallback(glfw_Handle, mousePosCalbk = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mousePos.setX((float) xpos);
				mousePos.setY((float) ypos);
			}
		});

		glfwSetScrollCallback(glfw_Handle, scrlClbk = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				setScrollOffset((float) yoffset);
			}
		});
	}

	public void update() {
		keysDown.clear();
		keysUp.clear();
		mouseBtnsDown.clear();
		mouseBtnsUp.clear();
		glfwPollEvents();
	}

	public void destroy() {
		keyClbk.free();
		mouseCalbk.free();
		scrlClbk.free();
		mousePosCalbk.free();
	}

	public boolean isKeyDown(int key) {
		return keysDown.contains(Integer.valueOf(key));
	}

	public boolean isKeyUp(int key) {
		return keysUp.contains(Integer.valueOf(key));
	}

	public boolean isMouseButtonDown(int btn) {
		return mouseBtnsDown.contains(btn);
	}

	public boolean isMouseButtonUp(int btn) {
		return mouseBtnsUp.contains(btn);
	}

	// TODO: Add Support For Decting Held Keys Not Really Needed But Useful in Some
	// Situations.
//	public boolean isMouseButtonHeld(int btn) {
//
//	}
//	public boolean isKeyHeld(int key) {
//
//	}

	public float getScrollOffset() {
		return scrlOffset;
	}

	public void setScrollOffset(float offset) {
		scrlOffset = offset;
	}

	public void setMousePosition(Vector2f newPos) {
		mousePos = newPos;
		glfwSetCursorPos(glfw_Handle, newPos.getX(), newPos.getY());
	}

	public Vector2f getMousePosition() {
		return mousePos;
	}
}
