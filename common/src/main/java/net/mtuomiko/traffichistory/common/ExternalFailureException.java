package net.mtuomiko.traffichistory.common;

import java.util.List;

public class ExternalFailureException extends CustomException {

    public ExternalFailureException(String message) {
        super(message);
    }

    public ExternalFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalFailureException(String message, Throwable cause, List<String> errors) {
        super(message, cause, errors);
    }
}
