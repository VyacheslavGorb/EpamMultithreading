package edu.gorb.seaport.exception;

public class SeaPortException extends Exception {
    public SeaPortException() {
    }

    public SeaPortException(String message) {
        super(message);
    }

    public SeaPortException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeaPortException(Throwable cause) {
        super(cause);
    }
}
