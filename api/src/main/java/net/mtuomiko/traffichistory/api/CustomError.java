package net.mtuomiko.traffichistory.api;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CustomError {
    private String message;

    public CustomError(String message) {
        this.message = message;
    }

    public CustomError() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
