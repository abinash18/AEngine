package net.abi.abisEngine.input;

import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.abi.abisEngine.math.Vector2f;

public class GLFWMouseAndKeyboardInput extends GLFWInput {

	private GLFWKeyCallback keyClbk;
	private GLFWMouseButtonCallback mouseCalbk;
	private GLFWCursorPosCallback mousePosCalbk;
	private GLFWScrollCallback scrlClbk;
	private GLFWCursorEnterCallback crsNtrClbk;

	/**
	 * We are using sets here because they cannot contain duplicates so it is more
	 * efficient than using lists.
	 */
	private Set<Integer> keysDown = new HashSet<Integer>();
	private Set<Integer> keysHeldDown = new HashSet<Integer>();
	private Set<Integer> keysUp = new HashSet<Integer>();
	private Set<Integer> mouseBtnsDown = new HashSet<Integer>();
	private Set<Integer> mouseBtnsHeldDown = new HashSet<Integer>();
	private Set<Integer> mouseBtnsUp = new HashSet<Integer>();

	private boolean mouseGrabbedAndHidden = false, mouseHidden = false, cursorInWindow;

	private Vector2f mousePos;
	private float scrlOffset;

	public GLFWMouseAndKeyboardInput() {
		this.mousePos = new Vector2f(0, 0);
	}

	// TODO: Implement Scan Code Instead Of Key Code
	public void initInput(long hndl) {
		super.glfw_Handle = hndl;
		glfwSetKeyCallback(glfw_Handle, (keyClbk = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW_PRESS) {
					if (!keysDown.contains(Integer.valueOf(key))) {
						keysDown.add(key);
						keysHeldDown.add(key);
						// keysUp.remove(key);
						return;
					}
				}

				if (action == GLFW_RELEASE) {
					if (!keysUp.contains(Integer.valueOf(key))) {
						keysUp.add(key);
						keysHeldDown.remove(Integer.valueOf(key));
						// keysDown.remove(Integer.valueOf(key));
						return;
					}
				}
			}
		}));

		glfwSetMouseButtonCallback(glfw_Handle, (mouseCalbk = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {

				if (action == GLFW_PRESS) {
					if (!mouseBtnsDown.contains(Integer.valueOf(button))) {
						mouseBtnsDown.add(button);
						mouseBtnsHeldDown.add(button);
						// mouseBtnsUp.remove(Integer.valueOf(button)); // Check This After For Null
						// Pointer Exception.
						return;
					}
				}

				if (action == GLFW_RELEASE) {
					if (!mouseBtnsUp.contains(Integer.valueOf(button))) {
						mouseBtnsUp.add(button);
						mouseBtnsHeldDown.remove(Integer.valueOf(button));
						// mouseBtnsDown.remove(Integer.valueOf(button)); // Check This After For Null
						// Pointer Exception.
						return;
					}
				}
			}
		}));

		glfwSetCursorPosCallback(glfw_Handle, (mousePosCalbk = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mousePos.setX((float) xpos);
				mousePos.setY((float) ypos);
				mousePos.mul(-1f);
			}
		}));

		glfwSetScrollCallback(glfw_Handle, (scrlClbk = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				setScrollOffset((float) yoffset);
			}
		}));

		glfwSetCursorEnterCallback(glfw_Handle, (crsNtrClbk = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, boolean entered) {
				cursorInWindow = entered;
			}
		}));
	}

	@Override
	public void update() {
		keysDown.clear();
		keysUp.clear();
		mouseBtnsDown.clear();
		mouseBtnsUp.clear();
		super.update();
	}

	/**
	 * Destroys The Call Backs Created.
	 * 
	 * Note: -Will Cause Crash If Called Right Before Calling glfwFreeCallbacks(Use
	 * destroySafe();).
	 */
	@Deprecated
	public void destroy() {
		keyClbk.free();
		mouseCalbk.free();
		scrlClbk.free();
		mousePosCalbk.free();
	}

	/**
	 * Safely destroys the callbacks created. This function is safe to use before
	 * calling glfwDestroyCallbacks.
	 */
	public void destroySafe() {
		glfwSetKeyCallback(glfw_Handle, null).free();
		glfwSetMouseButtonCallback(glfw_Handle, null).free();
		glfwSetScrollCallback(glfw_Handle, null).free();
		glfwSetCursorPosCallback(glfw_Handle, null).free();
	}

	public boolean isCursorInWindow() {
		return cursorInWindow;
	}

	public boolean isKeyDown(int key) {
		return keysDown.contains(Integer.valueOf(key));
	}

	public boolean isKeyHeldDown(int key) {
		return keysHeldDown.contains(Integer.valueOf(key));
	}

	public boolean isKeyUp(int key) {
		return keysUp.contains(Integer.valueOf(key));
	}

	public boolean isMouseButtonDown(int btn) {
		return mouseBtnsDown.contains(btn);
	}

	public boolean isMouseButtonHeldDown(int btn) {
		return mouseBtnsHeldDown.contains(btn);
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

	public void setCursorPos(Vector2f newPos) {
		mousePos = newPos;
		glfwSetCursorPos(glfw_Handle, newPos.x(), newPos.y());
	}

	public void setCursorPos(float x, float y) {
		mousePos.setX(x);
		mousePos.setY(y);
		glfwSetCursorPos(glfw_Handle, x, y);
	}

	public Vector2f getCursorPos() {
		return mousePos;
	}

	public boolean isMouseHiddenAndGrabbed() {
		return mouseGrabbedAndHidden;
	}

	public GLFWCursorEnterCallback getCrsNtrClbk() {
		return crsNtrClbk;
	}

	public boolean isMouseHidden() {
		return mouseHidden;
	}

	// TODO: try to use an int variable to store state then use an if statement is
	// return or in the down path function.
	public void setCursorMode(int mode) {

		if (mode == GLFW_CURSOR_DISABLED) {
			mouseHidden = true; // It is hidden when it is set to grabbed or disabled
			mouseGrabbedAndHidden = true;
		} else if (mode == GLFW_CURSOR_HIDDEN) {
			mouseHidden = true;
			mouseGrabbedAndHidden = false;
		} else if (mode == GLFW_CURSOR_NORMAL) {
			mouseHidden = false;
			mouseGrabbedAndHidden = false;
		}

		glfwSetInputMode(glfw_Handle, GLFW_CURSOR, mode);
	}

}
