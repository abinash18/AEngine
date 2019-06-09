package com.base.engine;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Input {

	/*
	 * 0 is typically the left mouse button, and 1 is the right mouse button.
	 */

	// The Number Of Keys On The Keyboard.
	private static final int NUM_KEYCODES = 256;
	// The Number Of Keys On The Mouse (Gaming Mice Have More Than This).
	private static final int NUM_MOUSEBUTTONS = 5;

	// Keyboard Input Integer Array Lists.
	private static ArrayList<Integer> currentKeys = new ArrayList<Integer>();
	private static ArrayList<Integer> downKeys = new ArrayList<Integer>();
	private static ArrayList<Integer> upKeys = new ArrayList<Integer>();

	// Mouse Input Integer Array Lists.
	private static ArrayList<Integer> currentMouse = new ArrayList<Integer>();
	private static ArrayList<Integer> downMouse = new ArrayList<Integer>();
	private static ArrayList<Integer> upMouse = new ArrayList<Integer>();

	public static void update() {

		///////////////////////////////////////////////////
		// Mouse Input Algorithm Start.
		///////////////////////////////////////////////////

		upMouse.clear();

		for (int i = 0; i < NUM_MOUSEBUTTONS; i++) {
			if (!getMouse(i) && currentMouse.contains(i)) {
				upMouse.add(i);
			}
		}

		downMouse.clear();

		for (int i = 0; i < NUM_MOUSEBUTTONS; i++) {
			if (getMouse(i) && !currentMouse.contains(i)) {
				downMouse.add(i);
			}
		}

		///////////////////////////////////////////////////////////////////////////
		/*
		 * This has to be last because it clears the array of integers representing
		 * mouse buttons that are down because without it the Algorithm cannot tell
		 * which keys are down.
		 */
		currentMouse.clear();

		for (int i = 0; i < NUM_MOUSEBUTTONS; i++) {
			if (getMouse(i)) {
				currentMouse.add(i);
			}
		}
		///////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////
		// Keyboard Input Algorithm Start.
		///////////////////////////////////////////////////

		upKeys.clear();

		for (int i = 0; i < NUM_KEYCODES; i++) {
			if (!getKey(i) && currentKeys.contains(i)) {
				upKeys.add(i);
			}
		}

		downKeys.clear();

		for (int i = 0; i < NUM_KEYCODES; i++) {
			if (getKey(i) && !currentKeys.contains(i)) {
				downKeys.add(i);
			}
		}

		///////////////////////////////////////////////////////////////////////////
		/*
		 * This has to be last because it clears the array of integers representing
		 * mouse buttons that are down because without it the Algorithm cannot tell
		 * which keys are down.
		 */
		currentKeys.clear();

		for (int i = 0; i < NUM_KEYCODES; i++) {
			if (getKey(i)) {
				currentKeys.add(i);
			}
		}
		///////////////////////////////////////////////////////////////////////////

	}

	public static boolean getKey(int keyCode) {
		return (Keyboard.isKeyDown(keyCode));
	}

	public static boolean getKeyDown(int keyCode) {
		return (downKeys.contains(keyCode));
	}

	public static boolean getKeyUp(int keyCode) {
		return (upKeys.contains(keyCode));
	}

	public static boolean getMouse(int mouseButton) {
		return (Mouse.isButtonDown(mouseButton));
	}

	public static boolean getMouseDown(int mouseButton) {
		return (downMouse.contains(mouseButton));
	}

	public static boolean getMouseUp(int mouseButton) {
		return (upMouse.contains(mouseButton));
	}

	public static Vector2f getMousePosition() {
		return (new Vector2f(Mouse.getX(), Mouse.getY()));
	}

}
