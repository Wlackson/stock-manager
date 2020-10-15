package com.inatel.stockmanager.response;

import com.inatel.stockmanager.exception.ApiException;
import io.swagger.annotations.ApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ApiModel(value = "Provides standardized response object")
public class ResponseHandler {

    public ResponseEntity standardizedResponse(String message, String httpStatus) {

        HttpStatus response;

        switch (httpStatus) {
            case "200":
                response = HttpStatus.OK;
                break;
            case "201":
                response = HttpStatus.CREATED;
                break;
            case "202":
                response = HttpStatus.ACCEPTED;
                break;
            case "203":
                response = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
                break;
            case "204":
                response = HttpStatus.NO_CONTENT;
                break;
            case "205":
                response = HttpStatus.RESET_CONTENT;
                break;
            case "206":
                response = HttpStatus.PARTIAL_CONTENT;
                break;
            default:
                response = HttpStatus.I_AM_A_TEAPOT;
                break;

        }

        ApiException apiException = new ApiException(
                ZonedDateTime.now(ZoneId.of("Z")),
                httpStatus,
                message
        );

        return new ResponseEntity<>(apiException, response);

    }

}
