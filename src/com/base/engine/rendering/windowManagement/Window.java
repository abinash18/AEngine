package com.base.engine.rendering.windowManagement;

@Deprecated
public class Window {

//	private static Logger logger = LogManager.getLogger(Window.class.getName());

//	@Deprecated
//	public static void createWindow(int width, int height, String title, boolean fullscreen, boolean vSync) {
//		Display.setTitle(title);
//		try {
//
////			Display.setDisplayMode(getFullscreen());
////			Display.setFullscreen(true);
//
//			setDisplayMode(width, height, fullscreen);
//			Display.setVSyncEnabled(vSync);
//			Display.create();
//			Keyboard.create();
//			Mouse.create();
//		} catch (LWJGLException e) {
//			// e.printStackTrace();
//			logger.error("Unable to create window.", e);
//		}
//	}
//
//	@Deprecated
//	public static void render(double fps) {
//		Display.update();
//		Display.sync((int) fps);
//	}
//
//	@Deprecated
//	public static void dispose() {
//		Display.destroy();
//		Keyboard.destroy();
//		Mouse.destroy();
//	}
//
//	@Deprecated
//	public static DisplayMode[] getAvailableDisplayModes() {
//
//		DisplayMode[] modes = null;
//		try {
//			modes = Display.getAvailableDisplayModes();
//		} catch (LWJGLException e) {
//			// e.printStackTrace();
//			logger.error("Unable to get display modes.", e);
//		}
//
//		for (int i = 0; i < modes.length; i++) {
//			DisplayMode current = modes[i];
//			logger.debug(current.getWidth() + "x" + current.getHeight() + "x" + current.getBitsPerPixel() + " "
//					+ current.getFrequency() + "Hz");
//		}
//
//		return modes;
//	}
//
//	/**
//	 * Set the display mode to be used
//	 * 
//	 * @param width      The width of the display required
//	 * @param height     The height of the display required
//	 * @param fullscreen True if we want fullscreen mode
//	 */
//	@Deprecated
//	public static void setDisplayMode(int width, int height, boolean fullscreen) {
//
//		// return if requested DisplayMode is already set
//		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height)
//				&& (Display.isFullscreen() == fullscreen)) {
//			return;
//		}
//
//		try {
//			DisplayMode targetDisplayMode = null;
//
//			if (fullscreen) {
//				DisplayMode[] modes = getAvailableDisplayModes();
//				int freq = 0;
//
//				for (int i = 0; i < modes.length; i++) {
//					DisplayMode current = modes[i];
//
//					if ((current.getWidth() == width) && (current.getHeight() == height)) {
//						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
//							if ((targetDisplayMode == null)
//									|| (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
//								targetDisplayMode = current;
//								freq = targetDisplayMode.getFrequency();
//							}
//						}
//
//						// if we've found a match for Bits Per Pixel and frequency against the
//						// original display mode then it's probably best to go for this one
//						// since it's most likely compatible with the monitor
//						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
//								&& (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
//							targetDisplayMode = current;
//							logger.info("Setting Current Display Mode To: " + current.getWidth() + "x"
//									+ current.getHeight() + current.getFrequency() + "Hz");
//							break;
//						}
//					}
//				}
//			} else {
//				targetDisplayMode = new DisplayMode(width, height);
//			}
//
//			if (targetDisplayMode == null) {
//				logger.error("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
//				return;
//			}
//
//			Display.setDisplayMode(targetDisplayMode);
//			Display.setFullscreen(fullscreen);
//
//		} catch (LWJGLException e) {
//			// System.out.println("Unable to setup mode " + width + "x" + height + "
//			// fullscreen=" + fullscreen + e);
//			logger.error("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen, e);
//		}
//	}
//
//	@Deprecated
//	public static Vector2f getCenter() {
//
//		return (new Vector2f(getWidth() / 2, getHeight() / 2));
//
//	}
//
//	@Deprecated
//	public static boolean isCloseRequested() {
//		return Display.isCloseRequested();
//	}
//
//	@Deprecated
//	public static int getWidth() {
//		return Display.getDisplayMode().getWidth();
//	}
//
//	@Deprecated
//	public static int getHeight() {
//		return Display.getDisplayMode().getHeight();
//	}
//
//	@Deprecated
//	public static String getTitle() {
//		return Display.getTitle();
//	}
}
