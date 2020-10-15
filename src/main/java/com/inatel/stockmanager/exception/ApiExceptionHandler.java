package com.inatel.stockmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {

        HttpStatus badRequest;

        switch (e.getCause().getMessage()) {
            case "200": badRequest = HttpStatus.OK; break;
            case "400": badRequest = HttpStatus.BAD_REQUEST; break;
            case "404": badRequest = HttpStatus.NOT_FOUND; break;
            case "405": badRequest = HttpStatus.METHOD_NOT_ALLOWED; break;
            default: badRequest = HttpStatus.INTERNAL_SERVER_ERROR; break;
        }

        ApiException apiException = new ApiException(
                ZonedDateTime.now(ZoneId.of("Z")),
                e.getCause().getMessage(),
                e.getMessage()
        );

        return new ResponseEntity<>(apiException, badRequest);

    }
}
