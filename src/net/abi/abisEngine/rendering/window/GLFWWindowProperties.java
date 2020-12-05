/**
 * 
 */
package net.abi.abisEngine.rendering.window;

import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowContentScaleCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowMaximizeCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GLCapabilities;

import net.abi.abisEngine.math.vector.Vector2i;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;

/**
 * @author abina
 *
 */
public class GLFWWindowProperties {
	public long preferredMonitor = 0, sharedContext = 0;
	/**
	 * sc_ is the dimensions of the window in screen coordinates, This is different
	 * than pixels since the positive of the y axis is inverted meaning it points
	 * down instead of up so the 0, 0 of the window is in the top left of the
	 * corner.
	 */
	public int sc_width, sc_height,
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
			f_top, f_left, f_right, f_bottom,
			/* The refresh rate used by VSync */
			preferredRefreshRate = GLFW_DONT_CARE,
			/**
			 * This option Synchronizes the frames so they render more steadily instead of
			 * dropping and causing lag.
			 */
			vSync = 0, startHidden = GLFWWindow.GLFW_FALSE;

	/** The name is what the engine recognizes and it is used to find the window. */
	public String name,
			/**
			 * The title to show on the decorated frame and the general title where ever it
			 * is showed.
			 */
			title;
	public boolean fullscreen = false,
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
	public float opacity = 1.0f;

	/** Position of the window in screen coordinates (the top left of the window) */
	public Vector2i position;

	public GLCapabilities capabilities;
	public GLFWFramebufferSizeCallback frmBffrClbk;
	public GLFWWindowCloseCallback wndCloseClbk;
	public GLFWWindowContentScaleCallback wndCntSclClbk;
	public GLFWWindowFocusCallback wndFcsClbk;
	public GLFWWindowIconifyCallback wndIconifyClbk;
	public GLFWWindowMaximizeCallback wndMxmzClbk;
	public GLFWWindowPosCallback wndPosClbk;
	public GLFWWindowSizeCallback wndSizeClbk;
	public GLFWWindowRefreshCallback wndRfrshClbk;

	public RenderingEngine renderEngine;

}
