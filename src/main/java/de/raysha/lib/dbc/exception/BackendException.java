package de.raysha.lib.dbc.exception;

public class BackendException extends RuntimeException {

	public BackendException() {
		super();
	}

	public BackendException(String message) {
		super(message);
	}

	public BackendException(Throwable cause) {
		super(cause);
	}

	public BackendException(String message, Throwable cause) {
		super(message, cause);
	}

}
