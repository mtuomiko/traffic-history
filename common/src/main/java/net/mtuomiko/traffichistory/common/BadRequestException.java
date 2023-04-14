package net.mtuomiko.traffichistory.common;

import java.util.List;

public class BadRequestException extends CustomException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(String message, Throwable cause, List<String> errors) {
        super(message, cause, errors);
    }
}
