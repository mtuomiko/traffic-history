package net.mtuomiko.traffichistory.common;

import java.util.Collections;
import java.util.List;

public class CustomException extends RuntimeException {

    private List<String> errors = Collections.emptyList();

    public CustomException(String message) {
        super(message, null);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(String message, Throwable cause, List<String> errors) {
        super(message, cause);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
