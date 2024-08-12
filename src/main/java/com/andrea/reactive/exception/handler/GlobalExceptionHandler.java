package com.andrea.reactive.exception.handler;

import com.andrea.reactive.exception.DatabaseOperationException;
import com.andrea.reactive.exception.ExternalAPIException;
import com.andrea.reactive.exception.SatelliteNotFoundException;
import com.andrea.reactive.exception.ValidationException;
import com.andrea.reactive.exception.dto.GlobalError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SatelliteNotFoundException.class)
    public ResponseEntity<GlobalError> satelliteNotFoundException(SatelliteNotFoundException e) {
        GlobalError error = createError(e.getMessage(), HttpStatus.NOT_FOUND);
        logError(SatelliteNotFoundException.class.getSimpleName(), error, e);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<GlobalError> handleValidationException(ValidationException e) {
        GlobalError error = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        logError(ValidationException.class.getSimpleName(), error, e);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalError> handleValidationExceptions(MethodArgumentNotValidException e) {
        GlobalError error = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        logError(MethodArgumentNotValidException.class.getSimpleName(), error, e);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<GlobalError> databaseOperationException(DatabaseOperationException e) {
        GlobalError error = createError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        logError(DatabaseOperationException.class.getSimpleName(), error, e);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExternalAPIException.class)
    public ResponseEntity<GlobalError> externalAPIException(ExternalAPIException e) {
        GlobalError error = createError(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        logError(ExternalAPIException.class.getSimpleName(), error, e);
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    private GlobalError createError(String message, HttpStatus status) {
        return GlobalError.builder().message(message).status(status).timestamp(ZonedDateTime.now()).build();
    }

    private void logError(String exceptionName, GlobalError error, Exception ex) {
        Throwable cause = ex.getCause();
        if (cause != null) {
            log.error("Exception: {}\nTimestamp: {}\nStatus: {}\nMessage: {}\nCause: {}\nStack Trace: {}",
                    exceptionName, error.getTimestamp(), error.getStatus(), error.getMessage(), cause, ex.getStackTrace(), ex);
        } else {
            log.error("Exception: {}\nTimestamp: {}\nStatus: {}\nMessage: {}\nStack Trace: {}",
                    exceptionName, error.getTimestamp(), error.getStatus(), error.getMessage(), ex.getStackTrace(), ex);
        }
    }
}
