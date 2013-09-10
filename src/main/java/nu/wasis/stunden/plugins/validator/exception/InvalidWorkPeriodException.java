package nu.wasis.stunden.plugins.validator.exception;

public class InvalidWorkPeriodException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidWorkPeriodException() {
	}

	public InvalidWorkPeriodException(String arg0) {
		super(arg0);
	}

	public InvalidWorkPeriodException(Throwable arg0) {
		super(arg0);
	}

	public InvalidWorkPeriodException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidWorkPeriodException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
