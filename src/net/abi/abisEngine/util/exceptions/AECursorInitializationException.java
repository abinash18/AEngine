package net.abi.abisEngine.util.exceptions;

import net.abi.abisEngine.rendering.windowManagement.GLFWWindow.StaticCursor;

public class AECursorInitializationException extends AEException {

	public AECursorInitializationException(String cause) {
		super(cause);
	}
	
	public AECursorInitializationException(String cause, StaticCursor c) {
		super(cause + " Name:" + c.getID());
	}

}
