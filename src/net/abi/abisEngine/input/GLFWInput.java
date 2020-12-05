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
package net.abi.abisEngine.input;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
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

import net.abi.abisEngine.math.vector.Vector2f;

// TODO: Add MultiCursor Support.
public abstract class GLFWInput {

	/** The key or button was released. */
	public static final int GLFW_RELEASE = 0;

	/** The key or button was pressed. */
	public static final int GLFW_PRESS = 1;

	/** The key was held down until it repeated. */
	public static final int GLFW_REPEAT = 2;

	/** Printable keys. */
	public static final int GLFW_KEY_SPACE = 32, GLFW_KEY_APOSTROPHE = 39, GLFW_KEY_COMMA = 44, GLFW_KEY_MINUS = 45,
			GLFW_KEY_PERIOD = 46, GLFW_KEY_SLASH = 47, GLFW_KEY_0 = 48, GLFW_KEY_1 = 49, GLFW_KEY_2 = 50,
			GLFW_KEY_3 = 51, GLFW_KEY_4 = 52, GLFW_KEY_5 = 53, GLFW_KEY_6 = 54, GLFW_KEY_7 = 55, GLFW_KEY_8 = 56,
			GLFW_KEY_9 = 57, GLFW_KEY_SEMICOLON = 59, GLFW_KEY_EQUAL = 61, GLFW_KEY_A = 65, GLFW_KEY_B = 66,
			GLFW_KEY_C = 67, GLFW_KEY_D = 68, GLFW_KEY_E = 69, GLFW_KEY_F = 70, GLFW_KEY_G = 71, GLFW_KEY_H = 72,
			GLFW_KEY_I = 73, GLFW_KEY_J = 74, GLFW_KEY_K = 75, GLFW_KEY_L = 76, GLFW_KEY_M = 77, GLFW_KEY_N = 78,
			GLFW_KEY_O = 79, GLFW_KEY_P = 80, GLFW_KEY_Q = 81, GLFW_KEY_R = 82, GLFW_KEY_S = 83, GLFW_KEY_T = 84,
			GLFW_KEY_U = 85, GLFW_KEY_V = 86, GLFW_KEY_W = 87, GLFW_KEY_X = 88, GLFW_KEY_Y = 89, GLFW_KEY_Z = 90,
			GLFW_KEY_LEFT_BRACKET = 91, GLFW_KEY_BACKSLASH = 92, GLFW_KEY_RIGHT_BRACKET = 93,
			GLFW_KEY_GRAVE_ACCENT = 96, GLFW_KEY_WORLD_1 = 161, GLFW_KEY_WORLD_2 = 162;

	/** Function keys. */
	public static final int GLFW_KEY_ESCAPE = 256, GLFW_KEY_ENTER = 257, GLFW_KEY_TAB = 258, GLFW_KEY_BACKSPACE = 259,
			GLFW_KEY_INSERT = 260, GLFW_KEY_DELETE = 261, GLFW_KEY_RIGHT = 262, GLFW_KEY_LEFT = 263,
			GLFW_KEY_DOWN = 264, GLFW_KEY_UP = 265, GLFW_KEY_PAGE_UP = 266, GLFW_KEY_PAGE_DOWN = 267,
			GLFW_KEY_HOME = 268, GLFW_KEY_END = 269, GLFW_KEY_CAPS_LOCK = 280, GLFW_KEY_SCROLL_LOCK = 281,
			GLFW_KEY_NUM_LOCK = 282, GLFW_KEY_PRINT_SCREEN = 283, GLFW_KEY_PAUSE = 284, GLFW_KEY_F1 = 290,
			GLFW_KEY_F2 = 291, GLFW_KEY_F3 = 292, GLFW_KEY_F4 = 293, GLFW_KEY_F5 = 294, GLFW_KEY_F6 = 295,
			GLFW_KEY_F7 = 296, GLFW_KEY_F8 = 297, GLFW_KEY_F9 = 298, GLFW_KEY_F10 = 299, GLFW_KEY_F11 = 300,
			GLFW_KEY_F12 = 301, GLFW_KEY_F13 = 302, GLFW_KEY_F14 = 303, GLFW_KEY_F15 = 304, GLFW_KEY_F16 = 305,
			GLFW_KEY_F17 = 306, GLFW_KEY_F18 = 307, GLFW_KEY_F19 = 308, GLFW_KEY_F20 = 309, GLFW_KEY_F21 = 310,
			GLFW_KEY_F22 = 311, GLFW_KEY_F23 = 312, GLFW_KEY_F24 = 313, GLFW_KEY_F25 = 314, GLFW_KEY_KP_0 = 320,
			GLFW_KEY_KP_1 = 321, GLFW_KEY_KP_2 = 322, GLFW_KEY_KP_3 = 323, GLFW_KEY_KP_4 = 324, GLFW_KEY_KP_5 = 325,
			GLFW_KEY_KP_6 = 326, GLFW_KEY_KP_7 = 327, GLFW_KEY_KP_8 = 328, GLFW_KEY_KP_9 = 329,
			GLFW_KEY_KP_DECIMAL = 330, GLFW_KEY_KP_DIVIDE = 331, GLFW_KEY_KP_MULTIPLY = 332, GLFW_KEY_KP_SUBTRACT = 333,
			GLFW_KEY_KP_ADD = 334, GLFW_KEY_KP_ENTER = 335, GLFW_KEY_KP_EQUAL = 336, GLFW_KEY_LEFT_SHIFT = 340,
			GLFW_KEY_LEFT_CONTROL = 341, GLFW_KEY_LEFT_ALT = 342, GLFW_KEY_LEFT_SUPER = 343, GLFW_KEY_RIGHT_SHIFT = 344,
			GLFW_KEY_RIGHT_CONTROL = 345, GLFW_KEY_RIGHT_ALT = 346, GLFW_KEY_RIGHT_SUPER = 347, GLFW_KEY_MENU = 348,
			GLFW_KEY_LAST = GLFW_KEY_MENU;

	/** If this bit is set one or more Shift keys were held down. */
	public static final int GLFW_MOD_SHIFT = 0x1;

	/** If this bit is set one or more Control keys were held down. */
	public static final int GLFW_MOD_CONTROL = 0x2;

	/** If this bit is set one or more Alt keys were held down. */
	public static final int GLFW_MOD_ALT = 0x4;

	/** If this bit is set one or more Super keys were held down. */
	public static final int GLFW_MOD_SUPER = 0x8;

	/**
	 * If this bit is set the Caps Lock key is enabled and the
	 * {@link #GLFW_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set.
	 */
	public static final int GLFW_MOD_CAPS_LOCK = 0x10;

	/**
	 * If this bit is set the Num Lock key is enabled and the
	 * {@link #GLFW_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set.
	 */
	public static final int GLFW_MOD_NUM_LOCK = 0x20;

	/**
	 * Mouse buttons. See <a target="_blank" href=
	 * "http://www.glfw.org/docs/latest/input.html#input_mouse_button">mouse button
	 * input</a> for how these are used.
	 */
	public static final int GLFW_MOUSE_BUTTON_1 = 0, GLFW_MOUSE_BUTTON_2 = 1, GLFW_MOUSE_BUTTON_3 = 2,
			GLFW_MOUSE_BUTTON_4 = 3, GLFW_MOUSE_BUTTON_5 = 4, GLFW_MOUSE_BUTTON_6 = 5, GLFW_MOUSE_BUTTON_7 = 6,
			GLFW_MOUSE_BUTTON_8 = 7, GLFW_MOUSE_BUTTON_LAST = GLFW_MOUSE_BUTTON_8,
			GLFW_MOUSE_BUTTON_LEFT = GLFW_MOUSE_BUTTON_1, GLFW_MOUSE_BUTTON_RIGHT = GLFW_MOUSE_BUTTON_2,
			GLFW_MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_3;

	/**
	 * Joysticks. See <a target="_blank" href=
	 * "http://www.glfw.org/docs/latest/input.html#joystick">joystick input</a> for
	 * how these are used.
	 */
	public static final int GLFW_JOYSTICK_1 = 0, GLFW_JOYSTICK_2 = 1, GLFW_JOYSTICK_3 = 2, GLFW_JOYSTICK_4 = 3,
			GLFW_JOYSTICK_5 = 4, GLFW_JOYSTICK_6 = 5, GLFW_JOYSTICK_7 = 6, GLFW_JOYSTICK_8 = 7, GLFW_JOYSTICK_9 = 8,
			GLFW_JOYSTICK_10 = 9, GLFW_JOYSTICK_11 = 10, GLFW_JOYSTICK_12 = 11, GLFW_JOYSTICK_13 = 12,
			GLFW_JOYSTICK_14 = 13, GLFW_JOYSTICK_15 = 14, GLFW_JOYSTICK_16 = 15, GLFW_JOYSTICK_LAST = GLFW_JOYSTICK_16;

	/**
	 * Gamepad buttons. See <a target="_blank" href=
	 * "http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how
	 * these are used.
	 */
	public static final int GLFW_GAMEPAD_BUTTON_A = 0, GLFW_GAMEPAD_BUTTON_B = 1, GLFW_GAMEPAD_BUTTON_X = 2,
			GLFW_GAMEPAD_BUTTON_Y = 3, GLFW_GAMEPAD_BUTTON_LEFT_BUMPER = 4, GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER = 5,
			GLFW_GAMEPAD_BUTTON_BACK = 6, GLFW_GAMEPAD_BUTTON_START = 7, GLFW_GAMEPAD_BUTTON_GUIDE = 8,
			GLFW_GAMEPAD_BUTTON_LEFT_THUMB = 9, GLFW_GAMEPAD_BUTTON_RIGHT_THUMB = 10, GLFW_GAMEPAD_BUTTON_DPAD_UP = 11,
			GLFW_GAMEPAD_BUTTON_DPAD_RIGHT = 12, GLFW_GAMEPAD_BUTTON_DPAD_DOWN = 13, GLFW_GAMEPAD_BUTTON_DPAD_LEFT = 14,
			GLFW_GAMEPAD_BUTTON_LAST = GLFW_GAMEPAD_BUTTON_DPAD_LEFT, GLFW_GAMEPAD_BUTTON_CROSS = GLFW_GAMEPAD_BUTTON_A,
			GLFW_GAMEPAD_BUTTON_CIRCLE = GLFW_GAMEPAD_BUTTON_B, GLFW_GAMEPAD_BUTTON_SQUARE = GLFW_GAMEPAD_BUTTON_X,
			GLFW_GAMEPAD_BUTTON_TRIANGLE = GLFW_GAMEPAD_BUTTON_Y;

	/**
	 * Gamepad axes. See <a target="_blank" href=
	 * "http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how
	 * these are used.
	 */
	public static final int GLFW_GAMEPAD_AXIS_LEFT_X = 0, GLFW_GAMEPAD_AXIS_LEFT_Y = 1, GLFW_GAMEPAD_AXIS_RIGHT_X = 2,
			GLFW_GAMEPAD_AXIS_RIGHT_Y = 3, GLFW_GAMEPAD_AXIS_LEFT_TRIGGER = 4, GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER = 5,
			GLFW_GAMEPAD_AXIS_LAST = GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;

	/** Input options. */
	public static final int GLFW_CURSOR = 0x33001, GLFW_STICKY_KEYS = 0x33002, GLFW_STICKY_MOUSE_BUTTONS = 0x33003,
			GLFW_LOCK_KEY_MODS = 0x33004, GLFW_RAW_MOUSE_MOTION = 0x33005;

	/** Cursor state. */
	public static final int GLFW_CURSOR_NORMAL = 0x34001, GLFW_CURSOR_HIDDEN = 0x34002, GLFW_CURSOR_DISABLED = 0x34003;

	/**
	 * Standard cursor shapes. See <a target="_blank" href=
	 * "http://www.glfw.org/docs/latest/input.html#cursor_standard">standard cursor
	 * creation</a> for how these are used.
	 */
	public static final int GLFW_ARROW_CURSOR = 0x36001, GLFW_IBEAM_CURSOR = 0x36002, GLFW_CROSSHAIR_CURSOR = 0x36003,
			GLFW_HAND_CURSOR = 0x36004, GLFW_HRESIZE_CURSOR = 0x36005, GLFW_VRESIZE_CURSOR = 0x36006;

	/** Boolean values. */
	public static final int GLFW_TRUE = 1, GLFW_FALSE = 0;

	protected long glfw_Handle;
	
	// TODO: Implement Scan Code Instead Of Key Code
	public abstract void initInput(long hndl);

	public void update() {
		glfwPollEvents();
	}

	/**
	 * Safely destroys the callbacks created. This function is safe to use before
	 * calling glfwDestroyCallbacks.
	 */
	public abstract void destroySafe();

}
