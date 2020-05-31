package net.abi.abisEngine.util.exceptions;

public class AEShaderCompilerRuntimeException extends AERuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AEShaderCompilerRuntimeException(String cause, Throwable t) {
		super(cause, t);
	}

	public AEShaderCompilerRuntimeException(String cause) {
		super(cause);
	}

}
