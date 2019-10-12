package com.base.engine.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.base.engine.handlers.logging.LogManager;
import com.base.engine.handlers.logging.Logger;
import com.base.engine.math.Vector2f;

public class Window {

	private static Logger logger = LogManager.getLogger(Window.class.getName());

	public static void createWindow(int width, int height, String title, boolean fullscreen, boolean vSync) {
		Display.setTitle(title);
		try {

//			Display.setDisplayMode(getFullscreen());
//			Display.setFullscreen(true);

			setDisplayMode(width, height, fullscreen);
			Display.setVSyncEnabled(vSync);
			Display.create();
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException e) {
			// e.printStackTrace();
			logger.error("Unable to create window.", e);
		}
	}

	public static void render(double fps) {
		Display.update();
		Display.sync((int) fps);
	}

	public static void dispose() {
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
	}

	public static DisplayMode[] getAvailableDisplayModes() {

		DisplayMode[] modes = null;
		try {
			modes = Display.getAvailableDisplayModes();
		} catch (LWJGLException e) {
			// e.printStackTrace();
			logger.error("Unable to get display modes.", e);
		}

		for (int i = 0; i < modes.length; i++) {
			DisplayMode current = modes[i];
			logger.debug(current.getWidth() + "x" + current.getHeight() + "x" + current.getBitsPerPixel() + " "
					+ current.getFrequency() + "Hz");
		}

		return modes;
	}

	/**
	 * Set the display mode to be used
	 * 
	 * @param width      The width of the display required
	 * @param height     The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public static void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height)
				&& (Display.isFullscreen() == fullscreen)) {
			return;
		}

		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				DisplayMode[] modes = getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++) {
					DisplayMode current = modes[i];

					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null)
									|| (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for Bits Per Pixel and frequency against the
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
								&& (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							logger.info("Setting Current Display Mode To: " + current.getWidth() + "x"
									+ current.getHeight() + current.getFrequency() + "Hz");
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				logger.error("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e) {
			// System.out.println("Unable to setup mode " + width + "x" + height + "
			// fullscreen=" + fullscreen + e);
			logger.error("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen, e);
		}
	}

	public static Vector2f getCenter() {

		return (new Vector2f(getWidth() / 2, getHeight() / 2));

	}

	public static boolean isCloseRequested() {
		return Display.isCloseRequested();
	}

	public static int getWidth() {
		return Display.getDisplayMode().getWidth();
	}

	public static int getHeight() {
		return Display.getDisplayMode().getHeight();
	}

	public static String getTitle() {
		return Display.getTitle();
	}
}
