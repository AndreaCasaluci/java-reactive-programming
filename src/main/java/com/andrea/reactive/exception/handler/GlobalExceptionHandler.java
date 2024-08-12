package com.andrea.reactive.exception.handler;

import com.andrea.reactive.exception.TestException;
import com.andrea.reactive.exception.dto.GlobalError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TestException.class)
    public ResponseEntity<GlobalError> testException(TestException e) {
        GlobalError error = createError(e.getMessage(), HttpStatus.I_AM_A_TEAPOT);
        logError(TestException.class.getSimpleName(), error, e);
        return new ResponseEntity<>(error, HttpStatus.I_AM_A_TEAPOT);
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
