package com.duyhung.lydinc_backend.exception;

import com.duyhung.lydinc_backend.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    //authenticate validation
    @ExceptionHandler(AuthValidationException.class)
    public ResponseEntity<?> handleAuthValidationException(AuthValidationException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    // token validation
    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<?> handleGenericException(JwtValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(401).body(errorResponse);
    }


    // drive file not found exception
    @ExceptionHandler(FileInDriveNotFoundException.class)
    public ResponseEntity<?> handleFNFException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
