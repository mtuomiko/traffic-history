package net.mtuomiko.traffichistory.common;

import java.util.List;

public class ValidationException extends CustomException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message, Throwable cause, List<String> errors) {
        super(message, cause, errors);
    }
}
