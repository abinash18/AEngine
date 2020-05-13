package net.abi.abisEngine.util.exceptions;

import java.io.IOException;

public class AEIOException extends AERuntimeException {

	public AEIOException(IOException e) {
		super(e);
	}

}
