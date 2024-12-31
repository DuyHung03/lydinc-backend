package com.duyhung.lydinc_backend.exception;


public class FileInDriveNotFoundException extends RuntimeException {
    public FileInDriveNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
