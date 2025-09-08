package com.ikea.warehouse_command_api.exception;

public class FileProcessingException extends RuntimeException {
    private final String error;

    public FileProcessingException(String error, String message) {
        super(message);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
