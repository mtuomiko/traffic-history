package net.mtuomiko.traffichistory.common;

import java.util.List;

public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message, Throwable cause, List<String> errors) {
        super(message, cause, errors);
    }
}
