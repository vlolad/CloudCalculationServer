package com.sergeev.cloudcalculation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    static {
        Locale.setDefault(new Locale("en"));
    }

    //Catch most exceptions
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.error("Get exception: {}", (Object) e.getStackTrace());
        return new ResponseEntity<>("Something goes wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
