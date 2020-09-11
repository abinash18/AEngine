package net.abi.abisEngine.util.exceptions;

import net.abi.abisEngine.rendering.window.GLFWWindow;

public class AEGLFWWindowInitializationException extends AEException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String windowProps;

	public AEGLFWWindowInitializationException(String cause) {
		super(cause);
		windowProps = "N/A";
	}

	public AEGLFWWindowInitializationException(String cause, GLFWWindow wnd) {
		super(cause);
		windowProps = wnd.toString();
	}

}
