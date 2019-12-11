package net.abi.abisEngine.util;

public class AERuntimeException extends RuntimeException {
	private static final long serialVersionUID = 666666555666666L;

	public AERuntimeException(Throwable cause) {
		super(cause);
	}

	public AERuntimeException(String cause, Throwable t) {
		super(cause, t);
	}

	public AERuntimeException(String cause) {
		super(cause);
	}

	public AERuntimeException(String cause, Exception e) {
		super(cause, e);
	}

}
