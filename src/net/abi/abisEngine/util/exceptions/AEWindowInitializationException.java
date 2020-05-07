package net.abi.abisEngine.util.exceptions;

import net.abi.abisEngine.rendering.windowManagement.GLFWWindow;

public class AEWindowInitializationException extends AEException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String windowProps;

	public AEWindowInitializationException(String cause) {
		super(cause);
		windowProps = "N/A";
	}

	public AEWindowInitializationException(String cause, GLFWWindow wnd) {
		super(cause);
		windowProps = wnd.toString();
	}

}
